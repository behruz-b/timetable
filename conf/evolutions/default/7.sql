# --- !Ups
ALTER TABLE "Timetables" ADD COLUMN "alternation" varchar not null;
# --- !Downs
ALTER TABLE "Timetables" DROP COLUMN "alternation";
