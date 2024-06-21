package com.orfangenes.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orfangenes.app.ORFanGenes;
import com.orfangenes.app.dto.*;
import com.orfangenes.app.model.InputSequence;
import com.orfangenes.app.service.*;
import com.orfangenes.app.service.validator.FastaValidator.*;
import com.orfangenes.app.util.*;
import com.orfangenes.app.model.Analysis;
import com.orfangenes.app.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.orfangenes.app.util.Constants.*;

@Slf4j
@RestController
public class Controller {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    ORFanGenes orFanGenes;

    @Autowired
    QueueService queueService;

    @Autowired
    ValidationService validationService;

    @Autowired
    private AnalysisService analysisService;

    private final ObjectMapper objectMapper = Utils.getJacksonObjectMapper();


    @Value("${data.outputdir}")
    private String OUTPUT_DIR;

    @PostMapping("/validate-sequence")
    public Map<String, String> validateSequence(@RequestBody Map<String, String> input) {
        try {
            FastaValidator FV=new FastaValidator(new FastaCallbackHandler());
            FV.setSequencetype(FastaValidator.Sequencetype.valueOf(input.get("type")));
            return FV.validate(input.get("sequence"));
        } catch (java.io.IOException e) //io error
        {
            System.out.println("ERROR: "+e.getMessage());
            e.printStackTrace();
        }
        catch (InvalidCharacterException e) //invalid character(s) found in fasta file
        {
            System.out.println("ERROR: "+e.getMessage()+" (line: "+e.getLine()+", char: "+e.getCharacter()+")");
        }
        catch (FastaFormatException e) //file not in fasta format
        {
            System.out.println("ERROR: "+e.getMessage()+" (line: "+e.getLine()+")");
        }
        catch (FastaHandlingException e)//error from callback methods; thrown by user
        {
            System.out.println("ERROR: "+e.getMessage());
        } catch (FastaValidatorException e) {
            System.out.println("ERROR: "+e.getMessage());
        }
        Map<String,String> result=new HashMap<>();
        result.put("status","FAILED");
        return result;
    }

    @DeleteMapping("/analysis/delete/{id}")
    public void deleteAnalysis(@PathVariable String id) throws Exception {
        analysisService.deleteAnalysis(id);
    }

    @GetMapping("validate-organism/{organismName}")
    public Map<String, Boolean> isValidOrganism(@PathVariable String organismName) throws IOException {
        return validationService.validate(organismName);
    }

    @PostMapping("analyse/list")
    public List<String> analyseList(@RequestBody List<InputSequence> sequences) throws Exception {
        List<String> analysisIdList = new ArrayList<>();
        for (InputSequence sequence : sequences) {
            String savedAnalysisId = analyse(sequence);
            analysisIdList.add(savedAnalysisId);
        }
        return analysisIdList;
    }

    @PostMapping("/analyse")
    public String analyse(@RequestBody InputSequence sequence) throws Exception {

        log.info("Analysis started....");
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
        analysis.setAnalysisDate(new Date());
        analysis.setIsPsiBlast(sequence.getIsPsiBlast());
        analysis.setNumIteration(sequence.getNum_iteration());
        analysis.setExecutionType(sequence.getExecutionType() == null ? "blast" : sequence.getExecutionType());

        User user;
        if (sequence.getEmail() == null) {
            user = databaseService.getUserByEmail(EMAIL);
            if (user == null) {
                user = new User();
                user.setFirstName(FIRST_NAME);
                user.setLastName(LAST_NAME);
                user.setEmail(EMAIL);
                user = databaseService.saveUser(user);
            }
        } else {
            user = databaseService.getUserByEmail(sequence.getEmail());
            if(user == null){
                user = new User();
                user.setFirstName(sequence.getFirstName());
                user.setLastName(sequence.getLastName());
                user.setEmail(sequence.getEmail());

                user = databaseService.saveUser(user);
            }
        }
        analysis.setUser(user);
        databaseService.savePendingAnalysis(analysis);

        try {
            FileHandler.createResultsOutputDir(analysisDir);
            FileHandler.saveInputSequence(analysisDir, sequence);
        } catch (Exception e) {
            log.error("Analysis Failed: " + e.getMessage());
        }
        queueService.sendToQueue(analysis);
        return sessionID;
    }

    @GetMapping("/kill/{analysisId}")
    public void killProcess(@PathVariable String analysisId) {
        ProcessHolder.killProcess(analysisId);
    }

    @GetMapping("/analysis/cancel/{analysisId}")
    public void cancelAnalysis(@PathVariable String analysisId) {
        databaseService.cancelAnalysis(analysisId);
    }

