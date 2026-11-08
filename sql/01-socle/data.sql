-- Gouvernance communale
-- Fichier de module : execute par gouvernance_communale.sql avec psql

-- MODULE : SOCLE COMMUN / DONNEES
-- =====================================================================
INSERT INTO SERVICE (id_service, nom) VALUES
   ('EHA', 'EHA'),
   ('ST',  'Service technique');

INSERT INTO ROLE (id_role, nom, description) VALUES
   ('ADMINISTRATEUR',  'Administrateur',     'Gere les comptes, les roles, les referentiels et les parametres'),
   ('MAIRE',           'Maire',              'Consulte le tableau de bord, valide les devis, vise les ordres de mission, decide des permis'),
   ('SG',              'Secretaire general', 'Supervise tous les services, saisit la decision de la Finance'),
   ('CHEF_SERVICE',    'Chef de service',    'Gere les donnees de son domaine (EHA ou ST)'),
   ('AGENT_TECHNIQUE', 'Agent technique',    'Consulte ses missions, saisit constats et rapports');

INSERT INTO PERMISSION (id_permission, action, ressource, description)
SELECT p, split_part(p, ':', 1), split_part(p, ':', 2), d
FROM (VALUES
   ('gerer:compte',               'Gerer les comptes utilisateurs'),
   ('gerer:role',                 'Gerer les roles et leurs permissions'),
   ('gerer:referentiel',          'Gerer les referentiels (FKT, types, statuts, services)'),
   ('gerer:parametre',            'Gerer les parametres du systeme'),
   ('voir:historique',            'Consulter l''historique des actions et des connexions'),
   ('gerer:agent',                'Gerer les fiches des agents'),
   ('voir:tableau_bord',          'Consulter le tableau de bord'),
   ('voir:statistique',           'Consulter les statistiques'),
   ('voir:signalement',           'Consulter les signalements'),
   ('creer:signalement',          'Enregistrer un signalement'),
   ('modifier:signalement',       'Modifier et qualifier un signalement'),
   ('annuler:signalement',        'Annuler un signalement (avec motif)'),
   ('transferer:signalement',     'Transferer un signalement a un autre service'),
   ('voir:coordonnees_personne',  'Voir les coordonnees des demandeurs et des parties'),
   ('creer:devis',                'Etablir un devis'),
   ('valider:devis',              'Valider un devis'),
   ('creer:decision_finance',     'Enregistrer la decision de la Finance'),
   ('creer:ordre_mission',        'Etablir un ordre de mission'),
   ('voir:ordre_mission',         'Consulter les ordres de mission'),
   ('viser:ordre_mission',        'Viser un ordre de mission'),
   ('creer:intervention',         'Planifier une intervention'),
   ('modifier:intervention',      'Modifier une intervention, la cloturer (rapport et cout reel)'),
   ('voir:descente',              'Consulter les descentes'),
   ('creer:descente',             'Enregistrer une descente'),
   ('voir:plainte',               'Consulter les plaintes'),
   ('creer:plainte',              'Ouvrir une plainte'),
   ('modifier:plainte',           'Modifier une plainte'),
   ('creer:convocation',          'Emettre une convocation'),
   ('creer:rapport_reunion',      'Rediger un rapport de reunion'),
   ('creer:proces_verbal',        'Emettre un proces-verbal'),
   ('voir:permis',                'Consulter les dossiers de permis'),
   ('creer:permis',               'Enregistrer une demande de permis'),
   ('modifier:permis',            'Modifier un dossier de permis'),
   ('creer:decision_permis',      'Enregistrer la decision du maire sur un permis'),
   ('decider:permis',             'Decider d''un permis (accord ou refus)'),
   ('creer:controle',             'Enregistrer un controle de conformite'),
   ('creer:certificat',           'Emettre un certificat de non-conformite'),
   ('creer:sanction',             'Enregistrer une sanction'),
   ('modifier:sanction',          'Modifier une sanction, constater le paiement')
) AS t(p, d);

