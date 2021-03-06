package com.orfangenes.app.util;

/**
 * This class contains all the Constants
 */
public class Constants {

    // default user
    public static final String FIRST_NAME = "orfanid";
    public static final String LAST_NAME = "orfanid";
    public static final String EMAIL = "orfanid@gmail.com";

    // Blast Types  TODO: SHould be an Enum
    public static final String TYPE_NUCLEOTIDE = "nucleotide";
    public static final String TYPE_PROTEIN = "protein";

    // Ranks TODO: SHould be an Enum
    public static final String ORFAN_GENE = "ORFan Gene";
    public static final String SPECIES = "species";
    public static final String GENUS = "genus";
    public static final String FAMILY = "family";
    public static final String ORDER = "order";
    public static final String CLASS = "class";
    public static final String PHYLUM = "phylum";
    public static final String KINGDOM = "kingdom";
    public static final String SUPERKINGDOM = "superkingdom";

    // TRGs TODO: SHould be an Enum
    public static final String STRICT_ORFAN = "Strict ORFan";
    public static final String MULTI_DOMAIN_GENE = "multi-domain gene";
    public static final String DOMAIN_RESTRICTED_GENE = "domain restricted gene";
    public static final String KINGDOM_RESTRICTED_GENE = "kingdom restricted gene";
    public static final String PHYLUM_RESTRICTED_GENE = "phylum restricted gene";
    public static final String CLASS_RESTRICTED_GENE = "class restricted gene";
    public static final String ORDER_RESTRICTED_GENE = "order restricted gene";
    public static final String FAMILY_RESTRICTED_GENE = "family restricted gene";
    public static final String GENUS_RESTRICTED_GENE = "genus restricted gene";

    // ARG TODO: SHould be an Enum
    public static final String ARG_QUERY = "query";
    public static final String ARG_TYPE = "type";
    public static final String ARG_TAX = "tax";
    public static final String ARG_MAX_TARGET_SEQS = "maxTargetSeqs";
    public static final String ARG_EVALUE = "evalue";
    public static final String ARG_IDENTITY = "identity";
    public static final String ARG_OUT = "out";
    public static final String ARG_RANK_LINEAGE_FILE_DIR = "rankLineageFileDir";

    // input files
    public static final String INPUT_FASTA = "input.fasta";
    public static final String FILE_RANK_LINEAGE = "rankedlineage.dmp";

    // output files
    public static final String BLAST_RESULTS_FILE = "blastResults.bl";
    public static final String FILE_OUTPUT_ORFAN_GENES = "ORFanGenes.json";
    public static final String FILE_OUTPUT_BLAST_RESULTS = "blastresults.json";
    public static final String FILE_OUTPUT_ORFAN_GENES_SUMMARY = "ORFanGenesSummary.json";
    public static final String FILE_OUTPUT_ORFAN_GENES_SUMMARY_CHART = "ORFanGenesSummaryChart.json";
    public static final String FILE_RESULT_METADATA = "metadata.json";
    public static final String FILE_RESULTS = "results.json";
    public static final String FILE_OUTPUT_CLAMP = "clamp_summary.json";

    // file names
    public static final String SEQUENCE = "sequence";
    public static final String BLAST_RESULTS = "blastResults";

    // file extensions
    public static final String FASTA_EXT = ".fasta";
    public static final String BLAST_EXT = ".bl";

    // Misc

    public static final String NOT_AVAILABLE = "N/A";
    public static final String LINE_SEPARATOR = "\n";
    public static final String SEQUENCE_SEPARATOR = "\n\n";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

}