package com.orfangenes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Suresh Hewapathirana
 */
public class ORFanGenesTest {

  int organismTaxID;
  String query;
  String outputdir;
  String blastType;
  String max_target_seqs;
  String evalue;

  @Before
  public void setUp() throws Exception {

    this.organismTaxID = 511145;
    this.query = "/Users/hewapathirana/projects/ORFanFinder/out/1548828660644_PB3/input.fasta";
    this.outputdir = "/Users/hewapathirana/projects/ORFanFinder/out/1548828660644_PB3";
    this.blastType = " protein";
    this.max_target_seqs = "550";
    this.evalue = "3" ;
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void run() {
    ORFanGenes.run(query, outputdir, organismTaxID, blastType, max_target_seqs, evalue);
  }
}