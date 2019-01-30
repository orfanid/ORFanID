package com.orfangenes.service;

import com.orfangenes.model.BlastResult;
import com.orfangenes.model.Gene;
import com.orfangenes.model.taxonomy.TaxTree;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.orfangenes.util.Constants.*;

@Slf4j
@AllArgsConstructor
public class Classifier {

    private Sequence sequence;
    private TaxTree tree;
    private int organismTaxID;

    public Map<Gene, String> getGeneClassification(List<BlastResult> blastResults) {
        Map<Gene, String> classification = new HashMap<>();

        Map<String, Integer> inputTaxHierarchy = tree.getHeirarchyFromNode(organismTaxID);

        for (Gene gene : sequence.getGenes()) { // For each input ID
            Set<Integer> taxIDs = getBlastTaxonomies(blastResults, gene.getGeneID());

            // Getting the hierarchy for each taxonomy
            List<Map<String, Integer>> hierarchies = new ArrayList<>();
            for (int taxID: taxIDs) {
                Map<String, Integer> taxHierarchy = tree.getHeirarchyFromNode(taxID);
                if (taxHierarchy != null && taxHierarchy.size() > 0) {
                    try{
                        int speciesTaxID = taxHierarchy.get(SPECIES);
                        if (speciesTaxID != organismTaxID) {
                            hierarchies.add(taxHierarchy);
                        }
                    } catch (NullPointerException e) {
                        log.warn("Taxonomy cannot find" + e.getMessage());
                    }
                }
            }
            String level = getLevel(hierarchies, inputTaxHierarchy, SUPERKINGDOM, 0);
            if (level == null) {
                level = STRICT_ORFAN;
            }
            classification.put(gene, level);
//            addToDatabase(gene, level);
        }
        return classification;
    }

//    private void addToDatabase (Gene gene, String level) {
//        Connection connection = ORFanDB.connectToDatabase(Database.DB_ORFAN);
//        String table = null;
//        switch (level){
//            case Constants.ORFAN_GENE:  table = Database.TB_ORFAN_GENES; break;
//            case Constants.STRICT_ORFAN: table = Database.TB_STRICT_ORFANS; break;
//            case Constants.MULTI_DOMAIN_GENE: table = Database.TB_MD_GENES; break;
//            case Constants.DOMAIN_RESTRICTED_GENE: table = Database.TB_DOMAIN_RG; break;
//            case Constants.KINGDOM_RESTRICTED_GENE: table = Database.TB_KINGDOM_RG; break;
//            case Constants.PHYLUM_RESTRICTED_GENE: table = Database.TB_PHYLUM_RG; break;
//            case Constants.CLASS_RESTRICTED_GENE: table = Database.TB_CLASS_RG; break;
//            case Constants.ORDER_RESTRICTED_GENE: table = Database.TB_ORDER_RG; break;
//            case Constants.FAMILY_RESTRICTED_GENE: table = Database.TB_FAMILY_RG; break;
//            case Constants.GENUS_RESTRICTED_GENE: table = Database.TB_GENUS_RG; break;
//        }
//        if (!ORFanDB.recordExists(connection, table, gene.getSequence())) {
//            String insertQuery = "INSERT INTO " + table + " (geneId, sequence, description, taxId) " +
//                    "VALUES (?,?,?,?)";
//            Object[] insertData = new Object[4];
//            insertData[0] = gene.getGeneID();
//            insertData[1] = gene.getSequence();
//            insertData[2] = gene.getDescription();
//            insertData[3] = gene.getTaxID();
//            ORFanDB.insertRecordPreparedStatement(connection, insertQuery, insertData);
//        }
//    }

    // Recursive method
    private String getLevel(List<Map<String, Integer>> hierarchies, Map<String, Integer> inputTaxHierarchy,
                            String currentRank, int levelsSkipped) {
        Map<String, String> rankInfo;
        if (currentRank != null) {
            rankInfo = getRankInfo(currentRank);
        } else {
            return null;
        }

        // Getting tax id of the current rank in the input sequence
        int rankTaxID = 0;
        try {
            rankTaxID = inputTaxHierarchy.get(currentRank);
        } catch (NullPointerException e) {
            getLevel(hierarchies, inputTaxHierarchy, rankInfo.get(NEXT_RANK), ++levelsSkipped);
        }
        Set<Integer> taxonomiesAtCurrentRank = new HashSet<>();

        for (Map<String, Integer> currentTaxHierarchy: hierarchies) {
            try {
                int currentRankId = currentTaxHierarchy.get(currentRank);
                taxonomiesAtCurrentRank.add(currentRankId);
            } catch (NullPointerException e) {
                // Do nothing
            }
        }

        if (taxonomiesAtCurrentRank.size() == 1 && taxonomiesAtCurrentRank.contains(rankTaxID)) {
            if (currentRank.equals(SPECIES)) {
                return ORFAN_GENE;
            }
            return getLevel(hierarchies, inputTaxHierarchy, rankInfo.get(NEXT_RANK), 0);
        } else if (taxonomiesAtCurrentRank.size() > 1 || (taxonomiesAtCurrentRank.size() == 1 && !taxonomiesAtCurrentRank.contains(rankTaxID))){
            for (int i = 0; i < levelsSkipped; i--) {
                String prevRank = rankInfo.get(PREV_RANK);
                if (prevRank != null) {
                    rankInfo = getRankInfo(rankInfo.get(PREV_RANK));
                } else {
                    return MULTI_DOMAIN_GENE;
                }
            }
            return rankInfo.get(GENE_TYPE);
        } else {
            return getLevel(hierarchies, inputTaxHierarchy, rankInfo.get(NEXT_RANK), ++levelsSkipped);
        }
    }

    private Map<String, String> getRankInfo(String currentRank) {
        String geneType = null;
        String prevRank = null;
        String nextRank = null;
        switch (currentRank) {
            case SUPERKINGDOM: geneType = DOMAIN_RESTRICTED_GENE; prevRank = null; nextRank = KINGDOM; break;
            case KINGDOM: geneType = KINGDOM_RESTRICTED_GENE; prevRank = SUPERKINGDOM; nextRank = PHYLUM; break;
            case PHYLUM: geneType = PHYLUM_RESTRICTED_GENE;  prevRank = KINGDOM; nextRank = CLASS; break;
            case CLASS: geneType = CLASS_RESTRICTED_GENE; prevRank = PHYLUM; nextRank = ORDER; break;
            case ORDER: geneType = ORDER_RESTRICTED_GENE; prevRank = CLASS; nextRank = FAMILY; break;
            case FAMILY: geneType = FAMILY_RESTRICTED_GENE; prevRank = ORDER; nextRank = GENUS; break;
            case GENUS: geneType = GENUS_RESTRICTED_GENE; prevRank = FAMILY; nextRank = SPECIES; break;
            case SPECIES: geneType = ORFAN_GENE; prevRank = GENUS; nextRank = null; break;
        }

        Map<String, String> rankInfo = new HashMap<>();
        rankInfo.put(GENE_TYPE, geneType);
        rankInfo.put(PREV_RANK, prevRank);
        rankInfo.put(NEXT_RANK, nextRank);
        return rankInfo;
    }

    private Set<Integer> getBlastTaxonomies(List<BlastResult> blastResults, String gid) {
        Set<Integer> taxIDs = new LinkedHashSet<>();
        for (BlastResult result : blastResults) {
            if (result.getQueryid().equals(gid)) {
                taxIDs.add(result.getStaxid());
            }
        }
        return taxIDs;
    }
}
