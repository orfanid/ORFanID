package com.orfangenes.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** @author Suresh Hewapathirana */
@Slf4j
public class AccessionSearch {

  private static final String DEFAULT_BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
  private static final String EFETCH = "efetch.fcgi";

  public static String fetchSequenceByAccession(String sequenceType, String sequenceIds) {
    StringBuilder sequence = new StringBuilder();
    try {

      URL url =
          new URL(
              DEFAULT_BASE_URL
                  + EFETCH
                  + "?db=" + sequenceType + "&id="
                  + sequenceIds
                  + "&rettype=fasta&retmode=text");
      log.info(url.getPath());
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "text/plain");

      if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
      }

      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

      System.out.println("Output from Server .... \n");
      String line;
      while ((line = br.readLine()) != null) {
          sequence.append(line + "\n");
      }

      conn.disconnect();

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sequence.toString();
  }
}
