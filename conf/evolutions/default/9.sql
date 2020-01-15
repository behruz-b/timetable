# --- !Ups
CREATE TABLE "Room" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "number" VARCHAR NOT NULL,
  "place" INT NOT NULL
);

ALTER TABLE "Groups" ADD COLUMN "totalStudent" INT NULL;
# --- !Downs
DROP TABLE "Room";
ALTER TABLE "Groups" DROP COLUMN "totalStudent";
