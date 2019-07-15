FROM gradle:jdk11 as gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar

FROM openjdk:11-slim
WORKDIR /app
COPY --from=gradle /home/gradle/src/build/libs/metrics-exporter.jar /app/metrics-exporter.jar
# See https://docs.openshift.com/container-platform/3.3/creating_images/guidelines.html
RUN chgrp -R 0 /app && \
    chmod -R g=u /app
EXPOSE 8080
CMD ["java", "-jar", "metrics-exporter.jar"]
