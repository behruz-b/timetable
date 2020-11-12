#!/bin/bash

#docker pull postgres:11

docker build -t timetable ./deploy/

#docker run --name t-postgres -p 5432:5432 -e 'POSTGRES_PASSWORD=123' -e 'POSTGRES_USER=tuser' -d postgres

sleep 2s

#docker exec -it t-postgres psql -U tuser -c "create database timetable"

cat ./conf/db/db.sql | docker exec -i t-postgres psql -U tuser -d timetable

source deploy.sh
