package com.orfangenes.app.model.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Suresh Hewapathirana
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankedLineage {
    int taxonomyId;
    List<TaxNode> lineage;
}
