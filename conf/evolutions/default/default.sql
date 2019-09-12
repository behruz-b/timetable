# --- !Ups
CREATE TABLE "Subject" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "numberClassRoom" INTEGER NOT NULL
);

# --- !Downs
DROP TABLE "Subject";