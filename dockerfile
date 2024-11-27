FROM eclipse-temurin:21-jre
EXPOSE 8888 7999
ENV APP_FILE=target/service-1.0-SNAPSHOT-jar-with-dependencies.jar
ENV APP_HOME=/home/app
RUN mkdir -p ${APP_HOME}
COPY ${APP_FILE} ${APP_HOME}
WORKDIR ${APP_HOME}
CMD [ "java", "-jar", "service-1.0-SNAPSHOT-jar-with-dependencies.jar"]