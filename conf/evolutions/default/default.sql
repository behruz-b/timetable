# --- !Ups
firstName, lastName, tSubject, department
CREATE TABLE "Subject" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "numberClassRoom" INTEGER NOT NULL
);

CREATE TABLE "Teachers" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "FullName" VARCHAR NOT NULL,
  "tSubject" VARCHAR NOT NULL,
  "department" VARCHAR NOT NULL
);

# --- !Downs
DROP TABLE "Subject";
DROP TABLE "Teachers";