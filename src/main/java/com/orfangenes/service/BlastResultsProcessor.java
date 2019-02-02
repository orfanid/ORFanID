package com.orfangenes.service;

import com.orfangenes.model.BlastResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.orfangenes.util.Constants.BLAST_RESULTS_FILE;

/**
 * This class process the blast results to identify Orphan Genes
 */
@Slf4j
@Getter
public class BlastResultsProcessor {

    private List<BlastResult> blastResults;

    public BlastResultsProcessor(String outputDir) {
        String blastResultsFileName = outputDir + "/" + BLAST_RESULTS_FILE;
        List<BlastResult> blastResults = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(blastResultsFileName));
            while (scanner.hasNextLine()) {
                BlastResult result = new BlastResult(scanner.nextLine());
                blastResults.add(result);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            log.error("Blast output file not found in {} directory", blastResultsFileName);
        }
        this.blastResults = blastResults;
    }

//    public JSONArray generateBlastResultsTrees(TaxTree taxTree, List<Gene> genes) {
//        JSONArray blastTrees = new JSONArray();
//        for (Gene gene: genes) {
//            JSONObject blastResult = new JSONObject();
//            blastResult.put("id", gene.getGeneID());
//            blastResult.put("description", gene.getDescription());
//            // Getting tax IDs for the gene
//            Set<Integer> taxIDs = new LinkedHashSet<>();
//            for (BlastResult result : blastResults) {
//                if (result.getQueryid().equals(gene.getGeneID())) {
//                    taxIDs.add(result.getStaxid());
//                }
//            }
//
//            // Getting the hierarchy for each taxonomy
//            List<Map<String, Integer>> hierarchies = new ArrayList<>();
//            for (int taxID: taxIDs) {
//                Map<String, Integer> taxHierarchy = taxTree.getHeirarchyFromNode(taxID);
//                hierarchies.add(taxHierarchy);
//            }
//
//            if (hierarchies.size() > 0) {
//                TaxTree tree = SerializationUtils.clone(taxTree);
//                for (Map<String, Integer> hierarchy: hierarchies) {
//                    if (hierarchy != null) {
//                        createTree(tree, hierarchy, Constants.SUPERKINGDOM);
//                    }
//                }
//
//                Set<Integer> taxonomiesAtSuperkingdom = new HashSet<>();
//                for (Map<String, Integer> currentTaxHierarchy: hierarchies) {
//                    try {
//                        int currentRankId = currentTaxHierarchy.get(Constants.SUPERKINGDOM);
//                        taxonomiesAtSuperkingdom.add(currentRankId);
//                    } catch (NullPointerException e) {
//                        // Do nothing
//                    }
//                }
//                if (taxonomiesAtSuperkingdom.size() > 1) {
//                    // TODO: Add root to tree
//                } else {
//                    int i = 0;
//                    boolean isValid = false;
//
//                    while (!isValid) {
//                        try {
//                            Map<String, Integer> hierarchy = hierarchies.get(i);
//                            int superkingdomTaxID = hierarchy.get(Constants.SUPERKINGDOM);
//                            TaxNode superkingdomNode = tree.getNode(superkingdomTaxID);
//                            JSONObject jsonTree = createJSONNode(superkingdomNode);
//                            blastResult.put("tree", jsonTree);
//                            blastTrees.add(blastResult);
//                            isValid = true;
//                        } catch (NullPointerException e) {
//                            i++;
//                        } catch (IndexOutOfBoundsException e) {
//                            //Do nothing. This is in the case of an invalid tree is created.
//                        }
//                    }
//                }
//            }
//        }
//        return blastTrees;
//    }

//    private JSONObject createJSONNode (TaxNode node) {
//        JSONObject jsonNode = new JSONObject();
//        jsonNode.put("name", node.getName());
//
//        if (node.getChildren().size() > 0) {
//            JSONArray children = new JSONArray();
//            for (TaxNode child: node.getChildren()) {
//                children.add(createJSONNode(child));
//            }
//            jsonNode.put("children", children);
//        }
//        return jsonNode;
//    }

//    private void createTree (TaxTree tree, Map<String, Integer> hierarchy, String currentRank) {
//        boolean isComplete = false;
//        TaxNode currentRankNode = null;
//        String nextRank = null;
//
//        if (hierarchy.size() == 0) {
//            return;
//        }
//
//        while (!isComplete) {
//            try {
//                int currentRankTaxID = hierarchy.get(currentRank);
//                currentRankNode = tree.getNode(currentRankTaxID);
//                nextRank = getNextRank(currentRank);
//                isComplete = true;
//            } catch (NullPointerException e) {
//                currentRank = getNextRank(currentRank);
//            }
//        }
//        isComplete = false;
//
//        while (nextRank != null && !isComplete) {
//            try {
//                //TODO: Uncomment this line. I commented this only for the testing purpose
////                currentRankNode.addChild(tree.getNode(hierarchy.get(nextRank)));
//                isComplete = true;
//                createTree(tree, hierarchy, nextRank);
//            } catch (NullPointerException e) {
//                nextRank = getNextRank(nextRank);
//            }
//        }
//    }
//
//    private String getNextRank(String currentRank) {
//        String nextRank = null;
//        switch (currentRank) {
//            case Constants.SUPERKINGDOM: nextRank = Constants.KINGDOM; break;
//            case Constants.KINGDOM: nextRank = Constants.PHYLUM; break;
//            case Constants.PHYLUM: nextRank = Constants.CLASS; break;
//            case Constants.CLASS:  nextRank = Constants.ORDER; break;
//            case Constants.ORDER: nextRank = Constants.FAMILY; break;
//            case Constants.FAMILY: nextRank = Constants.GENUS; break;
//            case Constants.GENUS: nextRank = Constants.SPECIES; break;
//            case Constants.SPECIES: nextRank = null; break;
//        }
//        return nextRank;
//    }
}
