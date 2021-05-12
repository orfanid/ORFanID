package com.orfangenes.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Suresh Hewapathirana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResultsTableRaw {

    private String analysisId;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date analysisDate;
    private String organism;
    private String email;
    private int numberOfGenes;
}