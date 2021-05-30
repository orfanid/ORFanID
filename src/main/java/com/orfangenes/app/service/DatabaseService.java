package com.orfangenes.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orfangenes.app.util.RestCall;
import com.orfangenes.app.util.Utils;
import com.orfangenes.app.model.Analysis;
import com.orfangenes.app.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
public class DatabaseService {

    private final ObjectMapper objectMapper = Utils.getJacksonObjectMapper();
    RestCall restCall;

    public DatabaseService(RestCall restCall) {
        this.restCall = restCall;
    }

    /** =========================== ANALYSIS =========================== */

    public String getOrfanbaseGenes() {
        String url = "analysis/orfanbase-table";
        return restCall.sendGetRequestWithRetry(url, null, null);
    }

    public Analysis getAnalysisById(String analysisId) throws IOException {
        MultiValueMap<String, String> queryParams =  new LinkedMultiValueMap<>();;
        queryParams.add("analysisId" , analysisId);
        String response = restCall.sendGetRequestWithRetry("analysis/analysis", null, queryParams);
        if(response !=null){
            return (Analysis) this.objectMapper.readValue(response, Analysis.class);
        }else{
            return null;
        }
    }

    public String getAnalysisJsonById(String analysisId) {
        MultiValueMap<String, String> queryParams =  new LinkedMultiValueMap<>();;
        queryParams.add("analysisId" , analysisId);
        String response = restCall.sendGetRequestWithRetry("analysis/analysis", null, queryParams);
        return response;
    }

    public Analysis saveAnalysis(Analysis analysis) throws IOException {
        String url = "/analysis/analysis";
        Long id = analysis.getId();
        if (id == null) {
            id = -1L;
            analysis.setId(id);
        }

        String payload = this.objectMapper.writeValueAsString(analysis);
        payload = payload.replaceAll("\"analysis\":null", "\"analysis\":" + id);
        payload = payload.replaceAll("\"analysis\": null", "\"analysis\":" + id);
        log.info("Saving analysis : " + payload);
        String response = this.restCall.sendPostRequest(url, payload);
        return (Analysis) this.objectMapper.readValue(response, Analysis.class);
    }

    /** =========================== GENE =========================== */

    public String getAllAnalysis() {
        String url = "analysis/analyses/all-analysis-table";
        return restCall.sendGetRequestWithRetry(url, null, null);
    }

    /** =========================== USER =========================== */

    public User getUserByEmail(String email) throws IOException {
        MultiValueMap<String, String> queryParams =  new LinkedMultiValueMap<>();;
        queryParams.add("email" , email);
        String response = restCall.sendGetRequestWithRetry("user", null, queryParams);
        if(response !=null){
            return (User) this.objectMapper.readValue(response, User.class);
        }else{
            return null;
        }
    }

    public User saveUser(User user) throws IOException {
        String url = "/user";
        Long id = user.getId();
        if (id == null) {
            id = -1L;
            user.setId(id);
        }

        String payload = this.objectMapper.writeValueAsString(user);
        log.info("Saving user : " + payload);
        String response = this.restCall.sendPostRequest(url, payload);
        return (User) this.objectMapper.readValue(response, User.class);
    }

    public String getDataSummary(String analysisId) {
        String url = "analysis/data/summary/" + analysisId;
        return restCall.sendGetRequestWithRetry(url, null, null);
    }

    public String getDataSummaryChart(String analysisId) {
        String url = "analysis/data/summary-chart/" + analysisId;
        return restCall.sendGetRequestWithRetry(url, null, null);
    }

    public String getDataGeneList(String analysisId) {
        String url = "analysis/data/genes/" + analysisId;
        return restCall.sendGetRequestWithRetry(url, null, null);
    }

    public String getDataBlastResults(String analysisId) {
        String url = "analysis/data/blastresults/" + analysisId;
        return restCall.sendGetRequestWithRetry(url, null, null);
    }

    public void savePendingAnalysis(Analysis analysis) throws JsonProcessingException {
        String url = "analysis/pending";
        String payload = this.objectMapper.writeValueAsString(analysis);
        restCall.sendPostRequest(url, payload);
    }

    public void update(Analysis analysis) throws JsonProcessingException {
        String url = "analysis/update";
        String payload = this.objectMapper.writeValueAsString(analysis);
        restCall.sendPostRequest(url, payload);
    }
}