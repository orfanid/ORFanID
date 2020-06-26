package com.orfangenes.service;

import com.orfangenes.model.BlastResult;
import com.orfangenes.model.Gene;
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
public class TaxTreeService implements Serializable {

    private int organismTaxID;
    private Set<Integer> blastHitsTaxIDs;

    private Map<Integer, List<String>> rankedLineageWithNames = new HashMap<>();
    private List<String> inputRankedLineage = new ArrayList<>();
    private Map<String, List<List<String>>> taxonomyTreeForGenes = new HashMap<>();


    public TaxTreeService(String rankedLineageFilePath, Set<Integer> blastHitsTaxIDs, int organismTaxID) {

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
            // Ignoring last "  |" in file
            String lineageLine = lineageFileLine.substring(0, lineageFileLine.length() - 2);

            String[] lineageLineData = lineageLine.split("\t\\|");
            List<String> lineageDataList = Arrays.asList(lineageLineData);

            // Ignoring tabs in string
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

    Map<String, List<List<String>>> buildRankedLineageList(List<BlastResult> blastResults) {

        try {
            this.inputRankedLineage = filterRankedLineagesByTaxonomyId(this.organismTaxID);
            // travel though each gene
            for (BlastResult blastResult : blastResults) {
                String geneId = blastResult.getQueryid();
                int subjectTaxonomyId = blastResult.getStaxid();
                // Creates a mapping between Gene ID, and its lineage
                taxonomyTreeForGenes.computeIfAbsent(geneId, k -> new ArrayList<>())
                        .add(filterRankedLineagesByTaxonomyId(subjectTaxonomyId));
            }
        } catch (Exception e) {
            log.error("RankedLineage Tree build error: " + e.getMessage());
        }
        return taxonomyTreeForGenes;
    }

    private List<String> filterRankedLineagesByTaxonomyId(int taxonomyId) {
        if(rankedLineageWithNames.get(taxonomyId) == null){
            System.err.println("Tax ID: " + taxonomyId + " does not have a lineage");
        }
        return rankedLineageWithNames.get(taxonomyId);
    }

    List<String> getInputRankedLineage() {
        return inputRankedLineage;
    }
}
