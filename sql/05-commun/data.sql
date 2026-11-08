-- Gouvernance communale
-- Fichier de module : execute par gouvernance_communale.sql avec psql

-- MODULE : COMMUN / DONNEES
-- =====================================================================
INSERT INTO STATUT (id_statut, type_objet, code, libelle, modifiable)
SELECT type_objet || '_' || code, type_objet, code, libelle, modifiable
FROM (VALUES
   ('SIGNALEMENT',    10, 'Recu',                         TRUE),
   ('SIGNALEMENT',    20, 'Qualifie',                     TRUE),
   ('SIGNALEMENT',    30, 'Devis en validation',          FALSE),
   ('SIGNALEMENT',    40, 'Depense autorisee',            FALSE),
   ('SIGNALEMENT',    50, 'Intervention planifiee',       FALSE),
   ('SIGNALEMENT',    60, 'Ordre de mission vise',        FALSE),
   ('SIGNALEMENT',    70, 'Intervention realisee',        FALSE),
   ('SIGNALEMENT',    80, 'Cloture',                      FALSE),
   ('SIGNALEMENT',    90, 'En attente',                   FALSE),
   ('SIGNALEMENT',    99, 'Annule',                       FALSE),
   ('DEVIS',          10, 'Brouillon',                    TRUE),
   ('DEVIS',          20, 'Soumis au maire',              FALSE),
   ('DEVIS',          30, 'Valide',                       FALSE),
   ('DEVIS',          40, 'Refuse',                       FALSE),
   ('ORDRE_MISSION',  10, 'Etabli',                       TRUE),
   ('ORDRE_MISSION',  20, 'Vise',                         FALSE),
   ('ORDRE_MISSION',  30, 'Refuse',                       FALSE),
   ('ORDRE_MISSION',  99, 'Annule',                       FALSE),
   ('INTERVENTION',   10, 'Planifiee',                    TRUE),
   ('INTERVENTION',   20, 'En cours',                     TRUE),
   ('INTERVENTION',   30, 'Realisee',                     TRUE),
   ('INTERVENTION',   40, 'Cloturee',                     FALSE),
   ('INTERVENTION',   99, 'Annulee',                      FALSE),
   ('PLAINTE',        10, 'Plainte ouverte',              TRUE),
   ('PLAINTE',        20, 'Convocation emise',            FALSE),
   ('PLAINTE',        30, 'Reunion tenue',                FALSE),
   ('PLAINTE',        40, 'Resolu',                       FALSE),
   ('PLAINTE',        50, 'Non resolu',                   FALSE),
   ('PLAINTE',        99, 'Annulee',                      FALSE),
   ('DOSSIER_PERMIS', 10, 'Depose',                       TRUE),
   ('DOSSIER_PERMIS', 20, 'En traitement',                TRUE),
   ('DOSSIER_PERMIS', 30, 'Complet',                      TRUE),
   ('DOSSIER_PERMIS', 40, 'Permis delivre',               FALSE),
   ('DOSSIER_PERMIS', 50, 'Refuse',                       FALSE),
   ('DOSSIER_PERMIS', 99, 'Annule',                       FALSE),
   ('CERTIFICAT_NC',  10, 'Emis',                         TRUE),
   ('CERTIFICAT_NC',  20, 'Signe',                        FALSE),
   ('CERTIFICAT_NC',  99, 'Annule',                       FALSE),
   ('SANCTION',       10, 'A payer',                      TRUE),
   ('SANCTION',       20, 'Payee',                        FALSE),
   ('SANCTION',       99, 'Annulee',                      FALSE)
) AS t(type_objet, code, libelle, modifiable);

-- =====================================================================
-- MODULE : COMMUN / SCHEMA COMPLEMENTAIRE
-- =====================================================================
-- Si le numero du tampon d'arrivee vient d'un registre tenu a la main :
-- ALTER TABLE SIGNALEMENT ADD COLUMN numero_courrier VARCHAR(50);

-- ---------------------------------------------------------------------
-- 11. Tracabilite : utilisateur qui a enregistre l'objet
-- Une seule colonne, au meme nom dans toutes les tables : id_utilisateur.
-- Elle permet a une classe Java mere (Tracable) de la porter pour les
-- entites qui en heritent. " Qui a modifie, transfere, cloture " reste
-- dans HISTORIQUE_ACTION.
-- NOT NULL : a executer sur des tables vides. S'il y a deja des donnees,
-- ajouter la colonne sans NOT NULL, la remplir, puis SET NOT NULL.
-- ---------------------------------------------------------------------

ALTER TABLE SIGNALEMENT              ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE DEVIS                    ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE INTERVENTION             ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE ORDRE_MISSION            ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE DESCENTE                 ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE PLAINTE                  ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE CONVOCATION              ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE CONVOCATION_DESTINATAIRE ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE RAPPORT_REUNION          ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE PROCES_VERBAL            ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
ALTER TABLE DOSSIER_PERMIS           ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);

-- Optionnel : memes colonnes pour les trois tables d'urbanisme restantes,
-- si elles doivent aussi heriter de la classe mere.
-- ALTER TABLE CONTROLE_CONFORMITE      ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
-- ALTER TABLE CERTIFICAT_NON_CONFORMITE ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);
-- ALTER TABLE SANCTION                 ADD COLUMN id_utilisateur VARCHAR(20) NOT NULL REFERENCES UTILISATEUR(id_utilisateur);

-- =====================================================================
-- MODULE : SOCLE COMMUN / DONNEES COMPLEMENTAIRES
-- =====================================================================
-- Permissions propres a un service.
ALTER TABLE PERMISSION ADD COLUMN id_service VARCHAR(20) REFERENCES SERVICE(id_service);

UPDATE PERMISSION SET id_service = 'EHA'
 WHERE ressource IN ('plainte', 'convocation', 'rapport_reunion', 'proces_verbal');

UPDATE PERMISSION SET id_service = 'ST'
 WHERE ressource IN ('permis', 'decision_permis', 'controle', 'certificat', 'sanction');

ALTER TABLE UTILISATEUR
   ADD CONSTRAINT ck_chef_service CHECK (id_role <> 'CHEF_SERVICE' OR id_service IS NOT NULL);

-- =====================================================================
