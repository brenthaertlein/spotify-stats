# Spotify Stats

[![codecov](https://codecov.io/gh/brenthaertlein/spotify-stats/branch/main/graph/badge.svg?token=4O5N0S3725)](https://codecov.io/gh/brenthaertlein/spotify-stats)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=brenthaertlein_spotify-stats&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=brenthaertlein_spotify-stats)

An API to aggregate data from Spotify Web API

## Table of Contents

* [Resources](#Resources)

## Getting started

* Install docker: https://www.docker.com/get-started
* Run from the command line: `docker run -d -p 27017:27017 mongo`
* Register an application with
  Spotify: https://developer.spotify.com/documentation/web-api/quick-start/
* Set Environment Variables
    * `SPOTIFY_CLIENT_ID` and `SPOTIFY_CLIENT_SECRET` (provided by signing up using the resource
      above. You can use a free spotify account)
* Install dependencies via gradle
* Run application

## Resources

* https://docs.gradle.org/current/userguide/kotlin_dsl.html
* https://www.baeldung.com/spring-boot-get-all-endpoints
* https://developer.spotify.com/documentation/web-api/
