# --- !Ups
ALTER TABLE "Timetables" ADD COLUMN "teachers2"  JSONB NULL;
ALTER TABLE "Timetables" ADD COLUMN "numberRoom2"  JSONB NULL;
ALTER TABLE "Timetables" ADD COLUMN "teachers" VARCHAR NULL;
# --- !Downs
ALTER TABLE "Timetables" DROP COLUMN "teachers2";
ALTER TABLE "Timetables" DROP COLUMN "numberRoom2";
ALTER TABLE "Timetables" DROP COLUMN "teachers";
