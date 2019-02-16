############################################################
# Dockerfile to build ORFanID web and its API and the Database
# 4th February 2019
# Version 1.0.0
# By: Suresh Hewapathirana
# TODO:
# - Add any items here
############################################################

# Base docker image with Ubuntu and Java
FROM java:8

################## BEGIN INSTALLATION ######################

RUN apt-get update
RUN apt-get install -y build-essential wget

#Install NCBI BLAST programmes
RUN wget -q ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/LATEST/ncbi-blast-2.8.1+-x64-linux.tar.gz && \
    tar xf ncbi-blast-2.8.1+-x64-linux.tar.gz && \
    cp ncbi-blast-2.8.1+/bin/* /usr/local/bin

##################### INSTALLATION END #####################

VOLUME /tmp
ADD /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
