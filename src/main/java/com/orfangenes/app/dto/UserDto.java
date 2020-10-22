package com.orfangenes.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author Suresh Hewapathirana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String analysisId;
    private String firstName;
    private String lastName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
}
