package com.orfangenes.app.service;

import com.orfangenes.app.util.Constants;
import com.orfangenes.app.model.BlastResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class process the blast results to identify Orphan Genes
 */
@Slf4j
@Getter
public class HomologyProcessingService {

    private List<BlastResult> blastResults;

    public HomologyProcessingService(String outputDir) {
        String blastResultsFileName = outputDir + File.separator + Constants.BLAST_RESULTS_FILE;
        List<BlastResult> blastResults = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(blastResultsFileName));
            while (scanner.hasNextLine()) {
                BlastResult result = new BlastResult(scanner.nextLine());
                if (result.getMultiplesTaxIdCount() == 0) {
                    blastResults.add(result);
                } else {
                    log.warn("Multi-species Gene identified in BLAST. Ignoring Gene ID " + result.getSseqid());
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            log.error("Blast output file not found in {} directory", blastResultsFileName);
        }
        this.blastResults = blastResults;
    }
}
