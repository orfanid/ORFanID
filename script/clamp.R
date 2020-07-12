# -------------------------------------------------------------------------------------
# Created on  : 2016 Nov 28
# Author      : Suresh Hewapathirana
# Purpose     : Finding orphan genes in Human Genome(Ensembl Genes v86) - (GRCh38.p7) 
# -------------------------------------------------------------------------------------

# install Biomart Library if it is not already installed
#source("https://bioconductor.org/biocLite.R")
#biocLite("biomaRt")

#install.packages('biomaRt')

if(!require("BiocManager")){
    install.packages("BiocManager")
    BiocManager::install("biomaRt")
}
if(!require('argparser')){
    install.packages('argparser')
}
if(!require('jsonlite')){
    install.packages('jsonlite')
    install.packages('jsonlite')
}
# install.packages("BiocManager")
# BiocManager::install("biomaRt")
# install.packages('argparser')
# install.packages('jsonlite')

# import library
suppressMessages(library(biomaRt))
suppressMessages(library("argparser"))
suppressMessages(library("jsonlite"))     # Convert R object to JSON format

parser <- arg_parser("Input Parameters")
parser <- add_argument(parser, "--chromosome", default = "All", 
                       help = "Chomosome number")
parser <- add_argument(parser, "--output", default = "summary.json",
                       help = "output json file path")
argv   <- parse_args(parser)

# Initiate ensembl. To view available ensembl, use listEnsembl() command
ensembl.hs = useEnsembl(biomart="ensembl", dataset="hsapiens_gene_ensembl",host="www.ensembl.org")

# view available attributes and filters associated with the ensembl
 #listAttributes(mart = ensembl.hs,page="gene")
 #listFilters(mart = ensembl.hs)

# table to collect summary results
summary <- data.frame(Chromosome=character(),
                  HumanGenes=integer(),
                  Retrotranspon=integer(),
                  DogOrtholog=integer(),
                  MouseOrtholog=integer(),
                  HumanParalog=integer(),
                  Pfam=integer(),
                  AllCandidates=integer(),
                  CandidateWithSeq=integer(), 
                  stringsAsFactors=FALSE
                  ) 
json.list <- list()
colnames<-c("Chromosome","HumanGenes","Retrotranspon","MouseOrtholog","DogOrtholog","HumanParalog","Pfam","AllCandidates","CandidateWithSeq")
colnames(summary) <- colnames

# Chomosomes that are considered for the study
if(argv$chromosome == "All"){
  chromosome_names <- c("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","X")
}else{
  chromosome_names <- c(argv$chromosome)
}

for (chromosome_name in chromosome_names) {
  
# get all the genes(ensembl gene ids) of the chromosome
chrHuman <- getBM(attributes = c("ensembl_gene_id"), 
                  filters = "chromosome_name", 
                  values = chromosome_name, 
                  uniqueRows=TRUE, 
                  mart = ensembl.hs)
length(chrHuman$ensembl_gene_id)

# genes that are annotates as retrotransposed psuedogenes
chrRetrotransposed <-getBM(attributes = c("ensembl_gene_id"),
                            filters = c("chromosome_name","biotype"),
                            values = list(chromosome_name,"processed_pseudogene"),
                            uniqueRows=TRUE,
                            mart = ensembl.hs)

# get all the human orthologous genes with Dog in chromosome 
chrDog <-getBM(attributes = c("ensembl_gene_id"), 
                  filters = c("chromosome_name","with_clfamiliaris_homolog"), 
                  values = list(chromosome_name,TRUE), 
                  uniqueRows=TRUE, 
                  mart = ensembl.hs)

# get all the human orthologous genes with Mouse in chromosome
chrMouse <- getBM(attributes = c("ensembl_gene_id"), 
                  filters = c("chromosome_name","with_mmusculus_homolog"), 
                  values = list(chromosome_name,TRUE), 
                  uniqueRows=TRUE, 
                  mart = ensembl.hs)

# get all the human paralogous genes in chromosome
chrHumanParalog <- getBM(attributes = c("ensembl_gene_id"), 
                  filters = c("chromosome_name","with_hsapiens_paralog"), 
                  values = list(chromosome_name,TRUE), 
                  uniqueRows=TRUE, 
                  mart = ensembl.hs)

# get all the genes that contain a pfam id/ belongs to a known protein family
chrPfam <- getBM(attributes = c("ensembl_gene_id"), 
                         filters = c("chromosome_name","with_pfam"), 
                         values = list(chromosome_name,TRUE), 
                         uniqueRows=TRUE, 
                         mart = ensembl.hs)

# exclude all the retrotransposed genes from the human gene list
human_without_psudo <- chrHuman$ensembl_gene_id[which(!chrHuman$ensembl_gene_id %in% chrRetrotransposed$ensembl_gene_id)]
length(human_without_psudo)

# exclude all the ortholog genes with Dog from the complete list
without_dog <- human_without_psudo[which(!human_without_psudo %in% chrDog$ensembl_gene_id)]
length(without_dog)

# exclude all the ortholog genes with Mouse
without_mouse <- without_dog[which(!without_dog %in% chrMouse$ensembl_gene_id)]
length(without_mouse)

# exclude all the Paralog genes in Human
without_paralog <- without_mouse[which(!without_mouse %in% chrHumanParalog$ensembl_gene_id)]
length(without_paralog)

# exclude all the genes that are belongs to a known protein family
without_pfam <- without_paralog[which(!without_paralog %in% chrPfam$ensembl_gene_id)]
length(without_paralog)

# get peptide sequence of the gene
seq <- getSequence(id=c(without_pfam), type="ensembl_gene_id", seqType="peptide", mart = ensembl.hs)
peptide_seq <- seq[which(seq$peptide != "Sequence unavailable"),]
length(peptide_seq$ensembl_gene_id)

# hist(unlist(lapply(X = (peptide_seq$peptide), FUN = nchar))*3)

# write protein sequences into a FASTA file
exportFASTA(peptide_seq,file=paste("chromosome",chromosome_name,".fasta", sep = ""))

# get hgnc gene symbols for the ensembl gene IDs
# gene_list <- getBM(filters= "ensembl_gene_id", attributes= c("ensembl_gene_id","description","hgnc_symbol"),values=peptide_seq$ensembl_gene_id,mart= ensembl.hs)
# add chomosome name as another column
# gene_list$chr <- chromosome_name
  
# add statistical details to the summary table
record = c("Chromosome" = chromosome_name, 
           length(chrHuman$ensembl_gene_id),
           length(chrRetrotransposed$ensembl_gene_id),
           length(chrDog$ensembl_gene_id),
           length(chrMouse$ensembl_gene_id),
           length(chrHumanParalog$ensembl_gene_id),
           length(chrPfam$ensembl_gene_id),
           length(seq$ensembl_gene_id),
           length(peptide_seq$ensembl_gene_id)
           )
summary[nrow(summary)+1,] <- record
json.list <- append(json.list,list(record))

} # end of loop

# create a summary table
#write.table(summary, file = "summary.csv", append = FALSE, sep = ",",col.names = TRUE, row.names = FALSE)

write(toJSON(list(data = json.list)), argv$output)

