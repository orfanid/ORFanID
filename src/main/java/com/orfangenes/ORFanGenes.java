package com.orfangenes;

import com.orfangenes.service.*;
//import com.orfangenes.service.ResultsGenerator;
import com.orfangenes.model.BlastResult;
import com.orfangenes.model.Gene;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.orfangenes.util.Constants.*;

@Slf4j
public class ORFanGenes {

    public static void main(String[] args) {
        log.info("Starting application....");
        log.info("Program arguments: " + Arrays.toString(args));
        try {
            CommandLine cmd = ORFanGenes.parseArgs(args);
            run(cmd.getOptionValue(ARG_QUERY),
                    cmd.getOptionValue(ARG_OUT),
                    Integer.parseInt(cmd.getOptionValue(ARG_TAX)),
                    cmd.getOptionValue(ARG_TYPE),
                    cmd.getOptionValue(ARG_MAX_TARGET_SEQS),
                    cmd.getOptionValue(ARG_EVALUE));
            log.info("Analysis Completed!");
        } catch (ParseException e) {
            log.error("Error : ", e);
        }
    }

    public static CommandLine parseArgs(String[] args) throws ParseException {
        log.info("Parameters: " + args.toString());
        Options options = new Options();
        options.addOption(ARG_QUERY, true, "Input Sequence file in FASTA format");
        options.addOption(ARG_TYPE, true, "Input Sequence type(protein/dna)");
        options.addOption(ARG_TAX, true, "NCBI taxonomy ID of the species");
        options.addOption(ARG_MAX_TARGET_SEQS, true, "Number of target sequences");
        options.addOption(ARG_EVALUE, true, "BLAST E-Value Threshold");
        options.addOption(ARG_OUT, true, "Output directory");
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public static void run(String query, String outputdir, int organismTaxID, String blastType, String max_target_seqs, String evalue) {

        final String rankedLineageFilepath = getFilePath("rankedlineage.dmp");

        // Generating BLAST file
        Sequence sequence = new Sequence(blastType, query, outputdir, organismTaxID);
        sequence.generateBlastFile(outputdir, max_target_seqs, evalue);
        BlastResultsProcessor processor = new BlastResultsProcessor(outputdir);
        List<BlastResult> blastResults = processor.getBlastResults();
        // Getting unique taxonomy IDs from BLAST result
        List<Integer> staxids = blastResults.stream().map(BlastResult::getStaxid).collect(Collectors.toList());
        Set<Integer> blastHitsTaxIDs = new HashSet<>(staxids);
        blastHitsTaxIDs.add(organismTaxID);

        TaxTree taxTree = new TaxTree(rankedLineageFilepath, blastHitsTaxIDs, organismTaxID);
        Classifier classifier = new Classifier(taxTree, organismTaxID, blastResults);
        Map<String, String> geneClassification = classifier.getGeneClassification(outputdir, sequence.getGenes());
        for (Map.Entry<String, String> stringStringEntry : geneClassification.entrySet()) {
            log.info(stringStringEntry.getKey() + "--->" + stringStringEntry.getValue());
        }
        System.out.println("--------------------------------------------------------- \n");
//        ResultsGenerator.generateResult(geneClassification, outputdir, processor, taxTree, sequence.getGenes());
    }

    private static String getFilePath(String filename) {
        String filepath = null;
        try {
            URL url = ORFanGenes.class.getClassLoader().getResource(filename);
            if (url == null) {
                throw new IllegalStateException(filename + " file not found!");
            }
            filepath = url.toURI().getPath();
        } catch (URISyntaxException e) {
            log.error("File not found");
        }
        return filepath;
    }
}