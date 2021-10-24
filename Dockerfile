FROM adoptopenjdk:11.0.11_9-jdk-openj9-0.26.0-focal

ARG VERSION=0.0.1-SNAPSHOT

COPY /build/libs/spotify-stats-$VERSION.jar /application.jar

CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /application.jar"]
