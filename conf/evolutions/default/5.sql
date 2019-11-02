# --- !Ups
ALTER TABLE "Groups" ADD COLUMN "count" INT NULL;
# --- !Downs
ALTER TABLE "Groups" DROP COLUMN "count";
