package com.orfangenes.model.taxonomy;

import static com.orfangenes.util.Constants.*;

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

    @Override
    public String toString() {
        String node;
        if (name.equals(NOT_AVAILABLE)) {
            node = ANSI_RED + "N/A" + "\t" + ANSI_RESET + "|";
        } else {
            node = name + "\t|";
        }
        return node;
    }
}
