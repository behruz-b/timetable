#!/bin/bash

sbt dist

docker run --rm -ti -v $PWD/target/universal/timetable-0.1.zip:/opt/timetable-0.1.zip -p 8080:8080 --link=t-postgres:postgres-host timetable:latest
