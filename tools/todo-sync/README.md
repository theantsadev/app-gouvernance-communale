# Synchronisation Excel et `todo.md`

Cet outil lit la feuille `liste des taches` de `Todo-stage-3327.xlsx` et
synchronise son tableau avec `todo.md`.

## Installation

Depuis `tools/todo-sync/` :

```powershell
composer install
```

## Synchronisation ponctuelle

```powershell
php bin/sync.php
```

Le sens peut être précisé en argument :

```powershell
# Excel vers todo.md (sens 1, valeur par défaut)
php bin/sync.php 1

# todo.md vers Excel
php bin/sync.php -1
```

Si la feuille `liste des taches` n’existe pas dans le classeur, elle est
créée automatiquement avec ses en-têtes.

Le sens HTTP peut être choisi avec le paramètre `sens` (`GET` ou `POST`) :
`http://localhost:8090/?sens=-1`.

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
