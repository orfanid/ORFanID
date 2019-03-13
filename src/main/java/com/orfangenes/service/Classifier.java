package com.orfangenes.service;

import com.orfangenes.model.BlastResult;
import com.orfangenes.model.Gene;
import com.orfangenes.model.taxonomy.RankedLineage;
import com.orfangenes.model.taxonomy.TaxNode;
import com.orfangenes.util.Constants;
import com.orfangenes.util.FileHandler;
import com.orfangenes.util.ResultsPrinter;
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

    public Map<String, String> getGeneClassification(String outputdir, Map<String, Gene> genes) {
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
                                DOMAIN_RESTRICTED_GENE); // 8
        Map<String, String> classification = new HashMap<>();
        try {
            ResultsPrinter.displayTree(organismTaxID, inputRankedLineage, taxonomyTreeForGenes);
            // travel though each gene
            for (Map.Entry<String, List<List<String>>> entry : taxonomyTreeForGenes.entrySet()) {
                String GeneId = entry.getKey();
                List<List<String>> blastResultsRankedLineages = entry.getValue();
                // travel though each lineage:
                // TaxId(0), Scientific name of the taxonomy(1), species(2), Genus(3), Family(4), Order(5), Class(6),
                // phylum(7), Kingdom(8), Super kingdom or Domain(9)
                // start from Super kingdom(domain) and travel towards species
                for (int columnNo = 9; columnNo > 0; columnNo--) {
                    if (columnNo == 2) { continue;}// read the scientific species name instead species-level
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
                    } else if (columnNo == 1) { // no homologous sibling + reached to the species column
                        classification.put(GeneId, classificationLevels.get(columnNo));
                        break;
                    }// otherwise let loop continue
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        // todo: bad practise, functions should do multiple things. try to modularise
        ResultsGenerator.generateORFanGeneSummary(classification, outputdir, genes);
        ResultsGenerator.generateBlastTree(this.taxonomyTreeForGenes, outputdir);
        return classification;
    }
}