    @PostMapping("/data/summary")
    public List<GeneSummary> getAnalysisDataSummary(@RequestBody SessionDto sessionDto) throws Exception{
        final String analysisId = sessionDto.getSessionId();
        TypeReference<List<GeneSummary>> typeRef = new TypeReference<List<GeneSummary>>() {};
        return objectMapper.readValue(databaseService.getDataSummary(analysisId), typeRef);
    }

    @PostMapping("/data/summary/chart")
    public SummaryChart getAnalysisDataSummaryChart(@RequestBody SessionDto sessionDto) throws Exception{
        final String analysisId = sessionDto.getSessionId();
        return objectMapper.readValue(databaseService.getDataSummaryChart(analysisId), SummaryChart.class);
    }

    @PostMapping("/data/genes")
    public List<ORFanGenes> getAnalysisDataGenesList(@RequestBody SessionDto sessionDto) throws Exception{
        final String analysisId = sessionDto.getSessionId();
        TypeReference<List<ORFanGenes>> typeRef = new TypeReference<List<ORFanGenes>>() {};
        return objectMapper.readValue(databaseService.getDataGeneList(analysisId), typeRef);
    }

    @PostMapping("/data/analysis")
    public Analysis getAnalysisData(@RequestBody SessionDto sessionDto) throws IOException {
        final String analysisId = sessionDto.getSessionId();
        return objectMapper.readValue(databaseService.getAnalysisJsonById(analysisId), Analysis.class);
    }

    @PostMapping("/data/blast")
    public String getBlast(@RequestBody SessionGeneDto sessionGeneDto) {
        final String analysisId = sessionGeneDto.getSessionId();
        final String geneid = sessionGeneDto.getGeneId();
        String blastResults = databaseService.getDataBlastResults(analysisId);
        return FileHandler.blastToJSON(blastResults, geneid);
    }

    @PostMapping("/save")
    public void saveResult(@Valid @RequestBody UserDto userFromForm) throws Exception {
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
    }

    @PostMapping("/orfanbase-genes")
    public List<Genes> getOrfanbaseGenes() throws Exception {
        TypeReference<List<Genes>> typeRef = new TypeReference<List<Genes>>() {};
        return objectMapper.readValue(databaseService.getOrfanbaseGenes(), typeRef).stream()
                .filter(genes -> genes.getGeneId() != null)
                .collect(Collectors.toList());
    }

    @GetMapping("/orfanbase-genes/paged")
    public PagedResults<Genes> getOrfanbaseGenesPaged(@RequestParam(name = "page", defaultValue = "0") String page,
                                              @RequestParam(name = "size", defaultValue = "10") String size) throws Exception {
        TypeReference<PagedResults<Genes>> typeRef = new TypeReference<PagedResults<Genes>>() {};
        return objectMapper.readValue(databaseService.getOrfanbaseGenesPaged(page, size), typeRef);
    }

    @PostMapping("/all-analysis")
    public List<AnalysisResultsTableRaw> getAllAnalysis() throws Exception{
        TypeReference<List<AnalysisResultsTableRaw>> typeRef = new TypeReference<List<AnalysisResultsTableRaw>>() {};
        return objectMapper.readValue(databaseService.getAllAnalysis(), typeRef).stream()
                .filter(analysisResultsTableRaw -> !AnalysisStatus.CANCELLED.equals(analysisResultsTableRaw.getStatus()))
                .collect(Collectors.toList());
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
    public void clampAnalyse(@RequestBody String chromosome) {

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
    }

    @GetMapping("/validate/accessions")
    public AccessionsValidationDto validateAccessions(@RequestParam("accessions") String accessions, @RequestParam("accessionType") String accessionType) {
        AccessionsValidationDto accessionsValidationDto = new AccessionsValidationDto();
        log.info("validating accessions..");
        List<String> invalidAccessions = new ArrayList<>();
        if(accessions != null && accessions.length()>1){
            String[] accessionList = accessions.split("[\\s,]+");
            for (String accession: accessionList) {
                try {
                    String geneSequence = AccessionSearch.fetchSequenceByAccession(accessionType, accession);
                } catch (Exception e) {
                    invalidAccessions.add(accession);
                    log.error("Error occurred while retrieving sequence for " + accession + ". Please check the accessions or the database type");
                }
            }
        }
        accessionsValidationDto.setInvalidAccessions(invalidAccessions);
        if (invalidAccessions.isEmpty()) {
            accessionsValidationDto.setIsValid(true);
        } else {
            accessionsValidationDto.setIsValid(false);
        }
        log.info("validating accessions..completed!");
        return accessionsValidationDto;
    }

    @GetMapping("test-api")
    @ResponseBody
    public String testAPI() throws IOException {
        return databaseService.getUserByEmail("orfanid@gmail.com").toString();
    }
}
