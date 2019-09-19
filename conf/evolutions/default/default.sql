# --- !Ups
CREATE TABLE "Subject" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "numberClassRoom" INTEGER NOT NULL
);

CREATE TABLE "Teachers" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "fullName" VARCHAR NOT NULL,
  "tSubject" VARCHAR NOT NULL,
  "department" VARCHAR NOT NULL
);

CREATE TABLE "Groups" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "direction" VARCHAR NOT NULL
);

CREATE TABLE "Timetable" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "studyShift" VARCHAR NOT NULL,
  "weekDay" VARCHAR NOT NULL,
  "couple" VARCHAR NOT NULL,
  "typeOfLesson" VARCHAR NOT NULL,
  "groupNumber" VARCHAR NOT NULL,
  "subject" VARCHAR NOT NULL,
  "teacher" VARCHAR NOT NULL,
  "numberRoom" VARCHAR NOT NULL
);

# --- !Downs
DROP TABLE "Subject";
DROP TABLE "Teachers";
DROP TABLE "GROUPS";