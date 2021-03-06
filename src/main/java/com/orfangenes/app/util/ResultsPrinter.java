package com.orfangenes.app.util;

import com.orfangenes.app.model.Gene;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.orfangenes.app.util.Constants.ANSI_RED;
import static com.orfangenes.app.util.Constants.ANSI_RESET;
import static com.orfangenes.app.util.Constants.NOT_AVAILABLE;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
public class ResultsPrinter {

    private static final String LINE_SEPERATOR = "----------------------------------------------------------------------------------------------------------------------------------------";

    public static void displayTree(int organismTaxID, List<String> inputRankedLineage, Map<String, List<List<String>>> taxonomyTreeForGenes) {

        System.out.println("\n\nInput Taxonomy: " + organismTaxID + "\n=========================\n");
        formatHeader();
        for (String taxNode : inputRankedLineage) {
            System.out.print(formatString(taxNode));
        }
        formatFooter();

        System.out.println("\n");
        // travel though each gene
        for (Map.Entry<String, List<List<String>>> entry : taxonomyTreeForGenes.entrySet()) {
            String GeneId = entry.getKey();
            System.out.println("\nGene Id: " + GeneId + "\n=========================\n");
            formatHeader();
            formatLineage(entry.getValue());
            formatFooter();
        }
    }

    private static void formatLineage(List<List<String>> lineageList) {
        // travel though each lineage
        for (List<String> rankedLineage : lineageList) {
            if (rankedLineage != null) {
                for (String taxNode : rankedLineage) {
                    System.out.format("%15s", formatString(taxNode));
                }
                System.out.println();
            }
        }
    }

    private static void formatHeader() {
        List<String> rankedLineageFileColumnNames =
                Arrays.asList("tax_id", "subspecies", "species", "genus", "family",
                        "order", "class", "phylum", "kingdom", "superkingdom");

        System.out.println();
        for (String columnName : rankedLineageFileColumnNames) {
            System.out.format("%15s|", columnName);
        }
        System.out.println("\n" + LINE_SEPERATOR);
    }

    private static void formatFooter(){
        System.out.println(LINE_SEPERATOR);
    }


    private static String formatString(String name) {
        String node;
        if (name.equals(NOT_AVAILABLE) || name.equals("")) {
            node = ANSI_RED + NOT_AVAILABLE + ANSI_RESET + "|";
        } else {
            node = name + "|";
        }
        return node;
    }

    public static void displayFinding(List<Gene> classifiedGenes){
        classifiedGenes.forEach(gene -> {
            System.out.println(gene.getGeneId() + " --> " + gene.getOrfanLevel());
        });
        System.out.println("--------------------------------------------------------- \n");
    }
}
