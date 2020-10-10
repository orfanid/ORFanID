package com.orfangenes.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Suresh Hewapathirana
 */

@Getter
@Setter
public abstract class AuditModel implements Serializable {

    @JsonIgnore
    private Date createdAt;

    @JsonIgnore
    private Date updatedAt;
}
