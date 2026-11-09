# Gouvernance communale

Application web de gouvernance pour la Commune Rurale d'Andranonahoatra.

Le projet couvre les services EHA, Voirie et Urbanisme : signalements,
interventions, plaintes, permis de construire et tableau de bord du maire.
La saisie directe par les citoyens est hors périmètre de la version 1.

## Organisation

- [`backend/`](./backend/) : API Spring Boot, Java 17, JPA et Spring Security.
- [`frontend/`](./frontend/) : interface React + TypeScript + Vite.
- [`sql/`](./sql/) : schéma PostgreSQL/PostGIS et données initiales par module.
- [`todo.md`](./todo.md) : suivi détaillé, une ligne correspondant à une petite tâche.
- [`tools/todo-sync/`](./tools/todo-sync/) : synchronisation de la feuille Excel `liste des taches` vers `todo.md`.
- [`docs/manuel-utilisateur.md`](./docs/manuel-utilisateur.md) : guide utilisateur.
- [`maquettes/`](./maquettes/) : maquettes fonctionnelles.

## Pré-requis

- Java 17
- Node.js et npm
- PostgreSQL avec PostGIS

## Base de données

Créer la base depuis une base d'administration :

```sql
CREATE DATABASE gouvernance_communale;
```

Puis, depuis '/' :

```text
\ir 'E:/licence/final/app-gouvernance-communale/gouvernance_communale.sql'
```

Le script principal appelle les fichiers de [`sql/`](./sql/) dans l'ordre
des dépendances.

## Lancer le projet

Backend, dans un terminal :

```powershell
cd E:\licence\final\app-gouvernance-communale\backend
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:DB_URL="jdbc:postgresql://localhost:5433/gouvernance_communale"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="votre_mot_de_passe"
$env:JWT_SECRET="une-cle-secrete-developpement-de-32-caracteres"
$env:ADMIN_EMAIL="adresse-administrateur"
$env:ADMIN_PASSWORD="mot-de-passe-administrateur"
.\mvnw.cmd spring-boot:run
```

Au démarrage, le backend crée le premier administrateur uniquement si aucun
administrateur n'existe. Les identifiants sont lus dans `ADMIN_EMAIL` et
`ADMIN_PASSWORD` et ne sont jamais exposés par une route publique.

Frontend, dans un deuxième terminal :

```powershell
cd E:\licence\final\app-gouvernance-communale\frontend
npm install
npm run dev
```

Ouvrir ensuite `http://localhost:5173`.

## Développement

Une petite tâche doit rester isolée : un changement fonctionnel ciblé,
une ligne du suivi et un commit. Les valeurs sensibles restent dans les
variables d'environnement et ne doivent jamais être commités.

Les attributs Java suivent le camelCase (`idRole`) et leurs noms SQL sont
déclarés explicitement dans le mapping JPA (`id_role`).

## Synchroniser le suivi Excel

Depuis [`tools/todo-sync/`](./tools/todo-sync/) :

```powershell
composer install
php bin/sync.php
```

Pour surveiller automatiquement les modifications du classeur :

```powershell
php bin/watch.php
```
