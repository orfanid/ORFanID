package com.orfangenes.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orfangenes.app.model.Analysis;

public interface QueueService {
    void sendToQueue(Analysis analysis) throws JsonProcessingException;
    void processAnalysis(String analysisObj) throws JsonProcessingException;
}
