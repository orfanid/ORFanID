package com.orfangenes.model.taxonomy;

import static com.orfangenes.util.Constants.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxNode implements Serializable {

  private int nID;
  private String name;
  private String nRank;
  private TaxNode parent;
  private Set<TaxNode> children;

//  public TaxNode(int nID, String name, String nRank) {
//    this.nID = nID;
//    this.name = name;
//    this.nRank = nRank;
//    this.children = new LinkedHashSet<>();
//  }


    //    public TaxNode getParent() {
    //    }
    //    public void setParent(TaxNode parent) {
    //        return children;
    //    }
    //    public Set<TaxNode> getChildren() {
    //
    //        children.add(node);
    //    public void addChild(TaxNode node) {
    //
    //    }
    //        return name;
    //    public String getName() {
    //
    //    }
    //        return nID;
    //    public int getID() {
    //
    //    }
    //        return nRank;
    //    public String getRank() {
    //
    //    }
    //        return parent;
    //
    //    }
    //        this.parent = parent;
    @Override
    public String toString() {
        String node;
        if (nID == -1) {
            node = ANSI_RED + "*****" + "->" + "N/A" + "(" + nRank + ")\t" + ANSI_RESET + "|";
        } else {
            node = nID + "->" + name + "(" + nRank + ")\t|";
        }
        return node;
    }
}
