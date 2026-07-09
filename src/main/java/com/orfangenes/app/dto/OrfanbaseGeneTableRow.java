package com.orfangenes.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrfanbaseGeneTableRow {
    private String geneId;
    private String description;
    private double gccontent;
    private int length;
    private String orfanLevel;
    private String organism;
    private int taxonomyId;
    private String analysisId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private Date analysisDate;
}
