# Organisation SQL

Chaque module contient deux fichiers :

- `schema.sql` : tables, contraintes et objets techniques du module ;
- `data.sql` : référentiels et données initiales du module.

## Ordre d execution

Le fichier `gouvernance_communale.sql` est l orchestrateur psql. Il respecte les dépendances :

1. `01-socle`
2. `02-signalement`
3. `03-plainte`
4. `04-urbanisme`
5. `05-commun`
6. données du socle
7. données communes
8. données urbanisme

Créer la base séparément depuis une base d administration :

```sql
CREATE DATABASE gouvernance_communale;
```

Puis exécuter le script connecté à cette base :

```text
psql -d gouvernance_communale -f gouvernance_communale.sql
```

## Évolutions

Les évolutions destinées aux bases déjà existantes sont dans `sql/evolutions/`.
Elles sont exécutées après le schéma initial, dans l'ordre alphabétique de
leur nom :

```text
psql -v ON_ERROR_STOP=1 -d gouvernance_communale -f sql/evolutions/2026-11-12_ajout-session-authentification.sql
```

La migration de `SESSION_AUTHENTIFICATION` est idempotente : elle ne recrée
pas la table lorsque le schéma initial la contient déjà.

Depuis une session `psql`, utiliser un chemin absolu entre guillemets :

```text
\ir 'E:/licence/final/gouvernance_communale.sql'
```
