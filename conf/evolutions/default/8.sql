# --- !Ups
ALTER TABLE "Timetables" ADD COLUMN "teachers"  JSONB NULL;
ALTER TABLE "Timetables" ADD COLUMN "numberRoom"  JSONB NULL;
# --- !Downs
ALTER TABLE "Timetables" DROP COLUMN "numberRoom";
ALTER TABLE "Timetables" DROP COLUMN "numberRoom";
