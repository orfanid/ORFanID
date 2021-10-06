package com.orfangenes.app.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class ValidationService {

    public Map<String, Boolean>  validate(String organismName) throws IOException {
        Map<String, Boolean> map = new HashMap<>();
        map.put("isValid", false);

        File file = new File("/new_taxdump/organisms.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null){
            if (st.equalsIgnoreCase(organismName)) {
                map.put("isValid", true);
                return map;
            }
        }

        return map;
    }
}
