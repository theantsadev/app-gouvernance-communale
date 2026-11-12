-- Evolution : ajoute la persistance des sessions JWT.
-- Le IF NOT EXISTS permet l'application sur une base issue du schema.sql
-- qui contient déjà cette table.

CREATE TABLE IF NOT EXISTS SESSION_AUTHENTIFICATION (
   id_session       VARCHAR(20) PRIMARY KEY,
   empreinte_token  VARCHAR(128) NOT NULL UNIQUE,
   id_utilisateur   VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur),
   date_expiration  TIMESTAMP   NOT NULL,
   date_creation    TIMESTAMP   NOT NULL DEFAULT now(),
   date_revoquee    TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_session_token
    ON SESSION_AUTHENTIFICATION (empreinte_token);
