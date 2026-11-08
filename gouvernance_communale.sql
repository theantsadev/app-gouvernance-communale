\set ON_ERROR_STOP on

-- =====================================================================
-- Gouvernance communale : orchestrateur du schema PostgreSQL + PostGIS
-- A lancer depuis psql après connexion à la base gouvernance_communale.
-- Exemple : psql -d gouvernance_communale -f gouvernance_communale.sql
-- =====================================================================

\ir sql/01-socle/schema.sql
\ir sql/02-signalement/schema.sql
\ir sql/03-plainte/schema.sql
\ir sql/04-urbanisme/schema.sql
\ir sql/05-commun/schema.sql

\ir sql/01-socle/data.sql
\ir sql/05-commun/data.sql
\ir sql/04-urbanisme/data.sql
