# --- !Ups
CREATE TABLE "Suggestion" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "teacherName" VARCHAR NOT NULL,
  "suggestion" VARCHAR NOT NULL
);

# --- !Downs
DROP TABLE "Suggestion";
