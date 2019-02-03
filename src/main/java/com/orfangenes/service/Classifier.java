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

    Map<String, List<RankedLineage>> taxonomyTreeForGenes;
    RankedLineage inputRankedLineage;

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
                        ORFAN_GENE, GENUS_RESTRICTED_GENE, FAMILY_RESTRICTED_GENE,
                        ORDER_RESTRICTED_GENE, CLASS_RESTRICTED_GENE, PHYLUM_RESTRICTED_GENE,
                        KINGDOM_RESTRICTED_GENE, DOMAIN_RESTRICTED_GENE, MULTI_DOMAIN_GENE);
        Map<String, String> classification = new HashMap<>();
        displayTree();
        try {
            // travel though each gene
            for (Map.Entry<String, List<RankedLineage>> entry : taxonomyTreeForGenes.entrySet()){
                String GeneId = entry.getKey();
                List<RankedLineage> blastResultsRankedLineages = entry.getValue();
                // travel though each lineage (eg: Species, Genus, Family, Class, Order, Kingdom, Super kingdom), start from Super kingdom
                for (int columnNo = tree.rankedLineageFileColumnNames.size()-2; columnNo > 0; columnNo--){
                    Set<String> blastResultsCommonIds = new HashSet<>();
                    // travel though each blast hits
                    for (RankedLineage rankedLineage : blastResultsRankedLineages) {
                        // get distinct taxonomy Ids
                        blastResultsCommonIds.add(rankedLineage.getLineage().get(columnNo-1).getName());
                    }
                    if(blastResultsCommonIds.size() >= 1 && !inputRankedLineage.getLineage().get(columnNo - 1).getName().equals(blastResultsCommonIds.iterator().next())){
                        classification.put(GeneId, classificationLevels.get(columnNo));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classification;
    }

    public void displayTree(){

        System.out.println("\n\nInput Taxonomy: " + organismTaxID + "\n=================\n");
        System.out.print(organismTaxID + " --> ");
        for (TaxNode taxNode : inputRankedLineage.getLineage()){
            System.out.print(taxNode.toString());
        }
        System.out.println("\n");

        // travel though each gene
        for (Map.Entry<String, List<RankedLineage>> entry : taxonomyTreeForGenes.entrySet()){
            String GeneId = entry.getKey();
            System.out.println("\nGene Id: " + GeneId + "\n=================\n");
            for ( RankedLineage rankedLineage : entry.getValue()){
                System.out.print(rankedLineage.getTaxonomyId() + " --> ");
                for (TaxNode taxNode : rankedLineage.getLineage()){
                    System.out.print(taxNode.toString());
                }
                System.out.println();
            }
        }
    }
}
