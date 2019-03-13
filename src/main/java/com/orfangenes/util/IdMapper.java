package com.orfangenes.util;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
public class IdMapper {

    private static final String UNIPROT_SERVER = "https://www.uniprot.org/";

    public static void run(String tool, ParameterNameValue[] params) throws Exception
    {
        StringBuilder locationBuilder = new StringBuilder(UNIPROT_SERVER + tool + "/?");
        for (int i = 0; i < params.length; i++)
        {
            if (i > 0)
                locationBuilder.append("&");
            locationBuilder.append(params[i].getName()).append('=').append(params[i].getValue());
        }
        String location = locationBuilder.toString();
        URL url = new URL(location);
        log.info("Submitting...");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects(true);
        conn.setDoInput(true);
        conn.connect();

        int status = conn.getResponseCode();
        while (true)
        {
            int wait = 0;
            String header = conn.getHeaderField("Retry-After");
            if (header != null)
                wait = Integer.valueOf(header);
            if (wait == 0)
            break;
            log.info("Waiting (" + wait + ")...");
            conn.disconnect();
            Thread.sleep(wait * 1000);
            conn = (HttpURLConnection) new URL(location).openConnection();
            conn.setDoInput(true);
            conn.connect();
            status = conn.getResponseCode();
        }

        if (status == HttpURLConnection.HTTP_OK) {
            log.info("Got a OK reply");
            InputStream reader = conn.getInputStream();
            URLConnection.guessContentTypeFromStream(reader);
            StringBuilder builder = new StringBuilder();
            int a = 0;
            while ((a = reader.read()) != -1)
            {
                builder.append((char) a);
            }
            System.out.println(builder.toString());
        } else {
        log.error("Failed, got " + conn.getResponseMessage() + " for " + location);
        }
        conn.disconnect();
    }
}
