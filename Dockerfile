############################################################
# Dockerfile to build ORFanID web and its API and the Database
# 4th February 2019
# Version 1.0.0
# By: Suresh Hewapathirana
############################################################

###################### BUILD STAGE #######################

# use openjdk:8-jdk image to build the project. This image already has java installed.
# we can used the alpine version, but it gave me an error in bash
FROM maven:3.3.9-jdk-8-alpine AS build-env

# update the linux OS
#RUN apt-get update

# use app folder as the working directory
WORKDIR /app

# copy application code and the configuration
COPY src ./src
COPY pom.xml ./
COPY script ./script
COPY config/application.yml ./src/main/resources
# package the code into jar. Do not run any test while packaging
RUN mvn clean package -DskipTests

###################### PACKAGE STAGE ######################

# use openjdk:8-jdk image to run the pakagedjar. This image already has java installed.
FROM openjdk:8-jdk

# update the OS,
# install wget progrmme to download tools from the command-line
# install bash to run the NCBI balstp/blastn programmes in command-line
# create a directory called "dataoutputdir" to save analysis files
RUN apt-get update && apt-get install wget bash && apt-get install -y r-base && mkdir /dataoutputdir

# use app folder as the working directory
WORKDIR /app

#Install NCBI BLAST programmes and make it executable. Secondly, download ranklineagefile from NCBI FTP
RUN wget -q ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/2.8.1/ncbi-blast-2.8.1+-x64-linux.tar.gz && \
    tar xf ncbi-blast-2.8.1+-x64-linux.tar.gz && \
    mkdir -p /usr/local/ncbi/blast/bin && \
    chmod +x ncbi-blast-2.8.1+/bin/blastp && \
    cp ncbi-blast-2.8.1+/bin/* /usr/local/ncbi/blast/bin && \
    wget -q ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/new_taxdump/new_taxdump.tar.gz && \
    tar xf new_taxdump.tar.gz

# Copy the jar file from the "build-env" to the current container
COPY --from=build-env /app/target/*.jar app.jar

# Run the app.jar in the Java environment. Specify the memory requirement as well.
ENTRYPOINT ["java","-Xmx2048m", "-jar","app.jar"]