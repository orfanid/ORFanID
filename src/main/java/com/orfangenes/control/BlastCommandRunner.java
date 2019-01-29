package com.orfangenes.control;

import static com.orfangenes.util.Constants.*;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 *  This class looks for the similar DNA/Protein sequences in the NCBI database
 *  by running the blast command.
 */
@Slf4j
@Builder
public class BlastCommandRunner extends Thread {

    private String fileNumber;
    private String sequenceType;
    private String out;
    private String max_target_seqs;
    private String evalue;

    @Override
    public void run() {
        final String programme = (sequenceType.equals(TYPE_PROTEIN))? "blastp": "blastn";
        List<String> command = Arrays.asList(
                "/usr/local/ncbi/blast/bin/" + programme,
                "-query", out + "/" + SEQUENCE + this.fileNumber + FASTA_EXT,
                "-db", "nr",
                "-outfmt", "6 qseqid sseqid pident length mismatch gapopen qstart qend sstart send evalue bitscore staxid",
                "-max_target_seqs", this.max_target_seqs,
                "-evalue", this.evalue,
                "-out", this.out + "/" + BLAST_RESULTS + this.fileNumber + BLAST_EXT,
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
