package com.orfangenes.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orfangenes.app.model.Analysis;
import com.orfangenes.app.model.AnalysisAdminMetadata;
import com.orfangenes.app.model.InputSequence;
import com.orfangenes.app.dto.AnalysisResultsTableRaw;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.orfangenes.app.util.Constants.TYPE_NUCLEOTIDE;
import static com.orfangenes.app.util.Constants.TYPE_PROTEIN;

@Slf4j
public class AnalysisAdminMetadataStore {

    private static final String METADATA_FILE = "admin-metadata.json";
    private static final int PREVIEW_LIMIT = 1200;
    private static final ObjectMapper objectMapper = Utils.getJacksonObjectMapper();

    private AnalysisAdminMetadataStore() {
    }

    public static AnalysisAdminMetadata createInitialMetadata(String analysisDir, Analysis analysis, InputSequence inputSequence) {
        AnalysisAdminMetadata metadata = new AnalysisAdminMetadata();
        metadata.setAnalysisId(analysis.getAnalysisId());
        metadata.setSubmittedAt(analysis.getAnalysisDate());
        metadata.setSubmittedInput(getOriginalInput(inputSequence));
        metadata.setInputType(getInputType(inputSequence));
        metadata.setProgram(getProgramName(analysis));
        metadata.setNumIterations(analysis.getNumIteration());
        metadata.setServerNode(getServerNode());
        setInputLengthSummary(metadata, metadata.getSubmittedInput());
        write(analysisDir, metadata);
        return metadata;
    }

    public static void recordNormalizedInput(String analysisDir) {
        AnalysisAdminMetadata metadata = read(analysisDir);
        metadata.setNormalizedFastaPreview(readInputFastaPreview(analysisDir));
        write(analysisDir, metadata);
    }

    public static AnalysisAdminMetadata markStarted(String analysisDir, Analysis analysis) {
        AnalysisAdminMetadata metadata = read(analysisDir);
        metadata.setAnalysisId(analysis.getAnalysisId());
        metadata.setStartedAt(new Date());
        metadata.setProgram(getProgramName(analysis));
        metadata.setNumIterations(analysis.getNumIteration());
        metadata.setServerNode(getServerNode());
        if (metadata.getSubmittedAt() == null) {
            metadata.setSubmittedAt(analysis.getAnalysisDate());
        }
        metadata.setQueueDurationSeconds(secondsBetween(metadata.getSubmittedAt(), metadata.getStartedAt()));
        write(analysisDir, metadata);
        return metadata;
    }

    public static AnalysisAdminMetadata markFinished(String analysisDir, Analysis analysis) {
        AnalysisAdminMetadata metadata = read(analysisDir);
        metadata.setAnalysisId(analysis.getAnalysisId());
        metadata.setFinishedAt(new Date());
        if (metadata.getStartedAt() == null) {
            metadata.setStartedAt(analysis.getAnalysisDate());
        }
        metadata.setDurationSeconds(secondsBetween(metadata.getStartedAt(), metadata.getFinishedAt()));
        metadata.setQueueDurationSeconds(secondsBetween(metadata.getSubmittedAt(), metadata.getStartedAt()));
        metadata.setProgram(getProgramName(analysis));
        metadata.setNumIterations(analysis.getNumIteration());
        metadata.setNormalizedFastaPreview(readInputFastaPreview(analysisDir));
        write(analysisDir, metadata);
        return metadata;
    }

    public static AnalysisAdminMetadata markErrored(String analysisDir, Analysis analysis, String errorMessage) {
        AnalysisAdminMetadata metadata = markFinished(analysisDir, analysis);
        metadata.setErrorMessage(errorMessage);
        write(analysisDir, metadata);
        return metadata;
    }

    public static AnalysisAdminMetadata read(String analysisDir) {
        File metadataFile = metadataFile(analysisDir);
        if (!metadataFile.exists()) {
            return new AnalysisAdminMetadata();
        }

        try {
            return objectMapper.readValue(metadataFile, AnalysisAdminMetadata.class);
        } catch (IOException e) {
            log.warn("Unable to read admin metadata from {}", metadataFile.getAbsolutePath(), e);
            return new AnalysisAdminMetadata();
        }
    }

    public static void applyToAnalysis(Analysis analysis, String analysisDir) {
        applyToAnalysis(analysis, read(analysisDir));
    }

    public static void applyToAnalysis(Analysis analysis, AnalysisAdminMetadata metadata) {
        if (analysis == null || metadata == null) {
            return;
        }

        analysis.setStartedAt(metadata.getStartedAt());
        analysis.setFinishedAt(metadata.getFinishedAt());
        analysis.setDurationSeconds(metadata.getDurationSeconds());
        analysis.setQueueDurationSeconds(metadata.getQueueDurationSeconds());
        analysis.setSubmittedInput(metadata.getSubmittedInput());
        analysis.setInputType(metadata.getInputType());
        analysis.setInputSequenceCount(metadata.getInputSequenceCount());
        analysis.setMinInputLength(metadata.getMinInputLength());
        analysis.setMaxInputLength(metadata.getMaxInputLength());
        analysis.setAverageInputLength(metadata.getAverageInputLength());
        analysis.setNormalizedFastaPreview(metadata.getNormalizedFastaPreview());
        analysis.setValidationWarnings(metadata.getValidationWarnings());
        analysis.setErrorMessage(metadata.getErrorMessage());
        analysis.setProgram(metadata.getProgram());
        analysis.setServerNode(metadata.getServerNode());
    }

