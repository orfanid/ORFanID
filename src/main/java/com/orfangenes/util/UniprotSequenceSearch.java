package com.orfangenes.util;

import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.Set;

/** @author Suresh Hewapathirana */
@Slf4j
public class UniprotSequenceSearch {

  public static QueryResult<UniProtEntry> SearchSequence(Set<String> accessions) {
    QueryResult<UniProtEntry> results = null;
    try {
      ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();

      // UniProtService
      UniProtService uniprotService = serviceFactoryInstance.getUniProtQueryService();
      uniprotService.start();

      Query query = UniProtQueryBuilder.accessions(accessions);

      results = uniprotService.getEntries(query);
      uniprotService.stop();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    return results;
  }
}
