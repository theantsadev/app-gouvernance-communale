-- Gouvernance communale
-- Fichier de module : execute par gouvernance_communale.sql avec psql

CREATE EXTENSION IF NOT EXISTS postgis;

-- =====================================================================
-- MODULE : SOCLE COMMUN
-- =====================================================================
-- ----------------------------- SCHEMA ---------------------------------
-- ---------------------------------------------------------------------
-- 1. Référentiels de base
-- ---------------------------------------------------------------------

CREATE TABLE SERVICE (
   id_service   VARCHAR(20)  PRIMARY KEY,
   nom          VARCHAR(100) NOT NULL,
   libelle_mg   VARCHAR(100),
   actif        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE FKT (
   id_fkt       VARCHAR(20)  PRIMARY KEY,
   nom          VARCHAR(100) NOT NULL,
   geom         GEOMETRY(MultiPolygon, 4326),   -- limite du FKT, vide tant que non numérisée
   actif        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE FKT_POPULATION (
   id_population    VARCHAR(20)  PRIMARY KEY,
   annee            INTEGER      NOT NULL,
   nombre_habitants INTEGER      NOT NULL CHECK (nombre_habitants >= 0),
   source           VARCHAR(100),
   id_fkt           VARCHAR(20)  NOT NULL REFERENCES FKT(id_fkt),
   UNIQUE (id_fkt, annee)
);

CREATE TABLE ROLE (
   id_role      VARCHAR(20)  PRIMARY KEY,
   nom          VARCHAR(100) NOT NULL,
   description  VARCHAR(255)
);

CREATE TABLE PERMISSION (
   id_permission VARCHAR(80)  PRIMARY KEY,      -- action:ressource
   action        VARCHAR(30)  NOT NULL,
   ressource     VARCHAR(50)  NOT NULL,
   description   VARCHAR(255),
   CHECK (id_permission = action || ':' || ressource)
);

CREATE TABLE ROLE_PERMISSION (
   id_role       VARCHAR(20) NOT NULL REFERENCES ROLE(id_role),
   id_permission VARCHAR(80) NOT NULL REFERENCES PERMISSION(id_permission),
   PRIMARY KEY (id_role, id_permission)
);

CREATE TABLE UTILISATEUR (
   id_utilisateur    VARCHAR(20)  PRIMARY KEY,
   email             VARCHAR(150) NOT NULL,
   mot_de_passe_hash VARCHAR(255) NOT NULL,
   nom               VARCHAR(100) NOT NULL,
   prenom            VARCHAR(100) NOT NULL,
   telephone         VARCHAR(30),
   date_creation     TIMESTAMP    NOT NULL DEFAULT now(),
   date_modification TIMESTAMP    NOT NULL DEFAULT now(),
   actif             BOOLEAN      NOT NULL DEFAULT TRUE,
   id_service        VARCHAR(20)  REFERENCES SERVICE(id_service),  -- périmètre ; vide pour maire, SG, admin
   id_role           VARCHAR(20)  NOT NULL REFERENCES ROLE(id_role)
);
CREATE UNIQUE INDEX uq_utilisateur_email ON UTILISATEUR (lower(email));

CREATE TABLE SESSION_AUTHENTIFICATION (
   id_session       VARCHAR(20) PRIMARY KEY,
   empreinte_token  VARCHAR(128) NOT NULL UNIQUE,
   id_utilisateur   VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur),
   date_expiration  TIMESTAMP   NOT NULL,
   date_creation    TIMESTAMP   NOT NULL DEFAULT now(),
   date_revoquee    TIMESTAMP
);
CREATE INDEX idx_session_token ON SESSION_AUTHENTIFICATION (empreinte_token);

CREATE TABLE AGENT (
   id_agent       VARCHAR(20)  PRIMARY KEY,
   matricule      VARCHAR(30)  NOT NULL UNIQUE,
   nom            VARCHAR(100) NOT NULL,
   prenom         VARCHAR(100) NOT NULL,
   fonction       VARCHAR(100) NOT NULL,
   telephone      VARCHAR(30),
   disponible     BOOLEAN      NOT NULL DEFAULT TRUE,
   actif          BOOLEAN      NOT NULL DEFAULT TRUE,
   id_fkt         VARCHAR(20)  REFERENCES FKT(id_fkt),
   id_service     VARCHAR(20)  NOT NULL REFERENCES SERVICE(id_service),
   id_utilisateur VARCHAR(20)  UNIQUE REFERENCES UTILISATEUR(id_utilisateur)   -- compte éventuel
);

CREATE TABLE STATUT (
   id_statut   VARCHAR(20)  PRIMARY KEY,        -- type_objet + '_' + code, ex. SIGNALEMENT_10
   type_objet  VARCHAR(30)  NOT NULL,
   code        INTEGER      NOT NULL,           -- valeur numérique pour les comparaisons
   libelle     VARCHAR(100) NOT NULL,
   libelle_mg  VARCHAR(100),
   modifiable  BOOLEAN      NOT NULL,           -- faux = l'objet est verrouillé
   actif       BOOLEAN      NOT NULL DEFAULT TRUE,
   UNIQUE (type_objet, code)
);

CREATE TABLE PERSONNE (
   id_personne VARCHAR(20)  PRIMARY KEY,
   nom         VARCHAR(100) NOT NULL,
   prenom      VARCHAR(100),
   telephone   VARCHAR(30),
   email       VARCHAR(150),
   adresse     VARCHAR(255),
   lot         VARCHAR(150),
   type        VARCHAR(50)  NOT NULL CHECK (type IN ('PERSONNE', 'ORGANISME', 'AUTORITE')),
   id_fkt      VARCHAR(20)  REFERENCES FKT(id_fkt)
);

-- Compteur des identifiants. id_sequence = préfixe (DEV) ou préfixe-année (SIG-2026).
CREATE TABLE SEQUENCE (
   id_sequence     VARCHAR(20) PRIMARY KEY,
   annee           INTEGER,
   derniere_valeur INTEGER     NOT NULL DEFAULT 0 CHECK (derniere_valeur >= 0),
   format          TEXT        NOT NULL
);

-- ---------------------------------------------------------------------
-- 2. Traçabilité, visas et décisions
-- ---------------------------------------------------------------------

CREATE TABLE HISTORIQUE_CONNEXION (
   id_connexion   VARCHAR(20)  PRIMARY KEY,
   email_saisi    VARCHAR(150) NOT NULL,
   type           VARCHAR(15)  NOT NULL CHECK (type IN ('CONNEXION', 'DECONNEXION', 'ECHEC')),
   date_connexion TIMESTAMP    NOT NULL DEFAULT now(),
   adresse_ip     VARCHAR(45),
   id_utilisateur VARCHAR(20)  REFERENCES UTILISATEUR(id_utilisateur)   -- vide si l'email est inconnu
);

CREATE TABLE HISTORIQUE_ACTION (
   id_action       VARCHAR(20) PRIMARY KEY,
   date_action     TIMESTAMP   NOT NULL DEFAULT now(),
   type_objet      VARCHAR(30) NOT NULL,
   id_objet        VARCHAR(20) NOT NULL,
   action          VARCHAR(40) NOT NULL,
   motif           TEXT,
   details         TEXT,
   id_utilisateur  VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur),
   id_statut_apres VARCHAR(20) REFERENCES STATUT(id_statut),
   id_statut_avant VARCHAR(20) REFERENCES STATUT(id_statut)
);

