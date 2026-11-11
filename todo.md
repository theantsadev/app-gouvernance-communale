| ID | Catégorie | Module | Page | Tâche détaillée | Type | Qui | Estimation | Temps passé | Reste à faire | Avancement |
|---:|---|---|---|---|---|---|---:|---:|---:|---:|
| 0 | Préparation | Analyse et conception |  | Analyse, conception (E/A et base de données) | Global | ETU003327 | 3840 | 3840 | 0 | 100,00% |
| 1 | Préparation | Design |  | Maquettes des écrans (smartphone en priorité) | Affichage | ETU003327 | 1440 | 1440 | 0 | 100,00% |
| 2 | Préparation | Préparation technique |  | Renforcement des compétences nécessaires (Java, React.js, PostGIS) | Global | ETU003327 | 960 | 960 | 0 | 100,00% |
| 3 | Préparation | Préparation technique |  | Installation des environnements | Global | ETU003327 | 480 | 480 | 0 | 100,00% |
| 4 | Développement | Socle commun | Header | Création du header général | Affichage | ETU003327 | 20 | 20 | 0 | 100,00% |
| 5 | Développement | Socle commun | Sidebar | Création du sidebar  général | Affichage | ETU003327 | 20 | 20 | 0 | 100,00% |
| 6 | Développement | Socle commun | Footer | Création du footer général | Affichage | ETU003327 | 10 | 10 | 0 | 100,00% |
| 7 | Développement | Socle commun | Layout | Création du layout général réutilisable | Affichage | ETU003327 | 10 | 10 | 0 | 100,00% |
| AUTH | Développement | Authentification |  | Connexion et session sécurisée sans déconnexion, rôles ni permissions (USR-003) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| LOGIN001 | Développement | Authentification | LoginPage | Création de la page de login sous socle/auth | Affichage | ETU003327 | 10 | 10 | 0 | 100,00% |
| LOGIN002 | Développement | Authentification |  | Création de la classe Utilisateur (exactement attribut de la base en version camelCase sans renommage) sous socle/entity | Métier | ETU003327 | 5 | 0 |  | #DIV/0! |
| LOGIN003 | Développement | Authentification |  | Création de la classe ou record UtilisateurLib (idUtilisateur,email,nom,prenom) sous socle/lib | Métier | ETU003327 | 5 | 0 |  | #DIV/0! |
| LOGIN004 | Développement | Authentification |  | Création de la fonction UtilisateurLib login(LoginRequest requete) dans AuthService avec vérification de l'utilisateur, du mot de passe et du compte actif | Métier | ETU003327 | 30 | 0 | 30 | 0,00% |
| LOGIN005 | Développement | Authentification |  | Création de l'api : /api/socle/auth/login avec la fonction UtilisateurLib login(LoginRequest requete) dans AuthController | Métier | ETU003327 | 20 | 0 |  | #DIV/0! |
| LOGIN006 | Développement | Authentification |  | Creation de la fonction login(email,motDePasse) sous src/services/authApi.ts qui appelle /api/socle/auth/login | Intégration | ETU003327 | 20 | 0 |  | #DIV/0! |
| LOGIN007 | Développement | Authentification |  | Création de la classe LoginRequest (email,motDePasse) avec contrôle des champs sous socle/dto | Métier | ETU003327 | 5 | 0 |  | #DIV/0! |
| LOGIN008 | Développement | Authentification |  | Création de la classe HistoriqueConnexion (exactement attribut de la base en version camelCase sans renommage) sous socle/entity | Métier | ETU003327 | 5 | 0 |  | #DIV/0! |
| LOGIN009 | Développement | Authentification |  | Création de la fonction d'enregistrement des connexions et des échecs dans HistoriqueConnexionService avec une transaction séparée pour les échecs | Métier | ETU003327 | 15 | 0 | 15 | 0,00% |
| LOGIN010 | Développement | Authentification |  | Création du service de génération du token JWT avec durée et secret lus dans les variables d'environnement sous socle/security | Métier | ETU003327 | 20 | 0 | 20 | 0,00% |
| LOGIN011 | Tests | Authentification |  | Création des tests de connexion : champs invalides, identifiants invalides, compte désactivé, connexion valide et conservation de l'échec dans l'historique | Tests | ETU003327 | 30 | 0 | 30 | 0,00% |
| 9 | Développement | Socle commun |  | Gestion des rôles et permissions : maire, SG, chefs de service, agents, administrateur (USR-002) |  | ETU003327 | 1200 | 0 | 1200 | 0,00% |
| 10 | Développement | Socle commun |  | CRUD comptes utilisateurs (USR-001) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 11 | Développement | Socle commun |  | Référentiels : FKT, types de signalement, statuts, services (SYS-001) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 12 | Développement | Socle commun |  | Stockage et consultation des pièces jointes (SYS-003) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 13 | Développement | Socle commun |  | Fiches des agents (SIG-020) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 14 | Développement | Signalements et interventions |  | Enregistrement d'un signalement avec numéro d'arrivée automatique (SIG-001) |  | ETU003327 | 960 | 0 | 960 | 0,00% |
| 15 | Développement | Signalements et interventions |  | Visa du chef FKT (SIG-002) |  | ETU003327 | 240 | 0 | 240 | 0,00% |
| 16 | Développement | Signalements et interventions |  | Qualification et priorisation du signalement (SIG-003) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 17 | Développement | Signalements et interventions |  | Établissement du devis (SIG-013) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 18 | Développement | Signalements et interventions |  | Validation du devis par le maire et décision de la Finance (SIG-014, SIG-015) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 19 | Développement | Signalements et interventions |  | Ordre de mission avec numéro automatique et visa du maire (SIG-007, SIG-008) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 20 | Développement | Signalements et interventions |  | Planification d'une intervention et affectation des agents (SIG-009) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 21 | Développement | Signalements et interventions |  | Planning de la semaine par agent et par FKT (SIG-010) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 22 | Développement | Signalements et interventions |  | Enregistrement d'une descente (SIG-006) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 23 | Développement | Signalements et interventions |  | Clôture d'une intervention avec rapport et coût réel (SIG-011) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 24 | Développement | Signalements et interventions |  | Suivi des statuts et historique des actions (SIG-004) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 25 | Développement | Signalements et interventions |  | Recherche et filtres (SIG-005) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 26 | Développement | Signalements et interventions |  | Statistiques des signalements et interventions (SIG-018) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 27 | Développement | Plaintes entre citoyens |  | Ouverture d'une procédure de plainte et suivi des statuts (EHA-011) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 28 | Développement | Plaintes entre citoyens |  | Émission de la convocation avec numéro automatique et coupon de retour (EHA-012) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 29 | Développement | Plaintes entre citoyens |  | Rapport de réunion : décision, délai et échéance (EHA-014) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 30 | Développement | Urbanisme |  | Demande de permis et suivi des statuts (URB-001, URB-003) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 31 | Développement | Urbanisme |  | Pièces jointes et vérification des 6 pièces (URB-002) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 32 | Développement | Urbanisme |  | Décision du maire : accord ou refus avec motif (URB-004) |  | ETU003327 | 240 | 0 | 240 | 0,00% |
| 33 | Développement | Urbanisme |  | Statistiques de zone sur la fiche du dossier (URB-005) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 34 | Développement | Urbanisme |  | Descente de contrôle de conformité (URB-007) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 35 | Développement | Urbanisme |  | Recherche multicritère des dossiers (URB-006) |  | ETU003327 | 240 | 0 | 240 | 0,00% |
| 36 | Développement | Urbanisme |  | Statistiques de l'urbanisme (URB-010) |  | ETU003327 | 720 | 0 | 720 | 0,00% |
| 37 | Développement | Tableau de bord du maire |  | Vue d'ensemble par service : en cours, terminés, en attente (TDB-001) |  | ETU003327 | 960 | 0 | 960 | 0,00% |
| 38 | Développement | Tableau de bord du maire |  | Zones prioritaires par nombre de signalements (TDB-003) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 39 | Développement | Tableau de bord du maire |  | Filtres par période et par FKT (TDB-002) |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 40 | Développement | Imprévus |  | Marge pour imprévus (cahier des charges, section 15) |  | ETU003327 | 1440 | 0 | 1440 | 0,00% |
| 41 | Tests |  |  | Tests, intégration et corrections |  | ETU003327 | 1920 | 0 | 1920 | 0,00% |
| 42 | Déploiement |  |  | Déploiement |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 43 | Formation |  |  | Rédaction du manuel d'utilisation |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 44 | Formation |  |  | Formation des utilisateurs |  | ETU003327 | 480 | 0 | 480 | 0,00% |
| 45 | Gestion de projet |  |  | Gestion de projet |  | ETU003327 | 480 | 0 | 480 | 0,00% |
