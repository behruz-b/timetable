# --- !Ups
ALTER TABLE "Timetables" ADD COLUMN "flow" BOOLEAN NOT NULL DEFAULT FALSE;
# --- !Downs
ALTER TABLE "Timetables" DROP COLUMN "flow";
