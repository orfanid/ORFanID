package com.orfangenes.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gene {
    private String geneID;
    private String sequence;
    private String description;
    private int taxID;
}
