/* Replace the location to where you saved the data files*/
-- Replace the location to where you saved the data files
\copy Users FROM '/class/classes/bhuyn053/cs166_project_phase3/data/users.csv' WITH DELIMITER ',' CSV HEADER;
\copy Catalog FROM '/class/classes/bhuyn053/cs166_project_phase3/data/catalog.csv' WITH DELIMITER ',' CSV HEADER;
\copy RentalOrder FROM '/class/classes/bhuyn053/cs166_project_phase3/data/rentalorder.csv' WITH DELIMITER ',' CSV HEADER;
\copy TrackingInfo FROM '/class/classes/bhuyn053/cs166_project_phase3/data/trackinginfo.csv' WITH DELIMITER ',' CSV HEADER;
\copy GamesInOrder FROM '/class/classes/bhuyn053/cs166_project_phase3/data/gamesinorder.csv' WITH DELIMITER ',' CSV HEADER;

