package com.orfangenes.util;

import org.junit.Test;

/**
 * @author Suresh Hewapathirana
 */
public class IdMapperTest {

  @Test
  public void ncbiToUniprot() {
      try {
          IdMapper.run("uploadlists", new ParameterNameValue[] {
                    new ParameterNameValue("from", "P_REFSEQ_AC"),
                    new ParameterNameValue("to", "ACC"),
                    new ParameterNameValue("format", "tab"),
                    new ParameterNameValue("query", "NP_511114.2 NP_004295.2 NP_031465.2 XP_004934106.1"),
            });
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

}