package com.orfangenes.controllers;

import com.orfangenes.ORFanGenes;
import com.orfangenes.model.entities.InputSequence;
import com.orfangenes.util.AccessionSearch;
import com.orfangenes.util.FileHandler;
import com.orfangenes.util.UniprotSequenceSearch;
import com.sun.net.httpserver.Authenticator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;

import javax.validation.Valid;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.orfangenes.util.Constants.*;

@Slf4j
@Controller
public class InternalController {

    @Value("${data.outputdir}")
    private String outputdir;

    @PostMapping("/analyse")
    public String analyse(@Valid @ModelAttribute("sequence") InputSequence sequence, BindingResult result, Model model) {

        Assert.assertFalse("Error", result.hasErrors());
        final String sessionID = System.currentTimeMillis() + "_" + RandomStringUtils.randomAlphanumeric(3);
        String outputPath = outputdir + sessionID;
        String inputFilePath = outputPath + "/" + INPUT_FASTA;

        String organismTaxID = sequence.getOrganismName().split("\\(")[1];
        organismTaxID = organismTaxID.substring(0, organismTaxID.length() - 1);
        int organismTax = Integer.parseInt(organismTaxID);
        try {
            FileHandler.createResultsOutputDir(outputPath);
            FileHandler.saveInputSequence(outputPath, sequence);
            FileHandler.saveResultMetadata(outputPath, sequence, sessionID);
            ORFanGenes.run(
                    inputFilePath,
                    outputPath,
                    organismTax,
                    sequence.getType(),
                    sequence.getMaxTargetSequence(),
                    sequence.getMaxEvalue());
        } catch (Exception e) {
            log.error("Analysis Failed: " + e.getMessage());
        }
        return "redirect:/result?sessionid=" + sessionID;
    }

    @PostMapping("/data/summary")
    @ResponseBody
    public String getSummary(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        String output = outputdir + sessionID;
        return FileHandler.readJSONAsString(output + "/" + FILE_OUTPUT_ORFAN_GENES_SUMMARY);
    }

    @PostMapping("/data/summary/chart")
    @ResponseBody
    public String getSummaryChart(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        String output = outputdir + sessionID;
        return FileHandler.readJSONAsString(output + "/" + FILE_OUTPUT_ORFAN_GENES_SUMMARY_CHART);
    }

    @PostMapping("/data/genes")
    @ResponseBody
    public String getOrfanGenes(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        String output = outputdir + sessionID;
        return FileHandler.readJSONAsString(output + "/" + FILE_OUTPUT_ORFAN_GENES);
    }

    @PostMapping("/data/blast")
    @ResponseBody
    public String getBlast(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        final String id = (String) payload.get("id");
        String output = outputdir + sessionID;
        return FileHandler.blastToJSON(output + "/" + FILE_OUTPUT_BLAST_RESULTS, id);
    }

    @PostMapping("/search/sequence")
    @ResponseBody
    public String getSequenceById(@RequestBody Map<String, Object> payload) {
        final String sequenceType = (String) payload.get("sequencetype");
        final String sequenceIds = (String) payload.get("sequenceids");
        log.info("sequenceType : " + sequenceType);
        log.info("sequenceIds : " + sequenceIds);
        final String sequence = AccessionSearch.fetchSequenceByAccession(sequenceType, sequenceIds);
        return sequence;
    }


//    @PostMapping("/sequence_info")
    @RequestMapping("/sequenceinfo")
    public String getUniprotEntryById(Model model) {
//        String accession = (String) payload.get("accession");
//        log.info("accession : " + accession);
//        accession = "P12345";
//        Set<String> accessions = new HashSet<>(Arrays.asList(accession));
//        QueryResult<UniProtEntry> results = UniprotSequenceSearch.SearchSequence(accessions);
//        return results.getFirstResult();
//        return new ModelAndView("sequence_info", "sequence_info", results.getFirstResult());
        model.addAttribute("sequenceinfo", "Suresh");
        return "sequenceinfo";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity saveResult(@RequestBody Map<String, Object> payload) {
        final String sessionID = (String) payload.get("sessionid");
        final String email = (String) payload.get("email");

        String metadataFilePath = outputdir + sessionID + "/" + FILE_RESULT_METADATA;
        JSONObject metadata = FileHandler.getObjectFromFile(metadataFilePath);
        // Adding email to result metadata
        metadata.put("email", email);
        metadata.put("saved", true);
        // Updating metadata
        FileHandler.saveOutputFiles(metadata, metadataFilePath);

        // Adding saved data to results file
        String resultsFilePath = outputdir + FILE_RESULTS;
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

        File[] sessions = new File(outputdir).listFiles(File::isDirectory);
        for (File session : sessions) {
            String sessionPath = session.getPath();
            String metadataFilePath = sessionPath + "/" + FILE_RESULT_METADATA;
            JSONObject metadata = FileHandler.getObjectFromFile(metadataFilePath);
            // Fetch result if it is marked as saved
            if (metadata != null && (boolean) metadata.get("saved")) {
                metadata.remove("saved");
                results.add(metadata);
            }
        }
        return results.toString();
    }
}
