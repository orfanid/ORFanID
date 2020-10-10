package com.orfangenes.app.service;

import com.orfangenes.app.model.taxonomy.TaxNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static com.orfangenes.app.util.Constants.*;

public class ResultsProcessingService {
    private static List<String> ranks =
            Arrays.asList(SPECIES,
                    GENUS,
                    FAMILY,
                    ORDER,
                    CLASS,
                    PHYLUM,
                    KINGDOM,
                    SUPERKINGDOM);

    public static JSONArray generateBlastTree(Map<String, List<List<String>>> taxonomyTreeForGenes) {
        JSONArray trees = new JSONArray();
        for (Map.Entry<String, List<List<String>>> taxTree: taxonomyTreeForGenes.entrySet()) {
            JSONObject tree = new JSONObject();

            String geneId = taxTree.getKey();
            tree.put("id", geneId);

            // Removing duplicate lineages
            List<List<String>> lineages = taxTree.getValue();
            Set<String> species = new HashSet<>();
            Set<String> superkingdoms = new HashSet<>();
            List<List<String>> uniqueLineages = new ArrayList<>();
            for (List<String> lineage : lineages) {
                if (lineage != null) {
                    String speciesName = lineage.get(1);
                    if (lineage.size() == 10 && species.add(speciesName)) {
                        lineage.remove(2); // Removing taxonomy ID
                        lineage.remove(0); // Removing common name
                        uniqueLineages.add(lineage);

                        String superkingdomName = lineage.get(7);
                        superkingdoms.add(superkingdomName);
                    }
                }
            }

            // All life
            TaxNode luca = new TaxNode();
            luca.setName("LUCA");
            luca.setNRank("root");
            luca.setNodeCount(1);

            Set<TaxNode> superkingdomNodes = new HashSet<>();
            if (superkingdoms.size() == 1) {
                TaxNode superkingdom = new TaxNode();
                superkingdom.setName(superkingdoms.iterator().next());
                superkingdom.setNRank(ranks.get(7));
                superkingdom.setChildren(getChildren(uniqueLineages, 7, superkingdom.getName()));
                superkingdom.setNodeCount(1);

                superkingdomNodes.add(superkingdom);
            } else {
                for (String superkingdomName : superkingdoms) {
                    TaxNode superkingdom = new TaxNode();
                    superkingdom.setName(superkingdomName);
                    superkingdom.setChildren(getChildren(uniqueLineages, 7, superkingdom.getName()));
                    superkingdom.setNRank(ranks.get(7));
                    superkingdom.setNodeCount(1);
                    superkingdomNodes.add(superkingdom);
                }
            }
            luca.setChildren(superkingdomNodes);
            JSONObject jsonTree = createJsonNode(luca);
            tree.put("tree", jsonTree);
            trees.add(tree);
        }
//        FileHandler.saveOutputFiles(trees, outputdir +File.separator + FILE_OUTPUT_BLAST_RESULTS);
        return trees;
    }

    private static Set<TaxNode> getChildren (List<List<String>> uniqueLineages, int lineageLevel, String parentName) {
        Set<TaxNode> children = new HashSet<>();
        Set<String> childrenNames = new HashSet<>();
        Map<String, Integer> duplicateChildrenCount = new HashMap<>();

        for (List<String> lineage : uniqueLineages) {
            String taxNameAtLevel = lineage.get(lineageLevel);
            if (taxNameAtLevel.equals(parentName) && lineageLevel > 0) {
                String childName = lineage.get(lineageLevel - 1);
                if (childrenNames.add(childName)) { // Avoid duplicates
                    TaxNode child = new TaxNode();
                    child.setName(childName);
                    child.setNRank(ranks.get(lineageLevel - 1));
                    child.setChildren(getChildren(uniqueLineages, lineageLevel - 1, child.getName()));
                    children.add(child);

                    duplicateChildrenCount.put(childName, 1);
                } else {
                    duplicateChildrenCount.put(childName, (duplicateChildrenCount.get(childName) + 1));
                }
            }
        }

        // Setting node count for children
        for (TaxNode node : children) {
            node.setNodeCount(duplicateChildrenCount.get(node.getName()));
        }
        return children;
    }

    private static JSONObject createJsonNode (TaxNode node) {
        JSONObject jsonNode = new JSONObject();
        jsonNode.put("name", String.format("%s(%d)", node.getName(), node.getNodeCount()));

        if (node.getChildren().size() > 0) {
            JSONArray children = new JSONArray();
            for (TaxNode child : node.getChildren()) {
                children.add(createJsonNode(child));
            }
            jsonNode.put("children", children);
        }
        return jsonNode;
    }
}