package com.orfangenes.app.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * This class handles all the GET, POST, PUT, DELETE requests to the API
 */
@Slf4j
public class RestCall {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKeyName;
    private final String apiKeyValue;
    private final String appName;

    RestCall(String baseUrl, String apiKeyName, String apiKeyValue, String appName) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKeyName = apiKeyName;
        this.apiKeyValue = apiKeyValue;
        this.appName = appName;
    }

    public String sendPostRequest(String url, String payload) {
        url = baseUrl + url;
        ResponseEntity<String> response;
        try {
            //  create headers
            HttpHeaders headers = createHeaders();

            // build the request
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(payload, headers);

            log.info("POST Request : " + url);
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                String errorMessage = "[POST] Received invalid response for : " + url + " : " + response;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return response.getBody();
    }

    public String sendDeleteRequest(String url, String payload) {
        url = baseUrl + url;
        ResponseEntity<String> response;
        try {
            //  create headers
            HttpHeaders headers = createHeaders();

            // build the request
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(payload, headers);

            log.info("DELETE Request : " + url);
            response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                String errorMessage = "[DELETE] Received invalid response for : " + url + " : " + response;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return response.getBody();
    }

    /**
     * This method sets HTTP headers, perform the rest call and returns results in String format
     *
     * @param uri constructed URL with URI and query parameters
     * @return
     */
    private String makeGetRequest(URI uri) {
        ResponseEntity<String> response;
        try {
            //  create headers
            HttpHeaders headers = createHeaders();

            // build the request
            HttpEntity entity = new HttpEntity(headers);

            log.info("GET Request : " + uri);
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                String errorMessage = "[GET] Received invalid response for : " + uri + " : " + response;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return response.getBody();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(apiKeyName, apiKeyValue);
        headers.set("app", appName);
        return headers;
    }
}
