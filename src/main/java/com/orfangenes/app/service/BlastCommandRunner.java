package com.orfangenes.app.service;

import static com.orfangenes.app.util.Constants.*;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * This class looks for the similar DNA/Protein sequences in the NCBI database
 * by running the blast command.
 */
@Slf4j
@Setter
public class BlastCommandRunner {

    private String sequenceType;
    private String out;
    private String maxTargetSeqs;
    private String evalue;

    String BLAST_LOCATION; // TODO

    public BlastCommandRunner(String blastLocation){
        this.BLAST_LOCATION = blastLocation;
    }

    public void run() {
        final String programme = (sequenceType.equals(TYPE_PROTEIN)) ? "blastp" : "blastn";
        List<String> command = Arrays.asList(
                BLAST_LOCATION + programme,
                "-query", out + File.separator + INPUT_FASTA,
                "-db", "nr",
                "-outfmt", "6 qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore staxids",
                "-max_target_seqs", this.maxTargetSeqs,
                "-evalue", this.evalue,
                "-out", this.out + File.separator + BLAST_RESULTS + BLAST_EXT,
                "-remote");
        try {
            log.info("Executing Blast Command:{}", command.toString());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // wait until the command get executed
            if (process.waitFor() != 0) {
                throw new RuntimeException("BLAST error occurred");
            } else {
                log.info("BLAST successfully completed!!");
            }
        } catch (IOException ex) {
            log.error("IOError: " + ex.getMessage());
        } catch (InterruptedException ex) {
            log.error("InterruptedException: " + ex.getMessage());
        }
    }
}
