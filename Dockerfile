FROM openjdk:8-jdk-alpine
# download BLAST
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} orfanid.jar
# set volumns
# set port
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/orfanid.jar"]