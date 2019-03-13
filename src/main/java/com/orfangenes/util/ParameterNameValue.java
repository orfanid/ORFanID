package com.orfangenes.util;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Suresh Hewapathirana
 */
@Getter
@Setter
public class ParameterNameValue {

    private final String name;
    private final String value;

    public ParameterNameValue(String name, String value)
            throws UnsupportedEncodingException
    {
        this.name = URLEncoder.encode(name, "UTF-8");
        this.value = URLEncoder.encode(value, "UTF-8");
    }
}