CREATE TABLE VISA (
   id_visa             VARCHAR(20)  PRIMARY KEY,
   type_objet          VARCHAR(30)  NOT NULL,
   id_objet            VARCHAR(20)  NOT NULL,
   type_visa           VARCHAR(50)  NOT NULL,
   signataire_nom      TEXT         NOT NULL,
   signataire_fonction VARCHAR(100),
   date_visa           TIMESTAMP    NOT NULL DEFAULT now(),
   mode                VARCHAR(20)  NOT NULL CHECK (mode IN ('PAPIER', 'ELECTRONIQUE')),
   id_enregistre_par   VARCHAR(20)  NOT NULL REFERENCES UTILISATEUR(id_utilisateur)
);

CREATE TABLE DECISION (
   id_decision       VARCHAR(20)  PRIMARY KEY,
   type_objet        VARCHAR(30)  NOT NULL,
   id_objet          VARCHAR(20)  NOT NULL,
   type_decision     VARCHAR(50)  NOT NULL,
   resultat          VARCHAR(50)  NOT NULL CHECK (resultat IN ('FAVORABLE', 'DEFAVORABLE')),
   motif             TEXT,
   decideur_nom      VARCHAR(100) NOT NULL,
   date_decision     TIMESTAMP    NOT NULL DEFAULT now(),
   mode              VARCHAR(20)  NOT NULL CHECK (mode IN ('PAPIER', 'ELECTRONIQUE')),
   id_enregistre_par VARCHAR(20)  NOT NULL REFERENCES UTILISATEUR(id_utilisateur),
   -- « Un refus exige un motif »
   CHECK (resultat <> 'DEFAVORABLE' OR (motif IS NOT NULL AND length(trim(motif)) > 0))
);

