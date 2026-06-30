package com.orfangenes.app.util;

import com.orfangenes.app.model.InputSequence;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.orfangenes.app.util.Constants.*;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
public class FileHandler {

    public static void createResultsOutputDir(String outputPath) {
        File file = new File(outputPath);
        file.mkdir();
    }

    public static void saveInputSequence(String outputPath, InputSequence sequence) {
        String genesequence = sequence.getSequence();
        String accessionType = sequence.getAccessionType();

        if (genesequence == null || genesequence.equals("")) {
            String accession = sequence.getAccession(); // 16128551,226524729,16127995
            try {
                genesequence = AccessionSearch.fetchSequenceByAccession(accessionType, accession);
                genesequence = normalizeSubmittedSequence(genesequence, accessionType);
                sequence.setSequence(genesequence);
            } catch (Exception e) {
                log.error("Gene sequence not found from provided accession", e.getMessage());
                throw new IllegalArgumentException("Gene sequence not found from provided accession", e);
            }
        } else if (isAccessionInput(genesequence)) {
            String accession = genesequence;
            try {
                genesequence = AccessionSearch.fetchSequenceByAccession(accessionType, accession);
                genesequence = normalizeSubmittedSequence(genesequence, accessionType);
                sequence.setAccession(accession);
                sequence.setSequence(genesequence);
            } catch (Exception e) {
                log.error("Gene sequence not found from provided accession", e.getMessage());
                throw new IllegalArgumentException("Gene sequence not found from provided accession", e);
            }
        } else {
            genesequence = normalizeSubmittedSequence(genesequence, accessionType);
            sequence.setSequence(genesequence);
        }

        try {
            String inputFilePath = outputPath + File.separator + INPUT_FASTA;
            FileOutputStream fileOutputStream = new FileOutputStream(inputFilePath);
            fileOutputStream.write(genesequence.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            log.error("File not found: " + e.getMessage());
        } catch (IOException e) {
            log.error("IO Error: " + e.getMessage());
        }
    }

    private static boolean isAccessionInput(String input) {
        if (input == null) {
            return false;
        }

        String normalized = input.trim();
        if (normalized.isEmpty() || normalized.startsWith(">")) {
            return false;
        }

        String[] tokens = normalized.split("[\\s,;]+");
        for (String token : tokens) {
            if (token.trim().isEmpty()) {
                continue;
            }
            if (!isAccessionToken(token.trim())) {
                return false;
            }
        }
        return tokens.length > 0;
    }

    private static boolean isAccessionToken(String token) {
        String normalized = token.toUpperCase();
        return normalized.matches("^[A-Z]{1,4}_[A-Z0-9]+(\\.[0-9]+)?$")
                || normalized.matches("^[A-Z]{1,4}[0-9]{5,}(\\.[0-9]+)?$")
                || normalized.matches("^[0-9]{5,}$");
    }

    static String normalizeSubmittedSequence(String inputSequence, String sequenceType) {
        String normalizedInput = inputSequence == null ? "" : inputSequence.replace("\r\n", "\n").replace("\r", "\n").trim();
        if (normalizedInput.isEmpty()) {
            throw new IllegalArgumentException("Sequence cannot be empty");
        }

        if (normalizedInput.startsWith(">")) {
            return normalizeFastaSequence(normalizedInput, sequenceType);
        }

        String cleanedSequence = normalizedInput.replaceAll("\\s+", "").toUpperCase();
        validateSequenceCharacters(cleanedSequence, sequenceType, "Query_1");
        return ">Query_1 submitted sequence" + LINE_SEPARATOR + cleanedSequence + LINE_SEPARATOR;
    }

    private static String normalizeFastaSequence(String fastaSequence, String sequenceType) {
        String[] lines = fastaSequence.split("\\n");
        String currentHeader = null;
        StringBuilder currentSequence = new StringBuilder();
        List<String> normalizedRecords = new ArrayList<>();
        int generatedId = 1;

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }
            if (trimmedLine.startsWith(">")) {
                if (currentHeader != null) {
                    generatedId = addNormalizedRecord(normalizedRecords, currentHeader, currentSequence.toString(), sequenceType, generatedId);
                }
                currentHeader = trimmedLine.substring(1).trim();
                currentSequence = new StringBuilder();
            } else {
                if (currentHeader == null) {
                    throw new IllegalArgumentException("FASTA sequence line found before a header");
                }
                currentSequence.append(trimmedLine);
            }
        }

        if (currentHeader != null) {
            addNormalizedRecord(normalizedRecords, currentHeader, currentSequence.toString(), sequenceType, generatedId);
        }

        if (normalizedRecords.isEmpty()) {
            throw new IllegalArgumentException("No FASTA sequence records found");
        }

