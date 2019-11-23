# --- !Ups
ALTER TABLE "Timetables" ADD COLUMN "specPartJson"  JSONB NULL;
# --- !Downs
ALTER TABLE "Timetables" DROP COLUMN "specPartJson";
