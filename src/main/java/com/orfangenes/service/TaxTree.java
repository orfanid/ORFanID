package com.orfangenes.service;

import com.orfangenes.model.BlastResult;
import com.orfangenes.model.taxonomy.TaxNode;
import com.orfangenes.model.taxonomy.RankedLineage;
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

  private Map<String, Integer> taxIdByName = new HashMap<>();
  private Map<Integer, List<String>> rankedLineageWithNames = new HashMap<>();
  private List<RankedLineage> rankedLineageList = new ArrayList<>();
  private Map<String, List<RankedLineage>> taxonomyTreeForGenes = new HashMap<>();
  private RankedLineage inputRankedLineage = new RankedLineage();
  private int organismTaxID;
  private Set<Integer> blastHitsTaxIDs;
  private List<String> taxonomyLevelNames =
      Arrays.asList("tax_id", "tax_name", "species", "genus", "family", "order", "class", "phylum", "kingdom", "superkingdom");

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
        String scientificName = rankedLineageRecord.get(1);
        taxIdByName.put(scientificName, taxonomyId);
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


  private void buildRankedLineageWithTaxonomyNodes(){
      try {
          // go through each ranked lineage record to construct ranked lineage with Taxonomy nodes
          for (Map.Entry<Integer, List<String>> record : rankedLineageWithNames.entrySet()) {
              List<TaxNode> tempNodeLineage = new ArrayList<>();
              List<String> lineageNames = record.getValue();
              TaxNode taxNode = new TaxNode();
              taxNode.setNID(record.getKey());
              taxNode.setName(lineageNames.get(1));
              taxNode.setNRank(taxonomyLevelNames.get(2));
              tempNodeLineage.add(taxNode);
              System.out.print(tempNodeLineage.toString());
              for (int i = 3; i < lineageNames.size(); i++) { // skip taxonomy Id column
                  String  lineageName = lineageNames.get(i);
                  int MISSING_NODE = -1;
                  int taxonomyId = (lineageName != null && !lineageName.equals(""))? taxIdByName.get(lineageName): MISSING_NODE;
                  taxNode = new TaxNode();
                  taxNode.setNID(taxonomyId);
                  taxNode.setNRank(taxonomyLevelNames.get(i));
                  taxNode.setName(lineageNames.get(i));
                  System.out.print(tempNodeLineage.toString());
                  tempNodeLineage.add(taxNode);
              }
              rankedLineageList.add(new RankedLineage(record.getKey(), tempNodeLineage));
              System.out.println();
          }
      } catch (Exception e) {
          log.error("Error occurred during tree construction : " + e.getMessage());
      }
  }

  public Map<String, List<RankedLineage>> buildRankedLineageList(List<BlastResult> blastResults){

      buildRankedLineageWithTaxonomyNodes();
      this.inputRankedLineage = filterRankedLineagesByTaxonomyId(this.organismTaxID);

      for (BlastResult blastResult : blastResults) {
          String geneId = blastResult.getQueryid();
          int subjectTaxonomyId = blastResult.getStaxid();
          taxonomyTreeForGenes.computeIfAbsent(geneId, k -> new ArrayList<>())
                  .add(filterRankedLineagesByTaxonomyId(subjectTaxonomyId));
      }
      return taxonomyTreeForGenes;
  }

    /**
     * Given a Taxonomy Id, this method will return the RankedLineage
     * @param taxonomyId
     * @return
     */
  private RankedLineage filterRankedLineagesByTaxonomyId(int taxonomyId){
      return rankedLineageList.stream()
              .filter(rankedLineage -> rankedLineage.getTaxonomyId()==taxonomyId)
              .collect(Collectors.toList()).get(0);
  }

}
