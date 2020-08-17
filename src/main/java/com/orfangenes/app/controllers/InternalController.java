package com.orfangenes.app.controllers;

import com.orfangenes.app.ORFanGenes;
import com.orfangenes.app.util.Constants;
import com.orfangenes.app.util.FileHandler;
import com.orfangenes.app.model.InputSequence;
import com.sun.net.httpserver.Authenticator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class InternalController {

    @Value("${data.outputdir}")
    private String OUTPUT_DIR;

    @Value("${app.dir.root}")
    private String APP_DIR;

    @PostMapping("/analyse")
    public String analyse(@Valid @ModelAttribute("sequence") InputSequence sequence, BindingResult result, Model model) {

        log.info("Analysis started....");
        Assert.assertFalse("Error", result.hasErrors());
        final String sessionID = System.currentTimeMillis() + "_" + RandomStringUtils.randomAlphanumeric(3);
        OUTPUT_DIR = (OUTPUT_DIR.endsWith("/"))? OUTPUT_DIR : OUTPUT_DIR + File.separator;
        String analysisDir = OUTPUT_DIR + sessionID;
        log.info("########### analysis  Dir: " + analysisDir);
        String inputFastaFile = analysisDir + File.separator + Constants.INPUT_FASTA;

        String organismTaxID = sequence.getOrganismName().split("\\(")[1];
        organismTaxID = organismTaxID.substring(0, organismTaxID.length() - 1);
        int organismTax = Integer.parseInt(organismTaxID);
        try {
            FileHandler.createResultsOutputDir(analysisDir);
            FileHandler.saveInputSequence(analysisDir, sequence);
            FileHandler.saveResultMetadata(analysisDir, sequence, sessionID);

            ORFanGenes.run(
                    inputFastaFile,
                    analysisDir,
                    organismTax,
                    sequence.getType(),
                    sequence.getMaxTargetSequence(),
                    "1e-" + sequence.getMaxEvalue(),
                    sequence.getIdentity(),
                    APP_DIR);
        } catch (Exception e) {
            log.error("Analysis Failed: " + e.getMessage());
        }
        return "redirect:/result?sessionid=" + sessionID;
    }

    @PostMapping("/data/summary")
    @ResponseBody
    public String getSummary(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        String output = OUTPUT_DIR + sessionID;
        return FileHandler.readJSONAsString(output + File.separator + Constants.FILE_OUTPUT_ORFAN_GENES_SUMMARY);
    }

    @PostMapping("/data/summary/chart")
    @ResponseBody
    public String getSummaryChart(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        String output = OUTPUT_DIR + sessionID;
        return FileHandler.readJSONAsString(output + File.separator + Constants.FILE_OUTPUT_ORFAN_GENES_SUMMARY_CHART);
    }

    @PostMapping("/data/genes")
    @ResponseBody
    public String getOrfanGenes(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        String output = OUTPUT_DIR + sessionID;
        return FileHandler.readJSONAsString(output + File.separator + Constants.FILE_OUTPUT_ORFAN_GENES);
    }

    @PostMapping("/data/blast")
    @ResponseBody
    public String getBlast(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        final String id = (String) payload.get("id");
        String output = OUTPUT_DIR + sessionID;
        return FileHandler.blastToJSON(output + File.separator + Constants.FILE_OUTPUT_BLAST_RESULTS, id);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity saveResult(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        final String email = (String) payload.get("email");

        String metadataFilePath = OUTPUT_DIR + sessionID + File.separator + Constants.FILE_RESULT_METADATA;
        JSONObject metadata = FileHandler.getObjectFromFile(metadataFilePath);
        // Adding email to result metadata
        metadata.put("email", email);
        metadata.put("saved", true);
        // Updating metadata
        FileHandler.saveOutputFiles(metadata, metadataFilePath);

        // Adding saved data to results file
        String resultsFilePath = OUTPUT_DIR + Constants.FILE_RESULTS;
        JSONArray results;
        try {
            results = FileHandler.getArrayFromFile(resultsFilePath);
            results.add(metadata);
        } catch (NullPointerException e) {
            // Create a results file if one is not found
            results = new JSONArray();
            results.add(metadata);
        }
        FileHandler.saveOutputFiles(results, resultsFilePath);

        return new ResponseEntity<Authenticator.Success>(HttpStatus.CREATED);
    }

    @PostMapping("/results")
    @ResponseBody
    public String getResults() {
        JSONArray results = new JSONArray();

        File[] sessions = new File(OUTPUT_DIR).listFiles(File::isDirectory);
        for (File session : sessions) {
            String sessionPath = session.getPath();
            String metadataFilePath = sessionPath + File.separator + Constants.FILE_RESULT_METADATA;
            JSONObject metadata = FileHandler.getObjectFromFile(metadataFilePath);
            // Fetch result if it is marked as saved
            if (metadata != null && (boolean) metadata.get("saved")) {
                metadata.remove("saved");
                results.add(metadata);
            }
        }
        return results.toString();
    }

    @GetMapping("/download/blast/{sessionid}")
    public ResponseEntity<Resource> downloadBlast(@PathVariable String sessionid) {
        String blastResultsFilePath = OUTPUT_DIR + sessionid + File.separator + Constants.BLAST_RESULTS_FILE;
        File blastResultsFile = new File(blastResultsFilePath);
        Path path = Paths.get(blastResultsFile.getAbsolutePath());
        ByteArrayResource resource = null;
        try {
            resource = new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException e) {
            log.error("Error while downloading Blast Results from session: " + sessionid, e);
        }

        return ResponseEntity.ok()
                .contentLength(blastResultsFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @PostMapping("/clamp")
    public String clampAnalyse(@Valid @ModelAttribute("chromosome") String chromosome) {

        String outputFile = OUTPUT_DIR + Constants.FILE_OUTPUT_CLAMP;

        try {

            log.info("Selected chromosome : " + chromosome);
            // TODO: Change the script location
            List<String> command = Arrays.asList("Rscript", "script/clamp.R", "--chromosome", chromosome, "--output", outputFile);

            log.info("Executing Blast Command:{}", command.toString());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // wait until the command get executed
            if (process.waitFor() != 0) {
                throw new RuntimeException("Clamp script error occurred");
            } else {
                log.info("Clamp Analysis completed!!");
            }

        } catch (Exception e) {
            log.error("Clamp Analysis Failed: " + e.getMessage());
        }
        return "redirect:/clampresults?chromosome=" + chromosome;
    }
}
