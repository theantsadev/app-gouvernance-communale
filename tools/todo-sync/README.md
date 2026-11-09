# Synchronisation Excel vers `todo.md`

Cet outil lit la feuille `liste des taches` de `Todo-stage-3327.xlsx` et
reconstruit le tableau Markdown de `todo.md`.

## Installation

Depuis `tools/todo-sync/` :

```powershell
composer install
```

## Synchronisation ponctuelle

```powershell
php bin/sync.php
```

## Synchronisation automatique

Le processus vérifie le classeur toutes les cinq secondes :

```powershell
php bin/watch.php
```

## Serveur HTTP

```powershell
php -S localhost:8090 -t public
```

Puis appeler `http://localhost:8090/` après chaque modification du classeur.
La requête `GET` ou `POST` déclenche une synchronisation.

Pour protéger la route :

```powershell
$env:TODO_SYNC_TOKEN="jeton-local"
php -S localhost:8090 -t public
```

Envoyer alors l'en-tête :

```text
Authorization: jeton-local
```

Les chemins peuvent être remplacés par `TODO_XLSX_PATH` et `TODO_MD_PATH`.
