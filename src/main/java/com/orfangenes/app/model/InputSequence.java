package com.orfangenes.app.model;

import com.orfangenes.app.util.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputSequence {

    private String organismName;
    private String accession;
    private String accessionType;
    private String sequence;
    private String maxEvalue;
    private String maxTargetSequence;
    private String identity;

    // TODO: THIS IMPLEMENTATION IS INCORRECT
    public String getType() {
        String[] sequences = this.sequence.split("\n\n");

        // Getting first sequence. All sequences do not need to be processed
        // since the input sequence should only have one type of gene.
        String sequence = sequences[0];
        String[] lines = sequence.split("\n");
        StringBuilder sequenceString = new StringBuilder();
        for (int i = 1; i < lines.length; i++) {
            sequenceString.append(lines[i]);
        }
        char[] characters = sequenceString.toString().toCharArray();
        for (char character : characters) {
            if ((character != 'A') && (character != 'T') && (character != 'C') && (character != 'G')) {
                return Constants.TYPE_PROTEIN;
            }
        }
        return Constants.TYPE_NUCLEOTIDE;
    }
}