INSERT INTO ROLE_PERMISSION (id_role, id_permission) VALUES
   -- Administrateur
   ('ADMINISTRATEUR', 'gerer:compte'), ('ADMINISTRATEUR', 'gerer:role'),
   ('ADMINISTRATEUR', 'gerer:referentiel'), ('ADMINISTRATEUR', 'gerer:parametre'),
   ('ADMINISTRATEUR', 'voir:historique'), ('ADMINISTRATEUR', 'gerer:agent'),
   -- Maire
   ('MAIRE', 'voir:tableau_bord'), ('MAIRE', 'voir:statistique'), ('MAIRE', 'voir:signalement'),
   ('MAIRE', 'voir:plainte'), ('MAIRE', 'voir:permis'), ('MAIRE', 'voir:ordre_mission'),
   ('MAIRE', 'valider:devis'), ('MAIRE', 'viser:ordre_mission'), ('MAIRE', 'decider:permis'),
   -- Secretaire general
   ('SG', 'voir:tableau_bord'), ('SG', 'voir:statistique'), ('SG', 'voir:signalement'),
   ('SG', 'voir:plainte'), ('SG', 'voir:permis'), ('SG', 'voir:ordre_mission'),
   ('SG', 'voir:descente'), ('SG', 'transferer:signalement'),
   ('SG', 'voir:coordonnees_personne'), ('SG', 'creer:decision_finance'),
   -- Chef de service (le perimetre EHA ou ST se verifie avec UTILISATEUR.id_service)
   ('CHEF_SERVICE', 'voir:statistique'), ('CHEF_SERVICE', 'voir:signalement'),
   ('CHEF_SERVICE', 'creer:signalement'), ('CHEF_SERVICE', 'modifier:signalement'),
   ('CHEF_SERVICE', 'annuler:signalement'), ('CHEF_SERVICE', 'transferer:signalement'),
   ('CHEF_SERVICE', 'voir:coordonnees_personne'), ('CHEF_SERVICE', 'creer:devis'),
   ('CHEF_SERVICE', 'creer:ordre_mission'), ('CHEF_SERVICE', 'voir:ordre_mission'),
   ('CHEF_SERVICE', 'creer:intervention'), ('CHEF_SERVICE', 'modifier:intervention'),
   ('CHEF_SERVICE', 'voir:descente'),
   ('CHEF_SERVICE', 'creer:descente'), ('CHEF_SERVICE', 'gerer:agent'),
   ('CHEF_SERVICE', 'voir:plainte'), ('CHEF_SERVICE', 'creer:plainte'),
   ('CHEF_SERVICE', 'modifier:plainte'), ('CHEF_SERVICE', 'creer:convocation'),
   ('CHEF_SERVICE', 'creer:rapport_reunion'), ('CHEF_SERVICE', 'creer:proces_verbal'),
   ('CHEF_SERVICE', 'voir:permis'), ('CHEF_SERVICE', 'creer:permis'),
   ('CHEF_SERVICE', 'modifier:permis'), ('CHEF_SERVICE', 'creer:decision_permis'),
   ('CHEF_SERVICE', 'creer:controle'), ('CHEF_SERVICE', 'creer:certificat'),
   ('CHEF_SERVICE', 'creer:sanction'), ('CHEF_SERVICE', 'modifier:sanction'),
   -- Agent technique
   ('AGENT_TECHNIQUE', 'voir:ordre_mission'), ('AGENT_TECHNIQUE', 'voir:descente'),
   ('AGENT_TECHNIQUE', 'creer:descente');
   
-- Statuts. id_statut = type_objet + '_' + code. modifiable = faux : l'objet est verrouille.

INSERT INTO CANAL (id_canal, libelle) VALUES
   ('LETTRE', 'Lettre');
-- =====================================================================
