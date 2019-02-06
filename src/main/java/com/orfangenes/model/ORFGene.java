package com.orfangenes.model;

import lombok.Getter;

@Getter
public class ORFGene {
    private String id;
    private String description;
    private String level;
    private String taxonomy;

    public ORFGene(Gene gene, String level) {
        this.id = gene.getGeneID();
        this.description = gene.getDescription();
        this.level = level;
        this.taxonomy = "species";
    }
}
