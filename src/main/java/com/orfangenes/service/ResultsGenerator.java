//package com.orfangenes.service;
//
//import com.orfangenes.model.Gene;
//import com.orfangenes.model.ORFGene;
//import com.orfangenes.service.TaxTree;
//import com.orfangenes.util.Constants;
//import com.orfangenes.util.FileHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import static com.orfangenes.util.Constants.*;
//
///**
// * @author Suresh Hewapathirana
// */
//@Slf4j
//public class ResultsGenerator {
//
//  public static void generateResult (Map<Gene, String> geneClassification, String out,
//                                     BlastResultsProcessor blastResultsProcessor,
//                                     TaxTree tree, List<Gene> inputGenes) {
//
//    Map<String, Integer> orfanGeneCount = generateORFanGenesFrequency(geneClassification, out);
//    JSONObject chartJSON = generateORFanGenesSummaryData(orfanGeneCount, out);
//
//    FileHandler.saveOutputFiles(chartJSON, out + "/" + FILE_OUTPUT_ORFAN_GENES_SUMMARY_CHART);
//    // Writing blast results to JSON file
//    JSONArray blastTrees = blastResultsProcessor.generateBlastResultsTrees(tree, inputGenes);
//    FileHandler.saveOutputFiles(blastTrees, out + "/" + FILE_OUTPUT_BLAST_RESULTS);
//    log.info("JSON files generated");
//  }
//
//  private static Map<String, Integer>  generateORFanGenesFrequency(Map<Gene, String> geneClassification, String out){
//
//    JSONArray orfanGenes = new JSONArray();
//
//    // ORFan and Native Gene count
//    Map<String, Integer> orfanGeneCount = new LinkedHashMap<>();
//    orfanGeneCount.put(Constants.MULTI_DOMAIN_GENE, 0);
//    orfanGeneCount.put(Constants.DOMAIN_RESTRICTED_GENE, 0);
//    orfanGeneCount.put(Constants.KINGDOM_RESTRICTED_GENE, 0);
//    orfanGeneCount.put(Constants.PHYLUM_RESTRICTED_GENE, 0);
//    orfanGeneCount.put(Constants.CLASS_RESTRICTED_GENE, 0);
//    orfanGeneCount.put(Constants.ORDER_RESTRICTED_GENE, 0);
//    orfanGeneCount.put(Constants.FAMILY_RESTRICTED_GENE, 0);
//    orfanGeneCount.put(Constants.GENUS_RESTRICTED_GENE, 0);
//    orfanGeneCount.put(Constants.ORFAN_GENE, 0);
//    orfanGeneCount.put(Constants.STRICT_ORFAN, 0);
//
//    // Iterating through every identified gene
//    Iterator it = geneClassification.entrySet().iterator();
//    while (it.hasNext()) {
//      Map.Entry pair = (Map.Entry)it.next();
//
//      ORFGene orfGene = new ORFGene((Gene) pair.getKey(), (String)pair.getValue());
//      JSONObject orfanJSON = new JSONObject();
//      orfanJSON.put("geneid", orfGene.getId());
//      orfanJSON.put("description", orfGene.getDescription());
//      orfanJSON.put("orfanLevel", orfGene.getLevel());
//      orfanJSON.put("taxonomyLevel", orfGene.getTaxonomy());
//      orfanGenes.add(orfanJSON);
//
//      int count = orfanGeneCount.get(orfGene.getLevel());
//      count++;
//      orfanGeneCount.put(orfGene.getLevel(), count);
//    }
//    // Writing orfanGenes data JSON data into file
//    FileHandler.saveOutputFiles(orfanGenes, out + "/" + FILE_OUTPUT_ORFAN_GENES);
//
//    return orfanGeneCount;
//  }
//
//  private static JSONObject generateORFanGenesSummaryData(Map<String, Integer> orfanGeneCount, String out){
//    // Genetating ORFan genes summary data and data to be displayed in the chart
//    JSONArray orfanGenesSummary = new JSONArray();
//
//    JSONObject chartJSON = new JSONObject();
//    JSONArray x = new JSONArray();
//    JSONArray y = new JSONArray();
//
//    Iterator it = orfanGeneCount.entrySet().iterator();
//    while (it.hasNext()) {
//      Map.Entry pair = (Map.Entry)it.next();
//
//      JSONObject summaryObject = new JSONObject();
//      summaryObject.put("type", pair.getKey());
//      summaryObject.put("count", pair.getValue());
//      orfanGenesSummary.add(summaryObject);
//
//      x.add(pair.getKey());
//      y.add(pair.getValue());
//    }
//
//    chartJSON.put("x", x);
//    chartJSON.put("y", y);
//
//    FileHandler.saveOutputFiles(orfanGenesSummary,out + "/" + FILE_OUTPUT_ORFAN_GENES_SUMMARY);
//    return chartJSON;
//  }
//
//}
