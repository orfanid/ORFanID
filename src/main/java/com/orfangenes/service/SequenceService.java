package com.orfangenes.service;

import com.orfangenes.model.Gene;
import static com.orfangenes.util.Constants.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class SequenceService {

    private String blastType;
    private String sequenceFile;
    private String outputDir;

    @Value("${ncbi.blast.programme.location}")
    String BLAST_LOCATION="/usr/local/ncbi/blast/bin/";


    public SequenceService(String blastType, String sequenceFile, String outputDir) {
        this.blastType = blastType;
        this.sequenceFile = sequenceFile;
        this.outputDir = outputDir;
    }

    public void findHomology( String out, String maxTargetSeqs, String eValue){
        String inputSequence = getSequenceFromFile(this.sequenceFile);
        List<String> sequenceBatches = separateSequenceToBatches(inputSequence);
        for (int fileCount = 0; fileCount <sequenceBatches.size() ; fileCount++) {
            createSequenceFile(out, sequenceBatches.get(fileCount) , fileCount+1);
        }
        runBlastCommands(maxTargetSeqs, eValue, sequenceBatches.size());
        combineBlastResults(sequenceBatches.size());
    }

    private void runBlastCommands(String maxTargetSeqs, String evalue, int fileCount) {
        log.warn("Running BLAST. Be patient...This will take 2-15 min...");
        long startTime = System.currentTimeMillis();

        BlastCommandRunner[] blastCommands = new BlastCommandRunner[fileCount];
        for (int i = 0; i < fileCount; i++) {
            BlastCommandRunner blastCommandRunner = new BlastCommandRunner(BLAST_LOCATION);
            blastCommandRunner.setFileNumber(Integer.toString(i + 1));
            blastCommandRunner.setSequenceType(this.blastType);
            blastCommandRunner.setOut(this.outputDir);
            blastCommandRunner.setMaxTargetSeqs(maxTargetSeqs);
            blastCommandRunner.setEvalue(evalue);
            blastCommandRunner.start();
            blastCommands[i] = blastCommandRunner;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }

        // Wait for all blast commands to finish running
        try {
            for (BlastCommandRunner command : blastCommands) {
                command.join();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        long stopTime = System.currentTimeMillis();
        log.info("BLAST successfully Completed!! Time taken: " + (stopTime - startTime) + "ms");
    }

    private void combineBlastResults(int fileCount) {
        // Combining all BLAST results to one file
        try {
            PrintWriter writer = new PrintWriter(outputDir + File.separator + BLAST_RESULTS + BLAST_EXT);
            for (int i = 0; i < fileCount; i++) {
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(outputDir + File.separator + BLAST_RESULTS + (i+1) + BLAST_EXT));
                String blastResult = reader.readLine();
                while (blastResult != null) {
                    writer.println(blastResult);
                    blastResult = reader.readLine();
                }
                reader.close();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getSequenceFromFile(String sequenceFileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(sequenceFileName), StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringBuilder.append(s).append(LINE_SEPARATOR));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return stringBuilder.toString();
    }

    private List<String> separateSequenceToBatches(String inputSequence) {

        final int SEQUENCE_BATCH_SIZE = 3;
        List<String> sequenceBatches = new ArrayList<>();

        String[] sequences = inputSequence.split(SEQUENCE_SEPARATOR);
        StringBuilder currentSequence = new StringBuilder();

        for (int i = 1; i <= sequences.length; i++) {
            currentSequence.append(sequences[i - 1]);
            currentSequence.append(SEQUENCE_SEPARATOR);

            if (i % SEQUENCE_BATCH_SIZE == 0 || i == sequences.length) {
                sequenceBatches.add(currentSequence.toString().trim());
            }
        }
        return sequenceBatches;
    }

    private void createSequenceFile(String out, String sequence, int fileNo) {
        try {
            PrintWriter writer =
                    new PrintWriter(out + File.separator + SEQUENCE + fileNo + FASTA_EXT, StandardCharsets.UTF_8.toString());
            writer.println(sequence);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    public Map<String, Gene> getGenes(int inputTax) {

        Map<String, Gene> genes = new HashMap<>();

        String inputSequence = getSequenceFromFile(this.sequenceFile);
        String[] sequences = inputSequence.split(SEQUENCE_SEPARATOR);

        for (String sequence : sequences) {
            if (sequence.equals(LINE_SEPARATOR)) {
                continue;
            }
            Gene gene = new Gene();
            String[] lines = sequence.split(LINE_SEPARATOR);

            // 1) process comment line
            String commentLine = lines[0];
            String[] comments = commentLine.split(" ", 2);
            gene.setGeneID(comments[0].substring(1));
            gene.setDescription(comments[1]);
            gene.setTaxID(inputTax);

            // 2) process sequence lines
            StringBuilder sequenceString = new StringBuilder();
            for (int i = 1; i < lines.length; i++) {
                sequenceString.append(lines[i]);
            }
            gene.setSequence(sequenceString.toString());
            genes.put(gene.getGeneID(), gene);
        }
        return genes;
    }
}
