-- Gouvernance communale
-- Fichier de module : execute par gouvernance_communale.sql avec psql

-- MODULE : SIGNALEMENT
-- =====================================================================
-- ----------------------------- SCHEMA ---------------------------------
-- ---------------------------------------------------------------------
-- 5. Signalements et interventions
-- ---------------------------------------------------------------------

CREATE TABLE SIGNALEMENT (
   id_signalement      VARCHAR(20) PRIMARY KEY,   -- SIG-AAAA-NNNN (numéro d'arrivée)
   objet               TEXT,
   description         TEXT        NOT NULL,
   quartier            VARCHAR(50),
   lot                 TEXT,
   point               GEOMETRY(Point, 4326),
   date_lettre         DATE,                      -- date écrite sur la lettre, à titre indicatif
   date_reception      DATE        NOT NULL,      -- date du tampon d'arrivée : la date officielle
   date_creation       TIMESTAMP   NOT NULL DEFAULT now(),
   date_modification   TIMESTAMP   NOT NULL DEFAULT now(),
   id_statut           VARCHAR(20) NOT NULL REFERENCES STATUT(id_statut),
   id_fkt              VARCHAR(20) NOT NULL REFERENCES FKT(id_fkt),
   id_service          VARCHAR(20) NOT NULL REFERENCES SERVICE(id_service),   -- service qui traite
   id_urgence          VARCHAR(20) REFERENCES URGENCE(id_urgence),            -- remplis à la qualification
   id_type_signalement VARCHAR(20) REFERENCES TYPE_SIGNALEMENT(id_type_signalement),
   id_canal            VARCHAR(20) NOT NULL REFERENCES CANAL(id_canal)
);

CREATE TABLE SIGNALEMENT_PERSONNE (
   id_signalement VARCHAR(20) NOT NULL REFERENCES SIGNALEMENT(id_signalement),
   id_personne    VARCHAR(20) NOT NULL REFERENCES PERSONNE(id_personne),
   role           VARCHAR(50) NOT NULL CHECK (role IN ('DEMANDEUR', 'MIS_EN_CAUSE', 'SIGNATAIRE')),
   PRIMARY KEY (id_signalement, id_personne, role)
);

CREATE TABLE SIGNALEMENT_RISQUE (
   id_signalement VARCHAR(20) NOT NULL REFERENCES SIGNALEMENT(id_signalement),
   id_risque      VARCHAR(20) NOT NULL REFERENCES RISQUE(id_risque),
   PRIMARY KEY (id_signalement, id_risque)
);

CREATE TABLE DEVIS (
   id_devis           VARCHAR(20)   PRIMARY KEY,
   version            INTEGER       NOT NULL CHECK (version > 0),
   montant_total      NUMERIC(14,2) CHECK (montant_total >= 0),
   date_etablissement TIMESTAMP     NOT NULL DEFAULT now(),
   id_statut          VARCHAR(20)   NOT NULL REFERENCES STATUT(id_statut),
   id_signalement     VARCHAR(20)   NOT NULL REFERENCES SIGNALEMENT(id_signalement),
   UNIQUE (id_signalement, version)
);

CREATE TABLE DEVIS_LIGNE (
   id_devis_ligne VARCHAR(20)   PRIMARY KEY,
   designation    VARCHAR(255)  NOT NULL,
   quantite       NUMERIC(12,2) NOT NULL CHECK (quantite > 0),
   unite          VARCHAR(50)   NOT NULL,
   prix_unitaire  NUMERIC(14,2) CHECK (prix_unitaire >= 0),
   montant        NUMERIC(14,2) CHECK (montant >= 0),
   id_devis       VARCHAR(20)   NOT NULL REFERENCES DEVIS(id_devis)
);

-- Le travail à réaliser (signalement ou plainte) : porte le rapport et le coût réel.
CREATE TABLE INTERVENTION (
   id_intervention VARCHAR(20)   PRIMARY KEY,
   type_objet      VARCHAR(30)   NOT NULL,        -- SIGNALEMENT ou PLAINTE
   id_objet        VARCHAR(20)   NOT NULL,
   date_prevue     DATE          NOT NULL,
   rapport         TEXT,                          -- obligatoire à la clôture (règle du code)
   cout_reel       NUMERIC(14,2) CHECK (cout_reel >= 0),
   date_cloture    DATE,
   id_statut       VARCHAR(20)   NOT NULL REFERENCES STATUT(id_statut)
);

CREATE TABLE INTERVENTION_AGENT (
   id_intervention VARCHAR(20) NOT NULL REFERENCES INTERVENTION(id_intervention),
   id_agent        VARCHAR(20) NOT NULL REFERENCES AGENT(id_agent),
   PRIMARY KEY (id_intervention, id_agent)
);

-- La feuille nominative visée par le maire.
CREATE TABLE ORDRE_MISSION (
   id_ordre_mission VARCHAR(20)  PRIMARY KEY,     -- ORM-AAAA-NNNN
   destination      VARCHAR(150) NOT NULL,
   motif            TEXT         NOT NULL,
   moyen_transport  VARCHAR(50)  NOT NULL,
   date_emission    DATE         NOT NULL,
   id_statut        VARCHAR(20)  NOT NULL REFERENCES STATUT(id_statut),
   id_intervention  VARCHAR(20)  NOT NULL REFERENCES INTERVENTION(id_intervention),
   id_chef_service  VARCHAR(20)  NOT NULL REFERENCES UTILISATEUR(id_utilisateur)
);



-- Une étape du tableau d'un ordre de mission, ou un contrôle de terrain.
CREATE TABLE DESCENTE (
   id_descente      VARCHAR(20)  PRIMARY KEY,
   type             VARCHAR(50)  NOT NULL CHECK (type IN ('MISSION', 'CONTROLE')),
   localite         VARCHAR(150) NOT NULL,
   date_descente    DATE         NOT NULL,
   heure_arrivee    TIME,
   heure_depart     TIME,
   observations     TEXT,
   point            GEOMETRY(Point, 4326),
   id_ordre_mission VARCHAR(20)  REFERENCES ORDRE_MISSION(id_ordre_mission),
   -- « Aucune descente sans ordre de mission » (le visa du maire est vérifié par le code)
   CHECK (type <> 'MISSION' OR id_ordre_mission IS NOT NULL),
   CHECK (heure_arrivee IS NULL OR heure_depart IS NULL OR heure_depart > heure_arrivee)
);

CREATE TABLE DESCENTE_AGENT (
   id_descente VARCHAR(20) NOT NULL REFERENCES DESCENTE(id_descente),
   id_agent    VARCHAR(20) NOT NULL REFERENCES AGENT(id_agent),
   PRIMARY KEY (id_descente, id_agent)
);

-- MODULE : SIGNALEMENT / DONNEES
-- ----------------------------- DONNEES --------------------------------
-- Aucune donnée métier initiale : les signalements sont saisis par les
-- utilisateurs de l'application.

-- =====================================================================
