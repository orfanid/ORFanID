package com.orfangenes.service;

import com.orfangenes.model.Gene;
import com.orfangenes.model.taxonomy.TaxNode;
import com.orfangenes.util.Constants;
import com.orfangenes.util.FileHandler;
import com.sun.tools.javah.Gen;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static com.orfangenes.util.Constants.*;

public class ResultsGenerator {
    private static List<String> ranks =
            Arrays.asList(SPECIES,
                    GENUS,
                    FAMILY,
                    ORDER,
                    CLASS,
                    PHYLUM,
                    KINGDOM,
                    SUPERKINGDOM);

    public static void generateORFanGeneSummary(Map<String, String> classification, String outputdir, Map<String, Gene> genes) {
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

        JSONArray geneData = new JSONArray();
        for (Map.Entry<String, String> entry : classification.entrySet()) {
            String classificationLevel = entry.getValue();
            int count = orfanGeneCount.get(classificationLevel);
            count++;
            orfanGeneCount.put(classificationLevel, count);

            // Generating Gene data
            JSONObject orfanJSON = new JSONObject();
            orfanJSON.put("geneid", entry.getKey());
            orfanJSON.put("orfanLevel", classificationLevel);
            Gene gene =genes.get(entry.getKey());
            orfanJSON.put("description", gene.getDescription());
            geneData.add(orfanJSON);
        }
        FileHandler.saveOutputFiles(geneData, outputdir + "/" + FILE_OUTPUT_ORFAN_GENES);

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

    public static void generateBlastTree(Map<String, List<List<String>>> taxonomyTreeForGenes, String outputdir) {
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
                String speciesName = lineage.get(1);
                if (lineage.size() == 10 && species.add(speciesName)) {
                    lineage.remove(2); // Removing taxonomy ID
                    lineage.remove(0); // Removing common name
                    uniqueLineages.add(lineage);

                    String superkingdomName = lineage.get(7);
                    superkingdoms.add(superkingdomName);
                }
            }

            if (superkingdoms.size() == 1) {
                TaxNode superkingdom = new TaxNode();
                superkingdom.setName(superkingdoms.iterator().next());
                superkingdom.setNRank(ranks.get(7));
                superkingdom.setChildren(getChildren(uniqueLineages, 7, superkingdom.getName()));
                superkingdom.setNodeCount(1);

                JSONObject jsonTree = createJsonNode(superkingdom);
                tree.put("tree", jsonTree);
                trees.add(tree);
            } else {
                // TODO: Discuss approach to developing BLAST Tree
            }
        }
        FileHandler.saveOutputFiles(trees, outputdir + "/" + FILE_OUTPUT_BLAST_RESULTS);
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