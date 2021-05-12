package com.orfangenes.app.controllers;

import com.orfangenes.app.dto.AccessionsValidationDto;
import com.orfangenes.app.util.AccessionSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
@RestController
@CrossOrigin
public class AnalysisController {

    @GetMapping("/validate/accessions")
    public AccessionsValidationDto validateAccessions(@RequestParam("accessions") String accessions, @RequestParam("accessionType") String accessionType) {
        AccessionsValidationDto accessionsValidationDto = new AccessionsValidationDto();
        log.info("validating accessions..");
        List<String> invalidAccessions = new ArrayList<>();
        if(accessions != null && accessions.length()>1){
            String[] accessionList = accessions.split(",");
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
}
