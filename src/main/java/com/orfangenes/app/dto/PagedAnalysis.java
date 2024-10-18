package com.orfangenes.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PagedAnalysis {
    private int total;
    private List<AnalysisResultsTableRaw> results;
    int totalPages;
}
