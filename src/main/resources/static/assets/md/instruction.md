# ORFanID Operating Instructions

ORFanID Sequence Submission
-------------
Visit the ORFanGenes website (http://orfangenes.com/) and click Get Started on the landing page.

![ ](assets/md/images/startScreen.png)

An ORFan analysis can be done by providing either  a DNA or protein sequence and the scientific name (or ID) of the organism. 

![ ](assets/md/images/inpoutScreen.png)

The input DNA or protein sequences can be added in three ways: Search by Accession, FASTA File Upload, or Direct Input. Details on the Input sequences are below:

![ ](assets/md/images/table.png)


Next, the Organism Name of the input gene must be specified. The Organism can be specified using the auto-completion selection textbox (see the screenshot below). Start typing and auto-completion will help you select the organism quickly by showing the Scientific name, NCBI Taxonomy ID and an image. If the organism of interest is not in the ORFanID database, please go to NCBI Taxonomy Database and obtain the full scientific name and the taxonomy ID which has to be added within parentheses. Example Organism names are provided in the screenshot below.

![ ](assets/md/images/selectOrganism.png)


Example sequences are provided to demonstrate the use of this ORFan identifying software engine. When a graphical icon at the bottom left of the screen is selected, the appropriate data will be automatically loaded into the forms. The “sequence” or the “accession” option can be selected.

![ ](assets/md/images/selectExample.png)

The search can be fine tuned using optional parameters by clicking on the "Advance Parameter" link. The following parameters can be adjusted according to the user preference:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1. Maximum eValue for BLAST(e-10) <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2. Maximum number of target sequences for BLAST results <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3. Identity<br />
An example is provided below. In each case, the slider should be moved to set the appropriate value.

![ ](assets/md/images/customizeSelection.png)

Next, select "Submit" and wait until ORFanGenes processes the input sequence and returns the results. Depending on the workload on the NCBI servers the ORFanID process could take anywhere from 3 to 15 minutes.

![ ](assets/md/images/processing.png)


Interpreting Results
-------------
Results will be displayed as in the screenshot below:

![ ](assets/md/images/output.png)

The example shown above contains three genes. Two of the sequences are Genus Restricted Genes, and the third is an ORFan Gene. The BLAST Results table shows the taxonomies (and their parents) for each gene in the input sequence from the BLAST search. <br />

A graphical representation of the taxonomy tree can be viewed by selecting the green Homology Evidence button for each sequence. 

![ ](assets/md/images/optionOutput.png)

![ ](assets/md/images/treeOutput.png)

Results can be submitted to the “ORFanBase” database by providing the user contact information as follows:

![ ](assets/md/images/orfanbaseSave.png)

The Results page displays previous ORFanID analyses stored in the database.

![ ](assets/md/images/ofranbaseDatabase.png) <br />


The ORFanBase Database
-------------
Genes that have been categorized from the analysis and submitted to the “ORFanbase” database can be found on the “ORFanbase” page:


![ ](assets/md/images/orfanBaseDatabaseGenes.png)

The orphan gene information obtained can now be used for your research purposes. <br />

 If you have any questions regarding the instructions for using the ORFanID to search and find ORFan Genes of interest to you, or regarding the analysis of your data input, please email us at:  info@orfangenes.com






































