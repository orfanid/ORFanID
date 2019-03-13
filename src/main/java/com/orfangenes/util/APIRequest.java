package com.orfangenes.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Suresh Hewapathirana
 */
public class APIRequest {

    public static void run(String accessions) throws Exception {
        String requestURL = "https://www.ebi.ac.uk/proteins/api/proteins/" + accessions ;
        URL url = new URL(requestURL);
        System.out.println(url);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection)connection;

        httpConnection.setRequestProperty("Accept", "application/json");


        InputStream response = connection.getInputStream();
        int responseCode = httpConnection.getResponseCode();

        if(responseCode != 200) {
            throw new RuntimeException("Response code was not 200. Detected response was "+responseCode);
        }

        String output;
        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            output = builder.toString();
        }
        finally {
            if (reader != null) try {
                reader.close();
            } catch (IOException logOrIgnore) {
                logOrIgnore.printStackTrace();
            }
        }

//        System.out.println(output);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(output);
        System.out.println(rootNode.get("accession"));
        System.out.println(rootNode.get("sequence"));
        for (JsonNode feature :rootNode.get("features")) {
            System.out.println(feature);
        }
    }
}
