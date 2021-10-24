FROM adoptopenjdk:11.0.3_7-jdk-openj9-0.14.3-bionic

ARG VERSION=0.0.1-SNAPSHOT

COPY /build/libs/spotify-stats-$VERSION.jar /application.jar

CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /application.jar"]
