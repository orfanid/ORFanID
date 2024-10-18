package com.orfangenes.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedAnalysis {
    private int total;
    private List<AnalysisResultsTableRaw> results;
    int totalPages;
}
