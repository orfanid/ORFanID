package com.orfangenes.app;

import com.orfangenes.app.service.*;
import com.orfangenes.app.model.BlastResult;
import com.orfangenes.app.util.ResultsPrinter;
import com.orfangenes.app.model.Analysis;
import com.orfangenes.app.model.Gene;
import com.orfangenes.app.model.User;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.orfangenes.app.util.Constants.*;

@Slf4j
@Service
public class ORFanGenes {

    @Autowired
    DatabaseService databaseService;

    public int run(String query, String outputDir, Analysis analysis, String APP_DIR) {

        JSONArray trees;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Assert.assertTrue("Failure to open the sequence file!", new File(query).exists());

        final String rankedLineageFilepath = APP_DIR + FILE_RANK_LINEAGE;
        System.out.println("Ranked Lineage File Path : " + rankedLineageFilepath);

        // Generating BLAST file
        SequenceService sequenceService = null;
        List<BlastResult> blastResults = null;
        try {
            sequenceService = new SequenceService(analysis.getSequenceType(), query, outputDir);
            sequenceService.findHomology(outputDir, analysis.getMaximumTargetSequences(), analysis.getEvalue());
            HomologyProcessingService processor = new HomologyProcessingService(outputDir);
            blastResults = processor.getBlastResults();
            blastResults = blastResults.stream()
                    .filter(blastResult -> blastResult.getPident() >= Double.parseDouble(String.valueOf(analysis.getIdentity())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Blast file generation issue: " + e.getMessage());
            e.printStackTrace();
        }
            Set<Integer> blastHitsTaxIDs = null;
            // Getting unique taxonomy IDs from BLAST result
            List<Integer> staxids = blastResults.stream()
                    .map(BlastResult::getStaxid)
                    .collect(Collectors.toList());
            blastHitsTaxIDs = new HashSet<>(staxids);
            blastHitsTaxIDs.add(analysis.getTaxonomyId());

        try {
            // classification
            TaxTreeService taxTreeService = new TaxTreeService(rankedLineageFilepath, blastHitsTaxIDs, analysis.getTaxonomyId());
            ClassificationService classificationService = new ClassificationService(taxTreeService, analysis.getTaxonomyId(), blastResults);
            List<Gene> classifiedGenes = classificationService.getGeneClassification(sequenceService.getGenes(analysis.getTaxonomyId()));
            Map<String, List<List<String>>> taxonomyTreeForGenes = classificationService.getTaxonomyTreeForGenes();
             trees = ResultsProcessingService.generateBlastTree(taxonomyTreeForGenes);
            ResultsPrinter.displayFinding(classifiedGenes);

            // save results to the database with default user(orfanid). If user saves the dataset with their information, then the
            // ownership will be changed at that time

            User user = databaseService.getUserByEmail(EMAIL);
            if(user == null){
                user = new User();
                user.setId(-1l);
                user.setFirstName(FIRST_NAME);
                user.setLastName(LAST_NAME);
                user.setEmail(EMAIL);
            }

            analysis.setAnalysisDate(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
            analysis.setSaved(false);
            analysis.setBlastResults(String.valueOf(trees));
            analysis.setUser(user);

            classifiedGenes.forEach(gene -> {
                gene.setAnalysis(analysis);
            });

            analysis.setGeneList(classifiedGenes);

            databaseService.saveAnalysis(analysis);

        } catch (Exception e) {
            log.error("Results classification issue: " + e.getMessage());
            e.printStackTrace();
        }

        return 1; //todo: change
    }
}