package com.orfangenes.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * @author Suresh Hewapathirana
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gene extends AuditModel implements Serializable {

    private Long id;
    private String geneId;
    private String sequence;
    private String description;
    private String orfanLevel;
    private int taxonomyId;
    private double gccontent = 0.00;
    private int length;
    private int startLocation;
    private int endLocation;

    private Analysis analysis;

    @JsonBackReference
    public Analysis getAnalysis() {
        return analysis;
    }
}
