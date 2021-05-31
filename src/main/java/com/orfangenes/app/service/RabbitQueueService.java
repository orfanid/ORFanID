package com.orfangenes.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orfangenes.app.ORFanGenes;
import com.orfangenes.app.model.Analysis;
import com.orfangenes.app.util.Constants;
import com.orfangenes.app.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
@Qualifier("rabbit")
public class RabbitQueueService implements QueueService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    ORFanGenes orFanGenes;

    @Value("${app.dir.root}")
    private String APP_DIR;

    @Value("${data.outputdir}")
    private String OUTPUT_DIR;

    private final ObjectMapper objectMapper = Utils.getJacksonObjectMapper();


    public void sendToQueue(Analysis analysis) throws JsonProcessingException {
        String analysisObj = objectMapper.writeValueAsString(analysis);
        Message message = MessageBuilder
                .withBody(analysisObj.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();
        rabbitTemplate.send("analysis", message);
    }

    @RabbitListener(queues = "analysis")
    public void processAnalysis(String analysisObj) throws JsonProcessingException {
        log.info("## Received queued message" + analysisObj);

        Analysis analysis = objectMapper.readValue(analysisObj, Analysis.class);

        OUTPUT_DIR = (OUTPUT_DIR.endsWith("/")) ? OUTPUT_DIR : OUTPUT_DIR + File.separator;
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
