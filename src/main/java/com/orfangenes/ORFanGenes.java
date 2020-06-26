package com.orfangenes;

import com.orfangenes.service.*;
import com.orfangenes.model.BlastResult;
import com.orfangenes.util.FileHandler;
import com.orfangenes.util.ResultsPrinter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
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
                    cmd.getOptionValue(ARG_EVALUE),
                    cmd.getOptionValue(ARG_IDENTITY));
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
        options.addOption(ARG_IDENTITY, true, "Protein identification percentage");
        options.addOption(ARG_OUT, true, "Output directory");
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public static void run(String query, String outputDir, int organismTaxID,
                           String blastType, String max_target_seqs, String eValue, String identity) {

        final String rankedLineageFilepath = FileHandler.getFilePath(FILE_RANK_LINEAGE);
        Assert.assertTrue("Failure to open the sequence file!", new File(query).exists());

        // Generating BLAST file
        SequenceService sequenceService = new SequenceService(blastType, query, outputDir);
        sequenceService.findHomology(outputDir, max_target_seqs, eValue);
        HomologyProcessingService processor = new HomologyProcessingService(outputDir);
        List<BlastResult> blastResults = processor.getBlastResults();
        blastResults = blastResults.stream()
                .filter(blastResult -> blastResult.getPident() >= Double.parseDouble(identity))
                .collect(Collectors.toList());

        // Getting unique taxonomy IDs from BLAST result
        List<Integer> staxids = blastResults.stream()
                .map(BlastResult::getStaxid)
                .collect(Collectors.toList());
        Set<Integer> blastHitsTaxIDs = new HashSet<>(staxids);
        blastHitsTaxIDs.add(organismTaxID);

        // classification
        TaxTreeService taxTreeService = new TaxTreeService(rankedLineageFilepath, blastHitsTaxIDs, organismTaxID);
        ClassificationService classificationService = new ClassificationService(taxTreeService, organismTaxID, blastResults);
        Map<String, String> geneClassification = classificationService.getGeneClassification(outputDir, sequenceService.getGenes(organismTaxID));
        ResultsPrinter.displayFinding(geneClassification);

    }
}