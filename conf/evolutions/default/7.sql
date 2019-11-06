# --- !Ups
ALTER TABLE "Timetables" ADD COLUMN "alternation" VARCHAR NULL;
# --- !Downs
ALTER TABLE "Timetables" DROP COLUMN "alternation";
