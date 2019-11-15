# --- !Ups
ALTER TABLE "Timetables" ALTER COLUMN "teachers" TYPE JSONB NULL;
ALTER TABLE "Timetables" ALTER COLUMN "numberRoom" TYPE JSONB NULL;
# --- !Downs
ALTER TABLE "Timetables" ALTER COLUMN "numberRoom" TYPE VARCHAR;
ALTER TABLE "Timetables" ALTER COLUMN "numberRoom" TYPE VARCHAR;
