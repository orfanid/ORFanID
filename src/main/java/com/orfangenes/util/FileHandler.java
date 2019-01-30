package com.orfangenes.util;

import com.orfangenes.model.entities.InputSequence;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.orfangenes.util.Constants.*;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
public class FileHandler {

    @Value("${data.outputdir}")
    private static String outputdir;

    public static void createResultsOutputDir(String outputPath) {
        File file = new File(outputPath);
        file.mkdir();
    }

    public static void saveInputSequence(String outputPath, InputSequence sequence) {
        Assert.assertFalse("Sequence cannot be empty", sequence == null | sequence.getSequence().length() <= 0);
        try {
            String inputFilePath = outputPath + "/" + INPUT_FASTA;
            FileOutputStream fileOutputStream = new FileOutputStream(inputFilePath);
            fileOutputStream.write(sequence.getSequence().getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            log.error("File not found: " + e.getMessage());
        } catch (IOException e) {
            log.error("IO Error: " + e.getMessage());
        }
    }

    public static void saveResultMetadata(String outputdir, InputSequence sequence, String sessionid) {
        long unixTime = System.currentTimeMillis() / 1000L;
        String date = Long.toString(unixTime);

        String organismName = sequence.getOrganismName().split("\\(")[0];

        JSONObject resultData = new JSONObject();
        resultData.put("date", date);
        resultData.put("sessionid", sessionid);
        resultData.put("organism", organismName);
        resultData.put("saved", false);

        String resultFileName = outputdir + "/" + FILE_RESULT_METADATA;
        saveOutputFiles(resultData, resultFileName);
    }

    public static String readJSONAsString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            log.error("File not found : " + e.getMessage());
        }
        return null;
    }

    public static JSONObject getObjectFromFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(content);
            return json;
        } catch (IOException e) {
            log.error("File not found : " + e.getMessage());
            return null;
        } catch (ParseException e) {
            log.error("Could not convert file content to json: " + e.getMessage());
            return null;
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

    public static String blastToJSON(String filePath, String geneID) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(filePath));
            JSONArray results = (JSONArray) obj;
            for (Object singleResult : results) {
                if (singleResult instanceof JSONObject) {
                    JSONObject result = (JSONObject) singleResult;
                    String id = (String)result.get("id");
                    if (id.equals(geneID)) {
                        return result.toString();
                    }
                }
            }
            return null;
        } catch (IOException e) {
            log.error("File not found : " + e.getMessage());
        } catch (ParseException e) {
            log.error("Blast results cannot be interpreted : " + e.getMessage());
        }
        return null;
    }

    public static void saveOutputFiles(JSONObject content, String outputFile) {
        try {
            StringWriter writer = new StringWriter();
            content.writeJSONString(writer);
            String contentString = writer.toString();
            PrintWriter printWriter = new PrintWriter(outputFile, "UTF-8");
            printWriter.println(contentString);
            printWriter.close();
            log.info("JSON file saved: " + outputFile);
        } catch (IOException e) {
            log.error("JSON file saving error: " + e.getMessage());
        }
    }

    public static void saveOutputFiles(JSONArray content, String outputFile) {
        try {
            StringWriter writer = new StringWriter();
            content.writeJSONString(writer);
            String contentString = writer.toString();
            PrintWriter printWriter = new PrintWriter(outputFile, "UTF-8");
            printWriter.println(contentString);
            printWriter.close();
            log.info("JSON file saved: " + outputFile);
        } catch (IOException e) {
            log.error("JSON file saving error: " + e.getMessage());
        }
    }
}
