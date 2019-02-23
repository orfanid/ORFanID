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
                if (result.getMultiplesTaxIdCount() == 0) {
                    blastResults.add(result);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            log.error("Blast output file not found in {} directory", blastResultsFileName);
        }
        this.blastResults = blastResults;
    }
}