        return String.join(LINE_SEPARATOR, normalizedRecords) + LINE_SEPARATOR;
    }

    private static int addNormalizedRecord(List<String> normalizedRecords, String header, String sequence, String sequenceType, int generatedId) {
        String cleanedSequence = sequence.replaceAll("\\s+", "").toUpperCase();
        if (cleanedSequence.isEmpty()) {
            throw new IllegalArgumentException("FASTA record is missing sequence characters");
        }
        validateSequenceCharacters(cleanedSequence, sequenceType, header);

        String normalizedHeader = header == null ? "" : header.trim();
        if (normalizedHeader.isEmpty()) {
            normalizedHeader = "Query_" + generatedId;
            generatedId++;
        }

        normalizedRecords.add(">" + normalizedHeader + LINE_SEPARATOR + cleanedSequence);
        return generatedId;
    }

    private static void validateSequenceCharacters(String sequence, String sequenceType, String sequenceLabel) {
        Pattern allowedCharacters = TYPE_PROTEIN.equals(sequenceType)
                ? Pattern.compile("^[ACDEFGHIKLMNPQRSTVWYBXZUOJ*]+$", Pattern.CASE_INSENSITIVE)
                : Pattern.compile("^[ACGTUNRYKMSWBDHV]+$", Pattern.CASE_INSENSITIVE);

        if (!allowedCharacters.matcher(sequence).matches()) {
            throw new IllegalArgumentException("Invalid characters found in " + sequenceLabel + " for " + sequenceType + " sequence input");
        }

        if (TYPE_NUCLEOTIDE.equals(sequenceType) && Pattern.compile("[EFILPQXZJO]", Pattern.CASE_INSENSITIVE).matcher(sequence).find()) {
            throw new IllegalArgumentException(sequenceLabel + " looks like protein sequence but nucleotide was selected");
        }
    }

    public static JSONArray getArrayFromFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(content);
            return json;
        } catch (IOException e) {
            log.error("File not found : " + e.getMessage());
            return null;
        } catch (ParseException e) {
            log.error("Could not convert file content to json: " + e.getMessage());
            return null;
        }
    }

//    public static void saveResultMetadata(String outputdir, InputSequence sequence, String sessionid) {
//        long unixTime = System.currentTimeMillis() / 1000L;
//        String date = Long.toString(unixTime);
//
//        String organismName = sequence.getOrganismName().split("\\(")[0];
//
//        JSONObject resultData = new JSONObject();
//        resultData.put("date", date);
//        resultData.put("sessionid", sessionid);
//        resultData.put("organism", organismName);
//        resultData.put("saved", false);
//
//        String resultFileName = outputdir + File.separator + FILE_RESULT_METADATA;
//        saveOutputFiles(resultData, resultFileName);
//    }

//    public static String readJSONAsString(String filePath) {
//        try {
//            return new String(Files.readAllBytes(Paths.get(filePath)));
//        } catch (IOException e) {
//            log.error("File not found : " + e.getMessage());
//        }
//        return null;
//    }
//
//    public static JSONObject getObjectFromFile(String filePath) {
//        try {
//            String content = new String(Files.readAllBytes(Paths.get(filePath)));
//            JSONParser parser = new JSONParser();
//            JSONObject json = (JSONObject) parser.parse(content);
//            return json;
//        } catch (IOException e) {
//            log.error("File not found : " + e.getMessage());
//            return null;
//        } catch (ParseException e) {
//            log.error("Could not convert file content to json: " + e.getMessage());
//            return null;
//        }
//    }
//
    public static String blastToJSON(String blastResults, String geneID) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(blastResults);
            JSONArray results = (JSONArray) obj;
            for (Object singleResult : results) {
                if (singleResult instanceof JSONObject) {
                    JSONObject result = (JSONObject) singleResult;
                    String id = (String) result.get("id");
                    if (id.equals(geneID)) {
                        return result.toString();
                    }
                }
            }
            return null;
        } catch (ParseException e) {
            log.error("Blast results cannot be interpreted : " + e.getMessage());
        }
        return null;
    }
//
//    public static void saveOutputFiles(JSONObject content, String outputFile) {
//        try {
//            StringWriter writer = new StringWriter();
//            content.writeJSONString(writer);
//            String contentString = writer.toString();
//            PrintWriter printWriter = new PrintWriter(outputFile, "UTF-8");
//            printWriter.println(contentString);
//            printWriter.close();
//            log.info("JSON file saved: " + outputFile);
//        } catch (IOException e) {
//            log.error("JSON file saving error: " + e.getMessage());
//        }
//    }
//
//    public static void saveOutputFiles(JSONArray content, String outputFile) {
//        try {
//            StringWriter writer = new StringWriter();
//            content.writeJSONString(writer);
//            String contentString = writer.toString();
//            PrintWriter printWriter = new PrintWriter(outputFile, "UTF-8");
//            printWriter.println(contentString);
//            printWriter.close();
//            log.info("JSON file saved: " + outputFile);
//        } catch (IOException e) {
//            log.error("JSON file saving error: " + e.getMessage());
//        }
//    }
}
