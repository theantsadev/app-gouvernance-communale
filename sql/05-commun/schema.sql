-- Gouvernance communale
-- Fichier de module : execute par gouvernance_communale.sql avec psql

-- MODULE : COMMUN
-- =====================================================================
-- ----------------------------- SCHEMA ---------------------------------
-- ---------------------------------------------------------------------
-- 8. Index transversaux
-- (PostgreSQL n'indexe pas automatiquement les clés étrangères)
-- ---------------------------------------------------------------------

CREATE INDEX idx_signalement_similaire ON SIGNALEMENT (id_fkt, lot, date_reception);   -- alerte « signalement similaire »
CREATE INDEX idx_signalement_service   ON SIGNALEMENT (id_service, id_statut);          -- périmètre du chef de service
CREATE INDEX idx_signalement_reception ON SIGNALEMENT (date_reception);
CREATE INDEX idx_signalement_point     ON SIGNALEMENT USING GIST (point);
CREATE INDEX idx_fkt_geom              ON FKT USING GIST (geom);
CREATE INDEX idx_dossier_point         ON DOSSIER_PERMIS USING GIST (point);
CREATE INDEX idx_descente_point        ON DESCENTE USING GIST (point);

CREATE INDEX idx_dossier_fkt           ON DOSSIER_PERMIS (id_fkt, id_statut);
CREATE INDEX idx_dossier_demandeur     ON DOSSIER_PERMIS (id_demandeur);
CREATE INDEX idx_plainte_signalement   ON PLAINTE (id_signalement);
CREATE INDEX idx_convocation_plainte   ON CONVOCATION (id_plainte);
CREATE INDEX idx_devis_signalement     ON DEVIS (id_signalement);
CREATE INDEX idx_intervention_agent    ON INTERVENTION_AGENT (id_agent);
CREATE INDEX idx_ordre_intervention    ON ORDRE_MISSION (id_intervention);
CREATE INDEX idx_descente_ordre        ON DESCENTE (id_ordre_mission);
CREATE INDEX idx_controle_dossier      ON CONTROLE_CONFORMITE (id_dossier_permis);
CREATE INDEX idx_certificat_controle   ON CERTIFICAT_NON_CONFORMITE (id_controle_conformite);
CREATE INDEX idx_sanction_certificat   ON SANCTION (id_certificat);
CREATE INDEX idx_sanction_echeance     ON SANCTION (echeance, id_statut);     -- sanctions en retard
CREATE INDEX idx_rapport_echeance      ON RAPPORT_REUNION (date_echeance);    -- plaintes à constater

-- Liens polymorphes : recherche de tout ce qui se rattache à un objet
CREATE INDEX idx_hist_action_objet     ON HISTORIQUE_ACTION (type_objet, id_objet);
CREATE INDEX idx_hist_action_user      ON HISTORIQUE_ACTION (id_utilisateur, date_action);
CREATE INDEX idx_hist_connexion_user   ON HISTORIQUE_CONNEXION (id_utilisateur, date_connexion);
CREATE INDEX idx_session_active ON SESSION_AUTHENTIFICATION (id_utilisateur, date_expiration)
    WHERE date_revoquee IS NULL;
CREATE INDEX idx_piece_jointe_objet    ON PIECE_JOINTE (type_objet, id_objet);
CREATE INDEX idx_visa_objet            ON VISA (type_objet, id_objet);
CREATE INDEX idx_decision_objet        ON DECISION (type_objet, id_objet);
CREATE INDEX idx_intervention_objet    ON INTERVENTION (type_objet, id_objet);

-- ---------------------------------------------------------------------
-- 9. Numérotation des identifiants
-- Atomique : l'UPDATE verrouille la ligne du compteur jusqu'à la fin de
-- la transaction, donc deux utilisateurs n'obtiennent jamais le même
-- numéro. À appeler dans la même transaction que l'INSERT de l'objet.
--
--   SELECT generer_id('SIG', TRUE, 4);   -- SIG-2026-0001  (repart chaque année)
--   SELECT generer_id('DEV');            -- DEV-00001      (compteur continu)
--
-- Préfixes annuels (numérotation officielle) : SIG, ORM, CNV, DPC, CNC, PVB.
-- Les autres tables utilisent un compteur continu : DEV, DVL, INT, DSC,
-- PLA, CND, RPR, RPA, CTL, SAN, PJT, VIS, DEC, PER, UTI, AGT, HAC, HCO, POP.
-- Le numéro imprimé sur les documents (ex. 003/CR/AH/2026) se déduit de
-- l'identifiant dans le code applicatif.
-- ---------------------------------------------------------------------

CREATE OR REPLACE FUNCTION generer_id(
   p_prefixe TEXT,
   p_annuel  BOOLEAN DEFAULT FALSE,
   p_largeur INTEGER DEFAULT 5
) RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
   v_annee INTEGER := CASE WHEN p_annuel THEN EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER END;
   v_cle   TEXT    := p_prefixe || CASE WHEN p_annuel
                                        THEN '-' || EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER
                                        ELSE '' END;
   v_val   INTEGER;
   v_txt   TEXT;
BEGIN
   INSERT INTO SEQUENCE AS s (id_sequence, annee, derniere_valeur, format)
   VALUES (v_cle, v_annee, 1, v_cle || '-' || repeat('N', p_largeur))
   ON CONFLICT (id_sequence) DO UPDATE SET derniere_valeur = s.derniere_valeur + 1
   RETURNING s.derniere_valeur INTO v_val;

   v_txt := v_val::TEXT;
   IF length(v_txt) < p_largeur THEN
      v_txt := lpad(v_txt, p_largeur, '0');   -- jamais de troncature au-delà de la largeur
   END IF;
   RETURN v_cle || '-' || v_txt;
END;
$$;

-- ----------------------------- DONNEES --------------------------------
-- Les statuts transversaux sont initialisés dans la section « Données
-- initiales », après les référentiels du socle.

-- ---------------------------------------------------------------------
