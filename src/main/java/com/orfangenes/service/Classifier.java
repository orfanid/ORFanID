package com.orfangenes.service;

import com.orfangenes.model.BlastResult;
import com.orfangenes.model.taxonomy.RankedLineage;
import com.orfangenes.model.taxonomy.TaxNode;
import com.orfangenes.util.Constants;
import com.orfangenes.util.FileHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static com.orfangenes.util.Constants.*;

@Slf4j
public class Classifier {

    private TaxTree tree;
    private int organismTaxID;

    Map<String, List<List<String>>> taxonomyTreeForGenes;
    List<String> inputRankedLineage;

    public Classifier(TaxTree tree, int organismTaxID, List<BlastResult> blastResults) {
        this.tree = tree;
        this.organismTaxID = organismTaxID;

        // Mapping between Gene ID and Lineage
        this.taxonomyTreeForGenes = tree.buildRankedLineageList(blastResults);
        // Lineage for input organism
        this.inputRankedLineage = tree.getInputRankedLineage();
    }

    public Map<String, String> getGeneClassification(String outputdir) {
        Set<String> blastResultsCommonIds;
        List<String> classificationLevels =
                Arrays.asList(STRICT_ORFAN, // 0
                                ORFAN_GENE, // 1
                                GENUS_RESTRICTED_GENE, // 2
                                FAMILY_RESTRICTED_GENE, // 3
                                ORDER_RESTRICTED_GENE, // 4
                                CLASS_RESTRICTED_GENE, // 5
                                PHYLUM_RESTRICTED_GENE, // 6
                                KINGDOM_RESTRICTED_GENE, // 7
                                DOMAIN_RESTRICTED_GENE, // 8
                                MULTI_DOMAIN_GENE); // 9
        Map<String, String> classification = new HashMap<>();
        displayTree();
        try {
            // travel though each gene
            for (Map.Entry<String, List<List<String>>> entry : taxonomyTreeForGenes.entrySet()) {
                String GeneId = entry.getKey();
                List<List<String>> blastResultsRankedLineages = entry.getValue();
                // travel though each lineage:
                // TaxId(0),Subspecies(1), Species(2), Genus(3), Family(4), Order(5), Class(6), phylum(7), Kingdom(8), Super kingdom(9)
                // start from Super kingdom and travel towards Subspecies
                for (int columnNo = 9; columnNo > 0; columnNo--) {
                    blastResultsCommonIds = new HashSet<>();
                    // travel though each blast hits
                    for (List<String> rankedLineage : blastResultsRankedLineages) {
                        // skip any missing values, the do not contribute for the evidence based decision.
                        if(!rankedLineage.get(columnNo).equals("") || rankedLineage.get(columnNo).equals(NOT_AVAILABLE)){
                            // get distinct taxonomy Ids
                            blastResultsCommonIds.add(rankedLineage.get(columnNo));
                        }
                    }
                    // add input organism taxonomy level name
                    blastResultsCommonIds.add(inputRankedLineage.get(columnNo));

                    // found homologous sibling(s)
                    if (blastResultsCommonIds.size() > 1 ) {
                        // found a classification
                        classification.put(GeneId, classificationLevels.get(columnNo-1));
                        break;
                    } else if (columnNo == 2) { // no homologous sibling + reached to the species column
                        classification.put(GeneId, classificationLevels.get(columnNo - 1));
                        break;
                    }// otherwise let loop continue
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        generateORFanGeneSummary(classification, outputdir);
        return classification;
    }

    private void generateORFanGeneSummary(Map<String, String> classification, String outputdir) {
        Map<String, Integer> orfanGeneCount = new LinkedHashMap<>();
        orfanGeneCount.put(Constants.MULTI_DOMAIN_GENE, 0);
        orfanGeneCount.put(Constants.DOMAIN_RESTRICTED_GENE, 0);
        orfanGeneCount.put(Constants.KINGDOM_RESTRICTED_GENE, 0);
        orfanGeneCount.put(Constants.PHYLUM_RESTRICTED_GENE, 0);
        orfanGeneCount.put(Constants.CLASS_RESTRICTED_GENE, 0);
        orfanGeneCount.put(Constants.ORDER_RESTRICTED_GENE, 0);
        orfanGeneCount.put(Constants.FAMILY_RESTRICTED_GENE, 0);
        orfanGeneCount.put(Constants.GENUS_RESTRICTED_GENE, 0);
        orfanGeneCount.put(Constants.ORFAN_GENE, 0);
        orfanGeneCount.put(Constants.STRICT_ORFAN, 0);

        for (Map.Entry<String, String> entry : classification.entrySet()) {
            String classificationLevel = entry.getValue();
            int count = orfanGeneCount.get(classificationLevel);
            count++;
            orfanGeneCount.put(classificationLevel, count);
        }

        // Generating ORFan Genes summary data to be shown in the table
        JSONArray orfanGenesSummary = new JSONArray();
        for (Map.Entry<String, Integer> entry : orfanGeneCount.entrySet()) {
            JSONObject summaryObject = new JSONObject();
            summaryObject.put("type", entry.getKey());
            summaryObject.put("count", entry.getValue());
            orfanGenesSummary.add(summaryObject);
        }
        FileHandler.saveOutputFiles(orfanGenesSummary, outputdir + "/" + FILE_OUTPUT_ORFAN_GENES_SUMMARY);

        // Generating ORFan Genes Summary Chart data
        JSONObject chartJSON = new JSONObject();
        JSONArray x = new JSONArray();
        JSONArray y = new JSONArray();
        for (Map.Entry<String, Integer> entry : orfanGeneCount.entrySet()) {
            x.add(entry.getKey());
            y.add(entry.getValue());
        }
        chartJSON.put("x", x);
        chartJSON.put("y", y);
        FileHandler.saveOutputFiles(chartJSON, outputdir + "/" + FILE_OUTPUT_ORFAN_GENES_SUMMARY_CHART);
    }

    public void displayTree() {

        System.out.println("\n\nInput Taxonomy: " + organismTaxID + "\n=================\n");
        for (String taxNode : inputRankedLineage) {
            System.out.print(formatString(taxNode));
        }
        System.out.println("\n");

        // travel though each gene
        for (Map.Entry<String, List<List<String>>> entry : taxonomyTreeForGenes.entrySet()) {
            String GeneId = entry.getKey();
            System.out.println("\nGene Id: " + GeneId + "\n=================\n");
            for (List<String> rankedLineage : entry.getValue()) {
                for (String taxNode : rankedLineage) {
                    System.out.print(formatString(taxNode));
                }
                System.out.println();
            }
        }
    }

    public String formatString(String name) {
        String node;
        if (name.equals(NOT_AVAILABLE) || name.equals("")) {
            node = ANSI_RED + NOT_AVAILABLE + "\t" + ANSI_RESET + "|";
        } else {
            node = name + "\t|";
        }
        return node;
    }
}
