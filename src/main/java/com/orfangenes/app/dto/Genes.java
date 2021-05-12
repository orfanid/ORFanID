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
@NoArgsConstructor
@AllArgsConstructor
public class Genes {
    private String geneId;
    private String sequence;
    private String description;
    private double gccontent;
    private int length;
    private String orfanLevel;
    private String organism;
    private int taxonomyId;
    private String analysisId;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date analysisDate;
}