-- ---------------------------------------------------------------------
-- 3. Référentiels métier
-- ---------------------------------------------------------------------

CREATE TABLE TYPE_SIGNALEMENT (
   id_type_signalement VARCHAR(20)  PRIMARY KEY,
   libelle             VARCHAR(100) NOT NULL,
   libelle_mg          VARCHAR(100),
   actif               BOOLEAN      NOT NULL DEFAULT TRUE,
   id_service          VARCHAR(20)  NOT NULL REFERENCES SERVICE(id_service)
);

CREATE TABLE CANAL (
   id_canal    VARCHAR(20)  PRIMARY KEY,
   libelle     VARCHAR(100) NOT NULL,
   libelle_mg  VARCHAR(100),
   actif       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE URGENCE (
   id_urgence  VARCHAR(20)  PRIMARY KEY,
   niveau      INTEGER      NOT NULL UNIQUE,
   libelle     VARCHAR(100) NOT NULL,
   libelle_mg  VARCHAR(100),
   actif       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE RISQUE (
   id_risque   VARCHAR(20)  PRIMARY KEY,
   libelle     VARCHAR(100) NOT NULL,
   libelle_mg  VARCHAR(100),
   actif       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE TYPE_PERMIS (
   id_type_permis VARCHAR(20)  PRIMARY KEY,
   libelle        VARCHAR(100) NOT NULL,
   actif          BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE PIECE_REQUISE (
   id_piece_requise VARCHAR(20)  PRIMARY KEY,
   obligatoire      BOOLEAN      NOT NULL DEFAULT TRUE,
   libelle          VARCHAR(100) NOT NULL,
   ordre            INTEGER      NOT NULL,
   actif            BOOLEAN      NOT NULL DEFAULT TRUE,
   id_type_permis   VARCHAR(20)  NOT NULL REFERENCES TYPE_PERMIS(id_type_permis),
   UNIQUE (id_type_permis, ordre)
);

-- ---------------------------------------------------------------------
-- 4. Pièces jointes génériques
-- ---------------------------------------------------------------------

CREATE TABLE PIECE_JOINTE (
   id_piece_jointe  VARCHAR(20)  PRIMARY KEY,
   type_objet       VARCHAR(30)  NOT NULL,
   id_objet         VARCHAR(20)  NOT NULL,
   categorie        VARCHAR(40)  NOT NULL,       -- scan de lettre, visa, photo de descente, permis signé...
   nom_stocke       TEXT         NOT NULL,       -- nom UUID sur le disque
   nom_original     TEXT         NOT NULL,
   chemin           TEXT         NOT NULL,
   type_mime        VARCHAR(100),
   taille           INTEGER      CHECK (taille >= 0),
   actif            BOOLEAN      NOT NULL DEFAULT TRUE,   -- faux si la pièce a été remplacée
   date_ajout       TIMESTAMP    NOT NULL DEFAULT now(),
   id_auteur        VARCHAR(20)  NOT NULL REFERENCES UTILISATEUR(id_utilisateur),
   id_piece_requise VARCHAR(20)  REFERENCES PIECE_REQUISE(id_piece_requise)
);

-- ----------------------------- DONNEES --------------------------------
-- Les données initiales du socle sont regroupées dans la section
-- « Données initiales » après la déclaration de tous les modules, afin
-- de respecter les dépendances entre clés étrangères.

-- =====================================================================
