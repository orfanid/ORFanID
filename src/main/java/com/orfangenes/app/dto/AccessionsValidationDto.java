package com.orfangenes.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccessionsValidationDto {
    private Boolean isValid;
    private List<String> invalidAccessions;
}
