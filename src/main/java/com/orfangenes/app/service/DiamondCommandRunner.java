package com.orfangenes.app.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.orfangenes.app.util.Constants.*;


/**
 * This class looks for the similar DNA/Protein sequences in the NCBI database
 * by running the blast command.
 */
@Slf4j
@Setter
public class DiamondCommandRunner {

    private String sequenceType;
    private String out;
    private String maxTargetSeqs;
    private String evalue;
    private Integer numberOfProcessors;
    private Boolean isPsiBlast;
    private Integer numIteration;

    String DIAMOND_LOCATION; // TODO
    String DIAMOND_NR_DB_LOCATION;
//    String BLAST_NT_DB_LOCATION;
    String analysisId;

    public DiamondCommandRunner(String diamondLocation, String diamondNRDbLocation, String analysisId){
        this.DIAMOND_LOCATION = diamondLocation;
        this.DIAMOND_NR_DB_LOCATION = diamondNRDbLocation;
        numberOfProcessors = Runtime.getRuntime().availableProcessors() - 2;
        this.analysisId = analysisId;
    }

    public void run() {
        final String programme = "diamond";
        final String db = "nr_diamond.dmnd";
        final String dbLocation = DIAMOND_NR_DB_LOCATION;
        List<String> fixedCommand = Arrays.asList(
                DIAMOND_LOCATION + programme,
                "blastp",
                "-q", out + File.separator + INPUT_FASTA,
                "--db", dbLocation + db,
                "--outfmt", "6", "qseqid", "sseqid", "pident", "length", "mismatch", "gapopen", "qstart", "qend", "sstart", "send", "evalue", "bitscore", "staxids",
                "--max-target-seqs", this.maxTargetSeqs,
                "--evalue", this.evalue,
                "--out", this.out + File.separator + BLAST_RESULTS + BLAST_EXT,
                "--threads", numberOfProcessors.toString());

        List<String> command = new ArrayList<>(fixedCommand);
        try {
            log.info("Executing Diamond Blast Command:{}", command.toString());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectOutput(new File("/dev/null"));
            Process process = processBuilder.start();
            ProcessHolder.addProcess(analysisId, process);

            // wait until the command get executed
//            printInputAndErrorStreams(process);
            if (process.waitFor() != 0) {
                process.destroy();
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
                ProcessHolder.removeProcess(analysisId);
                throw new RuntimeException("Diamond BLAST error occurred");
            } else {
                log.info("Diamond BLAST successfully completed!!");
                ProcessHolder.removeProcess(analysisId);
            }
        } catch (IOException ex) {
            log.error("IOError: " + ex.getMessage());
            throw new RuntimeException("Diamond BLAST error occurred");
        } catch (InterruptedException ex) {
            log.error("InterruptedException: " + ex.getMessage());
            throw new RuntimeException("Diamond BLAST error occurred");
        }
    }

    private void printInputAndErrorStreams(Process process) throws IOException {
        printInputSteam(process);
        printErrorStream(process);
    }

    private void printErrorStream(Process process) throws IOException {
        InputStream errorStream = process.getErrorStream();
        printStream(errorStream);
    }

    private void printInputSteam(Process process) throws IOException {
        InputStream inputStream = process.getInputStream();
        printStream(inputStream);
    }

    private void printStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            System.out.println("line - " + s);
        }
    }

    private Long extractMineIdFromFileName(String fileName) {
        String[] splitByExtension = fileName.split("\\.");
        String[] split = splitByExtension[0].split("-");
        Long mineId = Long.valueOf(split[split.length - 1]);
        return mineId;
    }
}
