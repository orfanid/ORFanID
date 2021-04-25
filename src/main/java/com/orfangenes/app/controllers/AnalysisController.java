package com.orfangenes.app.controllers;

import com.orfangenes.app.util.AccessionSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
@RestController
@CrossOrigin
public class AnalysisController {

    @GetMapping("/validate/accessions")
    public String validateAccessions(@RequestParam("accessions") String accessions, @RequestParam("accessionType") String accessionType) {
        String isValid = "Valid";
        log.info("validating accessions..");
        if(accessions != null && accessions.length()>1){
            String[] accessionList = accessions.split(",");
            for (String accession: accessionList) {
                try {
                    String geneSequence = AccessionSearch.fetchSequenceByAccession(accessionType, accession);
                } catch (Exception e) {
                    isValid = accession;
                    log.error("Error occurred while retrieving sequence for " + accession + ". Please check the accessions or the database type");
                    break;
                }
            }
        }else{
            isValid = "Invalid";
            log.error("No accession found");
        }
        log.info("validating accessions..completed!");
        return isValid;
    }
}
