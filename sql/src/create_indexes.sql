DROP INDEX IF EXISTS CATALOG catalog_gameName;
DROP INDEX IF EXISTS CATALOG catalog_genre;

CREATE INDEX catalog_gameName;
ON Catalog USING BTREE (gameName);

CREATE INDEX catalog_genre;
ON Catalog USING BTREE (genre);