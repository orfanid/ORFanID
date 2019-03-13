package com.orfangenes.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Suresh Hewapathirana
 */
public class UniprotSequenceSearchTest {

  @Test
  public void searchSequence() {
    Set<String> accessions = new HashSet<>(Arrays.asList("P12345"));
    UniprotSequenceSearch.SearchSequence(accessions);

  }
}