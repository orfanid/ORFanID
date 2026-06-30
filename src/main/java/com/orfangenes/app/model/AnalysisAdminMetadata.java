package com.orfangenes.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisAdminMetadata {

    private String analysisId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submittedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishedAt;
    private Long durationSeconds;
    private Long queueDurationSeconds;
    private String submittedInput;
    private String inputType;
    private Integer inputSequenceCount;
    private Integer minInputLength;
    private Integer maxInputLength;
    private Integer averageInputLength;
    private String normalizedFastaPreview;
    private String validationWarnings;
    private String errorMessage;
    private String program;
    private Integer numIterations;
    private String serverNode;
}
