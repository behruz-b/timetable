# --- !Ups
ALTER TABLE "Timetables" ALTER COLUMN "numberRoom" TYPE VARCHAR;
# --- !Downs
ALTER TABLE "Timetables" ALTER COLUMN "numberRoom" TYPE INT;
