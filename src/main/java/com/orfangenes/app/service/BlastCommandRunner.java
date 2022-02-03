package com.orfangenes.app.service;

import static com.orfangenes.app.util.Constants.*;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


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
    private Integer numberOfProcessors;
    private Boolean isPsiBlast;
    private Integer numIteration;

    String BLAST_LOCATION; // TODO
    String BLAST_NR_DB_LOCATION;
    String BLAST_NT_DB_LOCATION;
    String analysisId;

    public BlastCommandRunner(String blastLocation, String blastNRDbLocation, String blastNTDbLocation, String analysisId){
        this.BLAST_LOCATION = blastLocation;
        this.BLAST_NR_DB_LOCATION = blastNRDbLocation;
        this.BLAST_NT_DB_LOCATION = blastNTDbLocation;
        numberOfProcessors = Runtime.getRuntime().availableProcessors() - 2;
        this.analysisId = analysisId;
    }

    public void run() {
        final String programme = (sequenceType.equals(TYPE_PROTEIN)) ? (BooleanUtils.isTrue(isPsiBlast)) ? "psiblast" : "blastp" : "blastn";
        final String db = (sequenceType.equals(TYPE_PROTEIN)) ? "nr" : "nt";
        final String dbLocation = (sequenceType.equals(TYPE_PROTEIN)) ? BLAST_NR_DB_LOCATION : BLAST_NT_DB_LOCATION;
        List<String> fixedCommand = Arrays.asList(
                BLAST_LOCATION + programme,
                "-query", out + File.separator + INPUT_FASTA,
                "-db", dbLocation + db,
                "-outfmt", "6 qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore staxids",
                "-max_target_seqs", this.maxTargetSeqs,
                "-evalue", this.evalue,
                "-out", this.out + File.separator + BLAST_RESULTS + BLAST_EXT,
                "-num_threads", numberOfProcessors.toString());

        List<String> command = new ArrayList<>(fixedCommand);
        if (BooleanUtils.isTrue(isPsiBlast)) {
            command.add("-num_iterations");
            command.add(numIteration == null ? "0" : String.valueOf(numIteration));
        }
        try {
            log.info("Executing Blast Command:{}", command.toString());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            ProcessHolder.addProcess(analysisId, process);

            // wait until the command get executed
            if (!process.waitFor(30, TimeUnit.MINUTES)) {
                process.destroy();
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
                ProcessHolder.removeProcess(analysisId);
                throw new RuntimeException("BLAST error occurred");
            } else {
                log.info("BLAST successfully completed!!");
                ProcessHolder.removeProcess(analysisId);
            }
        } catch (IOException ex) {
            log.error("IOError: " + ex.getMessage());
            throw new RuntimeException("BLAST error occurred");
        } catch (InterruptedException ex) {
            log.error("InterruptedException: " + ex.getMessage());
            throw new RuntimeException("BLAST error occurred");
        }
    }
}
