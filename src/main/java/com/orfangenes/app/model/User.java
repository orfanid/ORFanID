package com.orfangenes.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author Suresh Hewapathirana
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AuditModel {

    private Long id;
    private String firstName;
    private String lastName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
}
