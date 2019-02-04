############################################################
# Dockerfile to build ORFanID web and its API and the Database
# 4Th February 2019
# Version 1.0.0
# By: Suresh Hewapathirana
# TODO:
# - Add any items here
############################################################

# Base docker image with Ubuntu and Java
FROM mlaccetti/docker-oracle-java8-ubuntu-16.04

ENV DEBIAN_FRONTEND noninteractive

################## BEGIN INSTALLATION ######################

RUN apt-get update
RUN apt-get install -y build-essential wget

#Install NCBI BLAST programmes
RUN wget -q ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/LATEST/ncbi-blast-2.8.1+-x64-linux.tar.gz && \
    tar xf ncbi-blast-2.8.1+-x64-linux.tar.gz && \
    cp ncbi-blast-2.8.1+/bin/* /usr/local/bin

##################### INSTALLATION END #####################

VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
#ARG DEPENDENCY=target/dependency
#COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY ${DEPENDENCY}/META-INF /app/META-INF
#COPY ${DEPENDENCY}/BOOT-INF/classes /app
#ENTRYPOINT ["java","-cp","app:app/lib/*","com.orfangenes.OrfanidApplication"]