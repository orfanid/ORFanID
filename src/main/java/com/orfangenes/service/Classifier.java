package com.orfangenes.service;

import com.orfangenes.model.BlastResult;
import com.orfangenes.model.taxonomy.RankedLineage;
import com.orfangenes.model.taxonomy.TaxNode;
import lombok.extern.slf4j.Slf4j;

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

        // for the blast
        this.taxonomyTreeForGenes = tree.buildRankedLineageList(blastResults);
        // for the input organism
        this.inputRankedLineage = tree.getInputRankedLineage();
    }

    public Map<String, String> getGeneClassification() {
        List<String> classificationLevels =
                Arrays.asList(
                        STRICT_ORFAN, ORFAN_GENE, GENUS_RESTRICTED_GENE, FAMILY_RESTRICTED_GENE,
                        ORDER_RESTRICTED_GENE, CLASS_RESTRICTED_GENE, PHYLUM_RESTRICTED_GENE,
                        KINGDOM_RESTRICTED_GENE, DOMAIN_RESTRICTED_GENE, MULTI_DOMAIN_GENE);
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
                    Set<String> blastResultsCommonIds = new HashSet<>();
                    // travel though each blast hits
                    for (List<String> rankedLineage : blastResultsRankedLineages) {
                        // get distinct taxonomy Ids
                        blastResultsCommonIds.add(rankedLineage.get(columnNo));
                    }
                    if (blastResultsCommonIds.size() >= 1 && !inputRankedLineage.get(columnNo).equals(blastResultsCommonIds.iterator().next())) {
                        classification.put(GeneId, classificationLevels.get(columnNo));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return classification;
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
