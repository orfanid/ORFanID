package com.orfangenes.app.controllers;

import com.orfangenes.app.ORFanGenes;
import com.orfangenes.app.dto.UserDto;
import com.orfangenes.app.model.InputSequence;
import com.orfangenes.app.service.DatabaseService;
import com.orfangenes.app.util.Constants;
import com.orfangenes.app.util.FileHandler;
import com.orfangenes.app.model.Analysis;
import com.orfangenes.app.model.User;
import com.sun.net.httpserver.Authenticator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    DatabaseService databaseService;

    @Autowired
    ORFanGenes orFanGenes;

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

        Analysis analysis = new Analysis();
        analysis.setAnalysisId(sessionID);
        analysis.setTaxonomyId(organismTax);
        analysis.setOrganism(sequence.getOrganismName().split("\\(")[0]);
        analysis.setEvalue(Integer.parseInt(sequence.getMaxEvalue()));
        analysis.setMaximumTargetSequences(Integer.parseInt(sequence.getMaxTargetSequence()));
        analysis.setIdentity(Integer.parseInt(sequence.getIdentity()));
        analysis.setSequenceType(sequence.getAccessionType());

        try {
            FileHandler.createResultsOutputDir(analysisDir);
            FileHandler.saveInputSequence(analysisDir, sequence);

            orFanGenes.run(
                    inputFastaFile,
                    analysisDir,
                    analysis,
                    APP_DIR);
        } catch (Exception e) {
            log.error("Analysis Failed: " + e.getMessage());
        }
        return "redirect:/result?sessionid=" + sessionID;
    }

    @PostMapping("/data/summary")
    @ResponseBody
    public String getAnalysisDataSummary(@RequestBody Map<String, Object> payload) {
        final String analysisId = (String) payload.get("sessionid");
        return databaseService.getDataSummary(analysisId);
    }

    @PostMapping("/data/summary/chart")
    @ResponseBody
    public String getAnalysisDataSummaryChart(@RequestBody Map<String, Object> payload) {
        final String analysisId = (String) payload.get("sessionid");
        return databaseService.getDataSummaryChart(analysisId);
    }

    @PostMapping("/data/genes")
    @ResponseBody
    public String getAnalysisDataGenesList(@RequestBody Map<String, Object> payload) {
        final String analysisId = (String) payload.get("sessionid");
        return databaseService.getDataGeneList(analysisId);
    }

    @PostMapping("/data/analysis")
    @ResponseBody
    public String getAnalysisData(@RequestBody Map<String, Object> payload) throws IOException {
        final String analysisId = (String) payload.get("sessionid");
        return databaseService.getAnalysisJsonById(analysisId);
    }

    @PostMapping("/data/blast")
    @ResponseBody
    public String getBlast(@RequestBody Map<String, Object> payload) {
        final String analysisId = (String) payload.get("sessionid");
        final String geneid = (String) payload.get("geneid");
        String blastResults = databaseService.getDataBlastResults(analysisId);
        return FileHandler.blastToJSON(blastResults, geneid);
    }

    @PostMapping("/save")
    public String saveResult(@Valid @ModelAttribute("user") UserDto userFromForm, BindingResult result, Model model) throws Exception {
        final String analysisId = userFromForm.getAnalysisId();
        final String firstName = userFromForm.getFirstName();
        final String lastname = userFromForm.getLastName();
        final String email = userFromForm.getEmail();

        Analysis analysis = databaseService.getAnalysisById(analysisId);
        if(analysis !=null){
            User user = databaseService.getUserByEmail(email);
            if(user == null){
                user = new User();
                user.setId(-1l);
                user.setFirstName(firstName);
                user.setLastName(lastname);
                user.setEmail(email);
            }
            analysis.setUser(user);
            analysis.setSaved(true);
            databaseService.saveAnalysis(analysis);
        }else{
            throw new Exception("Analysis not found for Analysis ID : " + analysisId);
        }
        System.out.println("Data updated!");
       return "redirect:/orfanbase";
    }

    @PostMapping("/orfanbase-genes")
    @ResponseBody
    public String getOrfanbaseGenes() {
        return databaseService.getOrfanbaseGenes();
    }

    @PostMapping("/all-analysis")
    @ResponseBody
    public String getAllAnalysis() {
        return databaseService.getAllAnalysis();
    }

    @GetMapping("/download/blast/{sessionid}")
    public ResponseEntity<Resource> downloadBlast(@PathVariable String sessionid) {
        OUTPUT_DIR = (OUTPUT_DIR.endsWith("/"))? OUTPUT_DIR : OUTPUT_DIR + File.separator;
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

        OUTPUT_DIR = (OUTPUT_DIR.endsWith("/"))? OUTPUT_DIR : OUTPUT_DIR + File.separator;
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
        return "redirect:/clampresults?chromosome=" + "all"; // todo hard coded
    }

    @GetMapping("test-api")
    @ResponseBody
    public String testAPI() throws IOException {
        return databaseService.getUserByEmail("orfanid@gmail.com").toString();
    }
}
