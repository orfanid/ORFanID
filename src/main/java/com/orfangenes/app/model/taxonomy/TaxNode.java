package com.orfangenes.app.model.taxonomy;

import com.orfangenes.app.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
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
    private int nodeCount;

    @Override
    public String toString() {
        String node;
        if (name.equals(Constants.NOT_AVAILABLE)) {
            node = Constants.ANSI_RED + "N/A" + "\t" + Constants.ANSI_RESET + "|";
        } else {
            node = name + "\t|";
        }
        return node;
    }
}
