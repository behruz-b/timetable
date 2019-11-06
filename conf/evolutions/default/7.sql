# --- !Ups
ALTER TABLE "Timetables" DROP COLUMN "alternation";
# --- !Downs
ALTER TABLE "Timetables" ADD COLUMN "alternation" VARCHAR NULL;
