package com.orfangenes.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.orfangenes.app.util.Constants;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private Date analysisDate;
    private String organism;
    private String email;
    private int numberOfGenes;
    private Constants.AnalysisStatus status;
    private String executionType;
}
