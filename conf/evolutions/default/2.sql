# --- !Ups
ALTER TABLE "Timetables" ADD COLUMN "specPart" VARCHAR NULL;
# --- !Downs
ALTER TABLE "Timetables" DROP "specPart";
