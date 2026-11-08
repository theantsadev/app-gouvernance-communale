-- Gouvernance communale
-- Fichier de module : execute par gouvernance_communale.sql avec psql

-- MODULE : URBANISME
-- =====================================================================
-- ----------------------------- SCHEMA ---------------------------------
-- ---------------------------------------------------------------------
-- 7. Permis et contrôles de conformité
-- ---------------------------------------------------------------------

CREATE TABLE DOSSIER_PERMIS (
   id_dossier_permis VARCHAR(20) PRIMARY KEY,     -- DPC-AAAA-NNNN (numéro de dossier)
   lot               VARCHAR(50) NOT NULL,
   quartier          VARCHAR(50),
   point             GEOMETRY(Point, 4326),
   description       TEXT,
   date_depot        DATE        NOT NULL,
   date_creation     TIMESTAMP   NOT NULL DEFAULT now(),
   date_modification TIMESTAMP   NOT NULL DEFAULT now(),
   id_statut         VARCHAR(20) NOT NULL REFERENCES STATUT(id_statut),
   id_fkt            VARCHAR(20) NOT NULL REFERENCES FKT(id_fkt),
   id_service        VARCHAR(20) NOT NULL REFERENCES SERVICE(id_service),
   id_demandeur      VARCHAR(20) NOT NULL REFERENCES PERSONNE(id_personne),
   id_type_permis    VARCHAR(20) NOT NULL REFERENCES TYPE_PERMIS(id_type_permis)
);

CREATE TABLE CONTROLE_CONFORMITE (
   id_controle_conformite VARCHAR(20) PRIMARY KEY,
   quartier               VARCHAR(50),
   lot                    VARCHAR(50),
   resultat               VARCHAR(50) NOT NULL CHECK (resultat IN ('CONFORME', 'NON_CONFORME')),
   observations           TEXT,
   id_fkt                 VARCHAR(20) NOT NULL REFERENCES FKT(id_fkt),
   id_descente            VARCHAR(20) NOT NULL UNIQUE REFERENCES DESCENTE(id_descente),
   id_dossier_permis      VARCHAR(20) REFERENCES DOSSIER_PERMIS(id_dossier_permis)   -- une construction sans permis peut être contrôlée
);

CREATE TABLE CERTIFICAT_NON_CONFORMITE (
   id_certificat          VARCHAR(20) PRIMARY KEY,   -- CNC-AAAA-NNNN
   date_emission          DATE        NOT NULL,
   motif                  TEXT        NOT NULL,      -- « Motif obligatoire »
   id_statut              VARCHAR(20) NOT NULL REFERENCES STATUT(id_statut),
   id_controle_conformite VARCHAR(20) NOT NULL REFERENCES CONTROLE_CONFORMITE(id_controle_conformite)
);

CREATE TABLE SANCTION (
   id_sanction        VARCHAR(20)   PRIMARY KEY,
   echeance           DATE,
   date_paiement      DATE,
   reference_paiement VARCHAR(100),
   montant            NUMERIC(14,2) CHECK (montant >= 0),
   id_statut          VARCHAR(20)   NOT NULL REFERENCES STATUT(id_statut),
   id_certificat      VARCHAR(20)   NOT NULL REFERENCES CERTIFICAT_NON_CONFORMITE(id_certificat)   -- « Certificat de non-conformité requis »
);

-- ----------------------------- DONNEES --------------------------------
-- Les types de permis et les pièces requises sont initialisés dans la
-- section « Données initiales » après la déclaration de tous les modules.

-- =====================================================================
