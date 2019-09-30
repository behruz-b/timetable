# --- !Ups
CREATE TABLE "Subjects" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "numberClassRoom" INTEGER NOT NULL
);

CREATE TABLE "Teachers" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "fullName" VARCHAR NOT NULL,
  "tSubject" VARCHAR[] NOT NULL,
  "department" VARCHAR NOT NULL
);

CREATE TABLE "Groups" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "direction" VARCHAR NOT NULL
);

CREATE TABLE "Timetables" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "studyShift" VARCHAR NOT NULL,
  "weekDay" VARCHAR NOT NULL,
  "couple" VARCHAR NOT NULL,
  "typeOfLesson" VARCHAR NOT NULL,
  "groups" VARCHAR NOT NULL,
  "divorce" VARCHAR NULL,
  "subjectId" INTEGER CONSTRAINT "timetablesFkSubjectId" REFERENCES "Subjects" ON UPDATE CASCADE ON DELETE CASCADE,
  "teachers" VARCHAR NOT NULL,
  "numberRoom" INT NOT NULL
);

# --- !Downs
DROP TABLE "Timetables";
DROP TABLE "Groups";
DROP TABLE "Teachers";
DROP TABLE "Subjects";
