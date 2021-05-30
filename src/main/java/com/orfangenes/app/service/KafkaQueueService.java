package com.orfangenes.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orfangenes.app.ORFanGenes;
import com.orfangenes.app.model.Analysis;
import com.orfangenes.app.util.Constants;
import com.orfangenes.app.util.FileHandler;
import com.orfangenes.app.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class KafkaQueueService implements QueueService {

    @Autowired
    @Qualifier("com.orfangenes.app.kafka.kafkaTemplate")
    private KafkaTemplate<String, String> template;

    @Autowired
    ORFanGenes orFanGenes;

    @Value("${app.dir.root}")
    private String APP_DIR;

    @Value("${data.outputdir}")
    private String OUTPUT_DIR;

    private final ObjectMapper objectMapper = Utils.getJacksonObjectMapper();


    @Override
    public void sendToQueue(Analysis analysis) throws JsonProcessingException {
        String analysisObj = objectMapper.writeValueAsString(analysis);
        template.send("analysis", analysisObj);
    }

    @Override
    @KafkaListener(id = "analysis-group", topics = "analysis", containerFactory = "com.orfangenes.app.kafka.kafkaListenerContainerFactory")
    public void processAnalysis(String analysisObj) throws JsonProcessingException {
        log.info("## Received queued message" + analysisObj);

        Analysis analysis = objectMapper.readValue(analysisObj, Analysis.class);

        OUTPUT_DIR = (OUTPUT_DIR.endsWith("/"))? OUTPUT_DIR : OUTPUT_DIR + File.separator;
        String analysisDir = OUTPUT_DIR + analysis.getAnalysisId();
        log.info("########### analysis  Dir: " + analysisDir);
        String inputFastaFile = analysisDir + File.separator + Constants.INPUT_FASTA;

        try {
            orFanGenes.run(
                    inputFastaFile,
                    analysisDir,
                    analysis,
                    APP_DIR);
        } catch (Exception e) {
            log.error("Analysis Failed: " + e.getMessage());
        }
    }
}
