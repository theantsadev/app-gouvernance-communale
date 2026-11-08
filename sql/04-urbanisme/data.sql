-- Gouvernance communale
-- Fichier de module : execute par gouvernance_communale.sql avec psql

-- MODULE : URBANISME / DONNEES
-- =====================================================================
INSERT INTO TYPE_PERMIS (id_type_permis, libelle) VALUES
   ('PC', 'Permis de construire');

INSERT INTO PIECE_REQUISE (id_piece_requise, libelle, ordre, obligatoire, id_type_permis) VALUES
   ('PC-DEMANDE',      'Demande',                                1, TRUE, 'PC'),
   ('PC-SITUATION',    'Situation juridique',                    2, TRUE, 'PC'),
   ('PC-CADASTRE',     'Plan cadastral',                         3, TRUE, 'PC'),
   ('PC-PLAN',         'Plan de la maison',                      4, TRUE, 'PC'),
   ('PC-PRESCRIPTION', 'Prescription d''urbanisme / alignement', 5, TRUE, 'PC'),
   ('PC-IMPOT',        'Recu du dernier impot',                  6, TRUE, 'PC');

-- A remplir avec les listes reelles de la commune (non inventees ici) :
--   FKT (avec geom quand les limites seront numerisees), FKT_POPULATION,
--   URGENCE, RISQUE, TYPE_SIGNALEMENT (par service), autres CANAL.

-- =====================================================================
