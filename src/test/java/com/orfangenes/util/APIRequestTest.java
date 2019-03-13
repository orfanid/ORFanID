package com.orfangenes.util;

import org.junit.Test;

/**
 * @author Suresh Hewapathirana
 */
public class APIRequestTest {

  @Test
  public void runTest() {
    try {
      APIRequest.run("P31600");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}