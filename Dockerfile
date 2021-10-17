FROM gradle:7.2-jdk11 as build-stage

WORKDIR /build
COPY . .

RUN /build/gradlew clean build

FROM adoptopenjdk:14.0.1_7-jre-openj9-0.20.0-bionic

ARG VERSION=0.0.1-SNAPSHOT

COPY --from=build-stage /build/build/libs/spotify-stats-$VERSION.jar /application.jar

CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /application.jar"]