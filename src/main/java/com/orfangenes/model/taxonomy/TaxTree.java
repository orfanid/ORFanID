package com.orfangenes.model.taxonomy;

import com.orfangenes.util.Constants;
import javafx.util.Pair;
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

  // old variables
  private Map<Integer, TaxNode> nodes = new HashMap<>();
  private Map<Integer, String> names = new HashMap<>();
  private ArrayList<Pair<TaxNode, Integer>> tempNodes = new ArrayList<>();
  // new variables
  private Map<Integer, String> namesByTaxId = new HashMap<>();
  private Map<String, Integer> taxIdByName = new HashMap<>();
  private Map<Integer, List<String>> rankedLineageById = new HashMap<>();
  List<Integer> blastHitsTaxIDs;

  public TaxTree(String namesFilename, String nodesFilename, String rankedlineage, List<Integer> blastHitsTaxIDs) {
    this.blastHitsTaxIDs = blastHitsTaxIDs;

    // Reading the names file line by line
    try (Stream<String> lines = Files.lines(Paths.get(namesFilename), Charset.defaultCharset())) {
      lines.parallel().forEachOrdered(line -> processName(line));
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    // Reading the nodes file line by line
    try (Stream<String> lines = Files.lines(Paths.get(nodesFilename), Charset.defaultCharset())) {
      lines.parallel().forEachOrdered(line -> processNode(line));
    } catch (IOException e) {
      log.error(e.getMessage());
    }

      // Reading the rankedlineage file line by line and collect records that are interested
      try (Stream<String> lines = Files.lines(Paths.get(rankedlineage), Charset.defaultCharset())) {
          lines.parallel().forEachOrdered(line -> processRankedLineage(line));
      } catch (IOException e) {
          log.error(e.getMessage());
      }

    // Set nodes to their parents and construct tree
    for (Pair<TaxNode, Integer> node : tempNodes) {
      TaxNode parent = this.nodes.get(node.getValue());
      node.getKey().setParent(parent);
    }

  }

  private void processRankedLineage(String lineageFileLine) {
    try {
      String lineageLine = lineageFileLine.substring(0, lineageFileLine.length()-2);
      String[] lineageLineData = lineageLine.split("\t\\|");
      List<String> lineageDataList = Arrays.asList(lineageLineData);
        List<String> lineageDataCorrectedList =  lineageDataList.stream().map(value -> value.contains("\t")? value.substring(1): value).collect(Collectors.toList());
      if(lineageDataCorrectedList.size() == 10){
        int taxonomyId = Integer.parseInt(lineageDataCorrectedList.get(0));
        String scientificName = lineageDataCorrectedList.get(1);
        namesByTaxId.put(taxonomyId, scientificName);
        taxIdByName.put(scientificName, taxonomyId);
        if(this.blastHitsTaxIDs.contains(taxonomyId)){
          this.rankedLineageById.put(taxonomyId, lineageDataCorrectedList);
        }
      }else{
        log.warn("RankedLineage file " + lineageLineData[0] + " record not in order");
      }
    } catch (NumberFormatException e) {
      log.warn("Error occurred in reading ranked lineage file" + e.getMessage());
    }
  }

  private void processNode(String nodeString) {
    try {
      String[] nodeData = nodeString.split("\t\\|\t");

      int taxID = Integer.parseInt(nodeData[0]);
      int parentID = Integer.parseInt(nodeData[1]);
      String rank = nodeData[2];
      String name = names.get(taxID);
      TaxNode node = new TaxNode(taxID, name, rank);

      // TODO:
      this.tempNodes.add(new Pair<>(node, parentID));
      this.nodes.put(taxID, node);

    } catch (NumberFormatException e) {
      log.warn("Error occurred in reading scientific species node file" + e.getMessage());
    }
  }

  private void processName(String nameString) {
    String[] nameData = nameString.split("\t\\|\t");
    String type = nameData[3];
    if (type.equals("scientific name\t|")) {
      try {
        int taxID = Integer.parseInt(nameData[0]);
        String name = nameData[1];
        names.put(taxID, name);
      } catch (NumberFormatException e) {
        log.warn("Error occurred in reading scientific species name" + e.getMessage());
      }
    }
  }

  public Map<String, Integer> getHeirarchyFromNode(int taxID) {
    TaxNode node = this.nodes.get(taxID);
    Map<String, Integer> hierarchy = new LinkedHashMap<>();

    try {
      if (node.getRank().equals(Constants.SPECIES)) {
        hierarchy.put(Constants.SPECIES, node.getID());
      }
      while (node.getParent() != null && node.getParent().getID() != 1) {
        node = node.getParent();
          if (node.getRank().equals("superkingdom") ||
                  node.getRank().equals("phylum") ||
                  node.getRank().equals("class") ||
                  node.getRank().equals("order") ||
                  node.getRank().equals("family") ||
                  node.getRank().equals("genus") ||
                  node.getRank().equals("species")
                  ){
              hierarchy.put(node.getRank(), node.getID());

          }
      }
    } catch (NullPointerException e) {
      log.error("An Error occured while constructing hierarchy");
    }
    return hierarchy;
  }

  public TaxNode getNode(int taxID) {
    return this.nodes.get(taxID);
  }

  public Map<Integer, TaxNode> getNodes() {
    return this.nodes;
  }
}
