package com.orfangenes.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Suresh Hewapathirana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryChart {
    String[] x;
    Integer[] y;
}
