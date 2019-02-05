package com.orfangenes.service;

import com.orfangenes.model.BlastResult;
import com.orfangenes.model.taxonomy.TaxNode;
import com.orfangenes.model.taxonomy.RankedLineage;
import static com.orfangenes.util.Constants.*;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TaxTree implements Serializable {

    private int organismTaxID;
    private Set<Integer> blastHitsTaxIDs;
    private Map<Integer, List<String>> rankedLineageWithNames = new HashMap<>();
    private List<String> inputRankedLineage = new ArrayList<>();
    private Map<String, List<List<String>>> taxonomyTreeForGenes = new HashMap<>();
    public List<String> rankedLineageFileColumnNames =
            Arrays.asList("tax_id", "subspecies", "species", "genus", "family", "order", "class", "phylum", "kingdom", "superkingdom");

    public TaxTree(String rankedLineageFilePath, Set<Integer> blastHitsTaxIDs, int organismTaxID) {

    this.blastHitsTaxIDs = blastHitsTaxIDs;
    this.organismTaxID = organismTaxID;

    // Reading the rankedlineage file line by line and collect records that are interested
    try (Stream<String> lines = Files.lines(Paths.get(rankedLineageFilePath), Charset.defaultCharset())) {
      lines.parallel().forEachOrdered(line -> processRankedLineage(line));
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void processRankedLineage(String lineageFileLine) {
    try {
      String lineageLine = lineageFileLine.substring(0, lineageFileLine.length() - 2);
      String[] lineageLineData = lineageLine.split("\t\\|");
      List<String> lineageDataList = Arrays.asList(lineageLineData);
      List<String> rankedLineageRecord =
          lineageDataList.stream()
                  .map(value -> value.contains("\t") ? value.substring(1) : value)
                  .collect(Collectors.toList());
      if (rankedLineageRecord.size() == 10) {
        int taxonomyId = Integer.parseInt(rankedLineageRecord.get(0));
        if (this.blastHitsTaxIDs.contains(taxonomyId)) {
          this.rankedLineageWithNames.put(taxonomyId, rankedLineageRecord);
        }
      } else {
        log.warn("RankedLineage file " + lineageLineData[0] + " record not in order");
      }
    } catch (NumberFormatException e) {
      log.warn("Error occurred in reading ranked lineage file" + e.getMessage());
    }
  }

//  private void buildRankedLineageWithTaxonomyNodes(){
//
//      try {
//          // go through each ranked lineage record to construct ranked lineage with Taxonomy nodes
//          for (Map.Entry<Integer, List<String>> record : rankedLineageWithNames.entrySet()) {
//
//              List<TaxNode> tempNodeLineage = new ArrayList<>();
//              List<String> lineageNames = record.getValue();
//              TaxNode taxNode = new TaxNode();
//              taxNode.setNID(record.getKey());
//              System.out.print(record.getKey() + "\t|");
//              taxNode.setName(lineageNames.get(1)); // scientific name/sub species level
//              taxNode.setNRank(rankedLineageFileColumnNames.get(1));
//              tempNodeLineage.add(taxNode);
//              for (int i = 2; i < lineageNames.size(); i++) { // skip taxonomy Id column
//                  taxNode = new TaxNode();
//                  String  taxonomyName = lineageNames.get(i);
//                  taxNode.setName((taxonomyName != null && !taxonomyName.equals(""))? taxonomyName:NOT_AVAILABLE);
//                  taxNode.setNRank(rankedLineageFileColumnNames.get(i));
//                  tempNodeLineage.add(taxNode);
//              }
//              rankedLineageList.add(new RankedLineage(record.getKey(), tempNodeLineage));
//          }
//      } catch (Exception e) {
//          log.error("Error occurred during tree construction : " + e.getMessage());
//      }
//  }

//    Map<String, List<RankedLineage>> buildRankedLineageList(List<BlastResult> blastResults){
//
//        buildRankedLineageWithTaxonomyNodes();
//        this.inputRankedLineage = filterRankedLineagesByTaxonomyId(this.organismTaxID);
//
//        for (BlastResult blastResult : blastResults) {
//            String geneId = blastResult.getQueryid();
//            int subjectTaxonomyId = blastResult.getStaxid();
//            taxonomyTreeForGenes.computeIfAbsent(geneId, k -> new ArrayList<>())
//                    .add(filterRankedLineagesByTaxonomyId(subjectTaxonomyId));
//        }
//        return taxonomyTreeForGenes;
//    }
//
//    /**
//     * Given a Taxonomy Id, this method will return the RankedLineage
//     * @param taxonomyId
//     * @return
//     */
//    private RankedLineage filterRankedLineagesByTaxonomyId(int taxonomyId){
//        return rankedLineageList.stream()
//                .filter(rankedLineage -> rankedLineage.getTaxonomyId()==taxonomyId)
//                .collect(Collectors.toList()).get(0);
//    }

  Map<String, List<List<String>>> buildRankedLineageList(List<BlastResult> blastResults){

      try {
          this.inputRankedLineage = filterRankedLineagesByTaxonomyId(this.organismTaxID);
          // travel though each gene
          for (BlastResult blastResult : blastResults) {
              String geneId = blastResult.getQueryid();
              int subjectTaxonomyId = blastResult.getStaxid();
              taxonomyTreeForGenes.computeIfAbsent(geneId, k -> new ArrayList<>())
                      .add(filterRankedLineagesByTaxonomyId(subjectTaxonomyId));
          }
      } catch (Exception e) {
          log.error("RankedLineage Tree build error: " +  e.getMessage());
      }
      return taxonomyTreeForGenes;
  }

  private List<String> filterRankedLineagesByTaxonomyId(int taxonomyId){
     return rankedLineageWithNames.get(taxonomyId);
  }

  List<String> getInputRankedLineage() {
      return inputRankedLineage;
  }
}
