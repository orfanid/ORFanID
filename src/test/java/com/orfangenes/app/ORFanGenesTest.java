package com.orfangenes.app;

import com.orfangenes.app.config.DatabaseApiConfig;
import com.orfangenes.app.service.DatabaseService;
import com.orfangenes.app.util.Constants;
import com.orfangenes.app.util.FileHandler;
import com.orfangenes.app.util.RestCall;
import com.orfangenes.app.model.Analysis;
import com.orfangenes.app.model.Gene;
import com.orfangenes.app.model.User;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.orfangenes.app.util.Constants.EMAIL;
import static com.orfangenes.app.util.Constants.FIRST_NAME;
import static com.orfangenes.app.util.Constants.LAST_NAME;
import static org.junit.Assert.*;

/**
 * @author Suresh Hewapathirana
 */
//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DatabaseApiConfig.class, DatabaseService.class})
public class ORFanGenesTest {

  @Autowired
  DatabaseService databaseService;

  int organismTaxID;
  String query;
  String outputdir;
  String blastType;
  String max_target_seqs;
  String evalue;

  private String OUTPUT_DIR="/Users/hewapathirana/projects/ORFanFinder/out";

  @Before
  public void setUp() throws Exception {

    this.organismTaxID = 511145;
    this.query = "/Users/hewapathirana/projects/ORFan/orfanid/src/main/resources/static/assets/data/Ecoli_511145.fasta";
    this.outputdir = "/Users/hewapathirana/projects/ORFanFinder/out/1548828660644_PB3";
    this.blastType = " protein";
    this.max_target_seqs = "550";
    this.evalue = "3" ;
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void save() throws JSONException, ParseException, IOException {

    String analysisId = "1594542076213_eee";
    OUTPUT_DIR = (OUTPUT_DIR.endsWith("/"))? OUTPUT_DIR : OUTPUT_DIR + File.separator;
    String metadataFilePath = OUTPUT_DIR + "1594542076213_jYX"  + File.separator + Constants.FILE_OUTPUT_ORFAN_GENES;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    User user = databaseService.getUserByEmail(EMAIL);

    String blastResultsFilePath = OUTPUT_DIR + "1594542076213_jYX" + File.separator + Constants.FILE_OUTPUT_BLAST_RESULTS;
    JSONArray blastResults = FileHandler.getArrayFromFile(blastResultsFilePath);


    Analysis analysis = new Analysis();
    analysis.setAnalysisId(analysisId + new Date().toString());
    analysis.setOrganism("Drosophila melanogaster");
    analysis.setTaxonomyId(7227);
    analysis.setAnalysisDate(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
    analysis.setEvalue(3);
    analysis.setSequenceType("protein");
    analysis.setIdentity(60);
    analysis.setMaximumTargetSequences(548);
    analysis.setSaved(false);
    analysis.setBlastResults(blastResults.toString());

    analysis.setUser(user);

    JSONArray genes =  FileHandler.getArrayFromFile(metadataFilePath);
    List<Gene> genesList = new ArrayList<>();
        for(int i=0;i<genes.size();i++)
        {
          JSONObject jsonEntry = (JSONObject) genes.get(i);
          Gene newGene = new Gene();
          newGene.setGeneId(jsonEntry.get("geneid").toString());
          newGene.setDescription(jsonEntry.get("description").toString());
          newGene.setOrfanLevel(jsonEntry.get("orfanLevel").toString());
          newGene.setTaxonomyId(analysis.getTaxonomyId());
          newGene.setSequence("");
          newGene.setAnalysis(analysis);
          genesList.add(newGene);
        }
        analysis.setGeneList(genesList);

    databaseService.saveAnalysis(analysis);
    System.out.println("Data saved!");
  }

  @Test
  public void update() throws Exception {

    String analysisId = "1594542076213_abc";

    Analysis analysis = databaseService.getAnalysisById(analysisId);
    if(analysis !=null){
      User user = databaseService.getUserByEmail("anudayananda123@gmail.com");
      if(user == null){
        user = new User();
        user.setId(-1l);
        user.setFirstName("anu123");
        user.setLastName("dayananda123");
        user.setEmail("anudayananda123@gmail.com");
      }
      analysis.setUser(user);
      analysis.setSaved(true);
      databaseService.saveAnalysis(analysis);
    }else{
      throw new Exception("Analysis not found for Analysis ID : " + analysisId);
    }
    System.out.println("Data updated!");
  }

  @Test
  public void getDataSummary() {
    String analysisId = "1600425128826_aeh";
    String response = databaseService.getDataSummary(analysisId);
  }

  @Test
  public void getDataSummaryChart() {
    String analysisId = "1600425128826_aeh";
    String response = databaseService.getDataSummaryChart(analysisId);
  }

  @Test
  public void getDataGeneList() {
    String analysisId = "1600425128826_aeh";
    String response = databaseService.getDataGeneList(analysisId);
  }

  @Test
  public void getDataBlastResults() {
    String analysisId = "1600425128826_aeh";
    String response = databaseService.getDataBlastResults(analysisId);
  }

  @Test
  public void getOrfanbaseGenes() {
    String response = databaseService.getOrfanbaseGenes();
  }
}