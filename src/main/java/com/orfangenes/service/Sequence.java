package com.orfangenes.service;

import com.orfangenes.model.Gene;
import lombok.extern.slf4j.Slf4j;

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
public class Sequence {
    private Map<String, Gene> genes;
    private int fileCount;
    private String blastType;

    private static final String BLAST_RESULTS = "blastResults";
    private static final String BLAST_EXT = ".bl";
    private static final String LINE_SEPARATOR = "\n";
    private static final String SEQUENCE_SEPARATOR = "\n\n";


    public Sequence(String blastType, String filename, String out, int inputTax) {
        this.blastType = blastType;
        File sequenceFile = new File(filename);
        if (!sequenceFile.exists()) {
            log.error("Failure to open sequence.");
            return;
        }
        this.genes = getGeneData(filename, inputTax);
        this.fileCount = divideSequence(filename, out);
    }

    public void generateBlastFile(String out, String max_target_seqs, String evalue) {
        log.warn("Running BLAST. Be patient...This will take 2-15 min...");
        long startTime = System.currentTimeMillis();

        BlastCommandRunner[] blastCommands = new BlastCommandRunner[fileCount];
        for (int i = 0; i < fileCount; i++) {
            BlastCommandRunner command =
                    BlastCommandRunner.builder()
                            .fileNumber(Integer.toString(i + 1))
                            .sequenceType(this.blastType)
                            .out(out)
                            .max_target_seqs(max_target_seqs)
                            .evalue(evalue)
                            .build();
            command.start();
            blastCommands[i] = command;
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
            combineBlastResults(out);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        long stopTime = System.currentTimeMillis();
        log.info("BLAST successfully Completed!! Time taken: " + (stopTime - startTime) + "ms");
        // TODO: what if we fail here
    }

    private void combineBlastResults(String out) {
        // Combining all BLAST results to one file
        try {
            PrintWriter writer = new PrintWriter(out + "/" + BLAST_RESULTS + BLAST_EXT);
            for (int i = 1; i < fileCount + 1; i++) {
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(out + "/" + BLAST_RESULTS + i + BLAST_EXT));
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

    public Map<String, Gene> getGenes() {
        return genes;
    }

    private Map<String, Gene> getGeneData(String sequenceFileName, int inputTax) {

        String inputSequence = getSequenceFromDisk(sequenceFileName);
        String[] sequences = inputSequence.split(SEQUENCE_SEPARATOR);
        Map<String, Gene> genes = new HashMap<>();

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

    private String getSequenceFromDisk(String sequenceFileName) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(sequenceFileName), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append(LINE_SEPARATOR));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return contentBuilder.toString();
    }

    private int divideSequence(String sequenceFileName, String out) {

        final int SEQUENCE_SLICE_SIZE = 3;

        String inputSequence = getSequenceFromDisk(sequenceFileName);
        String[] sequences = inputSequence.split(SEQUENCE_SEPARATOR);

        int fileNo = 0;
        StringBuilder currentSequence = new StringBuilder();

        for (int i = 1; i <= sequences.length; i++) {
            currentSequence.append(sequences[i - 1]);
            currentSequence.append(SEQUENCE_SEPARATOR);

            if (i % SEQUENCE_SLICE_SIZE == 0 || i == sequences.length) {
                createSequenceFile(out, currentSequence.toString().trim(), ++fileNo);
            }
        }
        return fileNo;
    }

    private void createSequenceFile(String out, String sequence, int fileNo) {
        try {
            PrintWriter writer =
                    new PrintWriter(out + "/sequence" + fileNo + ".fasta", "UTF-8");
            writer.println(sequence);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }
}
