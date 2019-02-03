package com.orfangenes.model;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class BlastResult implements Serializable {
  private String qseqid;
  private String sseqid;
  private double pident;
  private int length;
  private int mismatch;
  private int gapopen;
  private int qstart;
  private int qend;
  private int sstart;
  private int send;
  private double evalue;
  private double bbibtscore;
  private int staxid;
  private String queryid;

  public BlastResult(String result) {
    String[] resultData = result.split("\t");
    this.qseqid = resultData[0];
    this.sseqid = resultData[1];
    this.pident = Double.parseDouble(resultData[2]);
    this.length = Integer.parseInt(resultData[3]);
    this.mismatch = Integer.parseInt(resultData[4]);
    this.gapopen = Integer.parseInt(resultData[5]);
    this.qstart = Integer.parseInt(resultData[6]);
    this.qend = Integer.parseInt(resultData[7]);
    this.sstart = Integer.parseInt(resultData[8]);
    this.send = Integer.parseInt(resultData[9]);
    this.evalue = Double.parseDouble(resultData[10]);
    this.bbibtscore = Double.parseDouble(resultData[11]);
    setStaxid(resultData[12]);
    setQueryid(resultData[0]);
  }

  public void setStaxid(String taxID) {
    try {
      if (taxID.contains(";")) {
        // If many tax IDs are obtained from the last result, get the first one
        String[] taxIDs = taxID.split(";");
        taxID = taxIDs[0];
      }
      this.staxid = Integer.parseInt(taxID.trim());
    } catch (NumberFormatException e) {
      this.staxid = -1;
    }
  }

  public void setQueryid(String qseqid) {
    this.queryid = qseqid.trim();
  }
}
