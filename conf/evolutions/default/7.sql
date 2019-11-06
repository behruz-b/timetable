# --- !Ups
ALTER TABLE "Timetables" ADD COLUMN "alternation" varchar;
# --- !Downs
ALTER TABLE "Timetables" DROP COLUMN "alternation";
