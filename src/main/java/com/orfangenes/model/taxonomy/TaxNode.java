package com.orfangenes.model.taxonomy;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class TaxNode implements Serializable {

    private int nID;
    private String name;
    private String nRank;
    private TaxNode parent;
    private Set<TaxNode> children;

    public TaxNode(int nID, String name, String nRank) {
        this.nID = nID;
        this.name = name;
        this.nRank = nRank;
        this.children = new LinkedHashSet<>();
    }

    public void setParent(TaxNode parent) {
        this.parent = parent;
    }

    public TaxNode getParent() {
        return parent;
    }

    public String getRank() {
        return nRank;
    }

    public int getID() {
        return nID;
    }

    public String getName() {
        return name;
    }

    public void addChild(TaxNode node) {
        children.add(node);
    }

    public Set<TaxNode> getChildren() {
        return children;
    }
}