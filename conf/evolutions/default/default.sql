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

# --- !Downs
DROP TABLE "Subject";
DROP TABLE "Teachers";
DROP TABLE "GROUPS";