    public static void applyToTableRow(AnalysisResultsTableRaw row, String analysisDir) {
        AnalysisAdminMetadata metadata = read(analysisDir);
        row.setStartedAt(metadata.getStartedAt());
        row.setFinishedAt(metadata.getFinishedAt());
        row.setDurationSeconds(metadata.getDurationSeconds());
        row.setQueueDurationSeconds(metadata.getQueueDurationSeconds());
        row.setSubmittedInput(metadata.getSubmittedInput());
        row.setInputType(metadata.getInputType());
        row.setInputSequenceCount(metadata.getInputSequenceCount());
        row.setMinInputLength(metadata.getMinInputLength());
        row.setMaxInputLength(metadata.getMaxInputLength());
        row.setAverageInputLength(metadata.getAverageInputLength());
        row.setNormalizedFastaPreview(metadata.getNormalizedFastaPreview());
        row.setValidationWarnings(metadata.getValidationWarnings());
        row.setErrorMessage(metadata.getErrorMessage());
        row.setProgram(metadata.getProgram());
        row.setServerNode(metadata.getServerNode());
    }

    private static void write(String analysisDir, AnalysisAdminMetadata metadata) {
        try {
            File metadataFile = metadataFile(analysisDir);
            File parent = metadataFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(metadataFile, metadata);
        } catch (IOException e) {
            log.warn("Unable to write admin metadata for {}", analysisDir, e);
        }
    }

    private static File metadataFile(String analysisDir) {
        return new File(analysisDir, METADATA_FILE);
    }

    private static String getOriginalInput(InputSequence inputSequence) {
        if (inputSequence.getSequence() != null && !inputSequence.getSequence().trim().isEmpty()) {
            return inputSequence.getSequence().trim();
        }
        return inputSequence.getAccession() == null ? "" : inputSequence.getAccession().trim();
    }

    private static String getInputType(InputSequence inputSequence) {
        if (inputSequence.getAccession() != null && !inputSequence.getAccession().trim().isEmpty()) {
            return "accession";
        }
        if (inputSequence.getSequence() != null && inputSequence.getSequence().trim().startsWith(">")) {
            return "fasta_sequence";
        }
        return "raw_sequence";
    }

    private static String getProgramName(Analysis analysis) {
        if (analysis == null) {
            return "";
        }
        if (Boolean.TRUE.equals(analysis.getIsPsiBlast())) {
            return "PSI-BLAST";
        }
        if (analysis.getExecutionType() != null && analysis.getExecutionType().equalsIgnoreCase("diamond")) {
            return "DIAMOND";
        }
        if (TYPE_PROTEIN.equals(analysis.getSequenceType())) {
            return "BLASTP";
        }
        if (TYPE_NUCLEOTIDE.equals(analysis.getSequenceType())) {
            return "BLASTN";
        }
        return analysis.getExecutionType() == null ? "" : analysis.getExecutionType();
    }

    private static void setInputLengthSummary(AnalysisAdminMetadata metadata, String submittedInput) {
        List<Integer> lengths = extractSequenceLengths(submittedInput);
        metadata.setInputSequenceCount(lengths.size());
        if (lengths.isEmpty()) {
            return;
        }
        metadata.setMinInputLength(lengths.stream().min(Integer::compareTo).orElse(0));
        metadata.setMaxInputLength(lengths.stream().max(Integer::compareTo).orElse(0));
        metadata.setAverageInputLength((int) Math.round(lengths.stream().mapToInt(Integer::intValue).average().orElse(0)));
    }

    private static List<Integer> extractSequenceLengths(String submittedInput) {
        if (submittedInput == null || submittedInput.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String normalized = submittedInput.replace("\r\n", "\n").replace("\r", "\n").trim();
        if (normalized.startsWith(">")) {
            return Arrays.stream(normalized.split(">"))
                    .map(String::trim)
                    .filter(record -> !record.isEmpty())
                    .map(record -> {
                        String[] lines = record.split("\n");
                        return Arrays.stream(lines).skip(1).collect(Collectors.joining()).replaceAll("\\s+", "").length();
                    })
                    .filter(length -> length > 0)
                    .collect(Collectors.toList());
        }

        if (normalized.matches(".*[\\s,]+.*") && normalized.matches(".*[0-9_\\.].*")) {
            return Arrays.stream(normalized.split("[\\s,]+"))
                    .filter(value -> !value.trim().isEmpty())
                    .map(value -> value.trim().length())
                    .collect(Collectors.toList());
        }

        return Arrays.asList(normalized.replaceAll("\\s+", "").length());
    }

    private static String readInputFastaPreview(String analysisDir) {
        File inputFile = new File(analysisDir, Constants.INPUT_FASTA);
        if (!inputFile.exists()) {
            return "";
        }
        try {
            byte[] bytes = java.nio.file.Files.readAllBytes(inputFile.toPath());
            String content = new String(bytes);
            return content.length() > PREVIEW_LIMIT ? content.substring(0, PREVIEW_LIMIT) : content;
        } catch (IOException e) {
            return "";
        }
    }

    private static Long secondsBetween(Date start, Date end) {
        if (start == null || end == null) {
            return null;
        }
        return Math.max(0, (end.getTime() - start.getTime()) / 1000);
    }

    private static String getServerNode() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "";
        }
    }
}
