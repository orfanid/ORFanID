# ORFanGenes Instructions

## 1. How to Run an Example Sequence

1. Visit the ORFanGenes [website](http://orfangenes.com) and click on "Input" from the Navigation bar, or "Get Started" 
at the bottom of the page.
![GetStarted](https://github.com/Savidude/ORFanGenes/blob/master/src/main/resources/static/assets/images/documentation/GetStarted.png)
1. Click on the "Example" link on the bottom left. The text fields will automatically get filled with an example input 
sequence of genes of E. Coli.
![Example](https://github.com/Savidude/ORFanGenes/blob/master/src/main/resources/static/assets/images/documentation/example.png)
1. The E-Value defaults to e-3 and the Maximum Target Sequences for BLAST defaults to 1000. If these need to be changed, 
"Advanced Parameters" can be selected and move the slider to select the appropriate values.
1. Select "Submit" and wait for around 10 minutes or more until ORFanGenes processes the input sequence and returns the results.
![Classify](https://github.com/Savidude/ORFanGenes/blob/master/src/main/resources/static/assets/images/documentation/classify.png)
1. View the results of the example sequence
![Results](https://github.com/Savidude/ORFanGenes/blob/master/src/main/resources/static/assets/images/documentation/classify.png)

The example input contains three genes. Out of the three genes, two of which are **Genus Restricted Genes**, and the other
is an **ORFan Gene**. The Blast Results table shows the taxonomies (and their parents) for each gene in the input sequence 
from the BLAST search.

## 2. Using ORFanGenes

1. Visit the ORFanGenes [website](http://orfangenes.com) and click on "Input" from the Navigation bar, or "Get Started" 
at the bottom of the page.
![GetStarted](https://github.com/Savidude/ORFanGenes/blob/master/src/main/resources/static/assets/images/documentation/GetStarted.png)
1. Select the type of sequence (Protein or Nucleotide) that will be processed.
1. Click “Browse” and upload a sequence file. The file must be in .fasta format, and must contain the geneid value of each 
gene
1. Begin typing the input organism’s name and select the name from the suggested results, and select the organism from 
the dropdown menu.
![Input](https://github.com/Savidude/ORFanGenes/blob/master/src/main/resources/static/assets/images/documentation/input.png)
1. Select “Advanced parameters” and adjust the maximum E-value and maximum target sequences for BLAST.
![Advanced](https://github.com/Savidude/ORFanGenes/blob/master/src/main/resources/static/assets/images/documentation/advanced.png)
1. Click “Submit” and wait for results to load. The time taken for the results to be obtained is dependent on the number 
of input sequences, selected e-value, and number of target sequences.

## 3. Deploy the ORFanID

1. Make sure you have installed the [Docker](https://docs.docker.com/get-docker/) in your VM.
2. Download this repository to your working directory of the machine
3. Check the docker-compose.yml and  replace the volume mounts to map the location to your local storage. Run the ```docker-compose up --build``` command to build and run the container.
4. If you want to terminate the instance and remoce the container, you can use ```docker-compose down```