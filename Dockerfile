FROM gradle:8.3-jdk17
WORKDIR /opt/app
COPY ./build/libs/TuneQuest-1.0-SNAPSHOT.jar ./
COPY .env /opt/app/
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar TuneQuest-1.0-SNAPSHOT.jar"]