-- Gouvernance communale
-- Fichier de module : execute par gouvernance_communale.sql avec psql

-- MODULE : PLAINTE
-- =====================================================================
-- ----------------------------- SCHEMA ---------------------------------
-- ---------------------------------------------------------------------
-- 6. Plaintes entre citoyens
-- ---------------------------------------------------------------------

CREATE TABLE PLAINTE (
   id_plainte          VARCHAR(20) PRIMARY KEY,
   date_ouverture      DATE        NOT NULL,
   objet               TEXT        NOT NULL,
   date_resolution     DATE,
   date_modification   TIMESTAMP   NOT NULL DEFAULT now(),
   id_descente_constat VARCHAR(20) REFERENCES DESCENTE(id_descente),   -- descente qui constate le résultat
   id_statut           VARCHAR(20) NOT NULL REFERENCES STATUT(id_statut),
   id_signalement      VARCHAR(20) NOT NULL REFERENCES SIGNALEMENT(id_signalement),
   CHECK (date_resolution IS NULL OR date_resolution >= date_ouverture)
);

CREATE TABLE PLAINTE_PARTIE (
   id_plainte  VARCHAR(20) NOT NULL REFERENCES PLAINTE(id_plainte),
   id_personne VARCHAR(20) NOT NULL REFERENCES PERSONNE(id_personne),
   role        VARCHAR(50) NOT NULL CHECK (role IN ('PLAIGNANT', 'MIS_EN_CAUSE')),
   PRIMARY KEY (id_plainte, id_personne)
);

CREATE TABLE CONVOCATION (
   id_convocation VARCHAR(20) PRIMARY KEY,        -- CNV-AAAA-NNNN
   date_emission  TIMESTAMP   NOT NULL DEFAULT now(),
   date_rdv       DATE        NOT NULL,
   heure_rdv      TIME        NOT NULL,
   lieu           VARCHAR(100) NOT NULL,
   motif          TEXT        NOT NULL,
   id_plainte     VARCHAR(20) NOT NULL REFERENCES PLAINTE(id_plainte)
);

-- Le coupon de retour, par personne convoquée.
CREATE TABLE CONVOCATION_DESTINATAIRE (
   id_convocation_destinataire VARCHAR(20) PRIMARY KEY,
   date_remise       DATE,
   signataire_coupon VARCHAR(100),
   id_personne       VARCHAR(20) NOT NULL REFERENCES PERSONNE(id_personne),
   id_convocation    VARCHAR(20) NOT NULL REFERENCES CONVOCATION(id_convocation),
   UNIQUE (id_convocation, id_personne)
);

CREATE TABLE RAPPORT_REUNION (
   id_rapport_reunion VARCHAR(20) PRIMARY KEY,
   date_reunion       DATE        NOT NULL,
   observations       TEXT,
   expose             TEXT,
   decision           TEXT        NOT NULL,      -- « Décision obligatoire »
   delai_jours        INTEGER     NOT NULL CHECK (delai_jours >= 0),   -- « Délai obligatoire »
   date_echeance      DATE        NOT NULL,
   id_convocation     VARCHAR(20) NOT NULL UNIQUE REFERENCES CONVOCATION(id_convocation),
   -- L'échéance = date de la réunion + délai accordé
   CHECK (date_echeance = date_reunion + delai_jours)
);

CREATE TABLE REUNION_PARTICIPANT (
   id_reunion_participant VARCHAR(20) PRIMARY KEY,
   qualite                VARCHAR(50) CHECK (qualite IS NULL OR qualite IN
                              ('PLAIGNANT', 'MIS_EN_CAUSE', 'TEMOIN', 'AUTORITE', 'AGENT')),
   present                BOOLEAN     NOT NULL,
   nom_libre              TEXT,
   id_personne            VARCHAR(20) REFERENCES PERSONNE(id_personne),
   id_rapport_reunion     VARCHAR(20) NOT NULL REFERENCES RAPPORT_REUNION(id_rapport_reunion),
   CHECK (id_personne IS NOT NULL OR nom_libre IS NOT NULL)
);

CREATE TABLE PROCES_VERBAL (
   id_proces_verbal VARCHAR(20) PRIMARY KEY,
   date_emission    TIMESTAMP   NOT NULL DEFAULT now(),
   motif            TEXT,
   id_plainte       VARCHAR(20) NOT NULL REFERENCES PLAINTE(id_plainte)
);

-- MODULE : PLAINTE / DONNEES
-- ----------------------------- DONNEES --------------------------------
-- Aucune donnée métier initiale : les plaintes sont créées à partir d'un
-- signalement traité par le service EHA.

-- =====================================================================
