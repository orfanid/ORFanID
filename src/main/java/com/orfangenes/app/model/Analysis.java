package com.orfangenes.app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.orfangenes.app.util.Constants;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Suresh Hewapathirana
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analysis extends AuditModel {

    private Long id;
    @NotBlank
    private String analysisId;
    private Date analysisDate;
    @NotBlank
    @Size(min = 3, max = 100)
    private String organism;
    private int taxonomyId;
    private boolean isSaved = false;
    private String blastResults;
    private int evalue;
    private int maximumTargetSequences;
    private int identity;
    private String sequenceType;
    private Constants.AnalysisStatus status;

    private User user;
    @ToString.Exclude
    private List<Gene> geneList = new ArrayList<>();

    @JsonManagedReference
    public List<Gene> getGeneList() {
        return geneList;
    }
}
