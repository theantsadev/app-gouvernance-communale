# SESSION001-SESSION013 : Gestion de session authentifiée

* Source : AUTH (connexion et session sécurisée)
* Fiche liée : `docs/LOGIN.md`
* Module : `socle/auth`
* Maquette : aucune (fonction transverse)
* Statut : authentifié vers session valide ou déconnectée

Cette fiche complète la connexion. Le JWT identifie la requête, tandis que la
session persistée permet de vérifier qu'il n'a pas été révoqué. Les rôles et
permissions détaillées sont hors périmètre.

## Affichage

Le layout authentifié ne doit jamais afficher de données utilisateur par défaut.

- Au démarrage, vérifier la présence de `commune.session`.
- Appeler `/api/socle/auth/session/valid`.
- Récupérer l'utilisateur courant avec `/api/socle/utilisateur/current`.
- Afficher le layout seulement après validation de la session et récupération de
  l'utilisateur.
- Rediriger vers `/connexion` si la session est absente, expirée, révoquée ou
  refusée par le backend.
- La déconnexion supprime toujours `commune.session` côté navigateur.
- Une réponse HTTP `401` supprime automatiquement la session locale et force le
  retour vers la connexion.

Le frontend ne considère jamais la présence du token dans `localStorage` comme
une preuve suffisante d'authentification.

## Métier

Ordre : extraction du token, contrôle du JWT, contrôle de la session persistée,
contrôle de l'utilisateur, placement dans le `SecurityContext`.

### Création

Après une connexion réussie :

1. `AuthService` génère le JWT avec `JwtService`.
2. `SessionAuthentificationService` calcule l'empreinte SHA-256 du JWT.
3. La session est persistée avec son identifiant, l'utilisateur et la date
   d'expiration.
4. Le JWT brut n'est jamais enregistré en base.

### Validation d'une requête

`JwtAuthenticationFilter` :

1. extrait une seule fois le token `Bearer` avec `JwtService.extraireToken`;
2. vérifie la signature et l'expiration du JWT ;
3. vérifie que la session existe, n'est pas révoquée et n'est pas expirée ;
4. vérifie que l'utilisateur existe et est actif via `UtilisateurService`;
5. place `idUtilisateur` dans le `SecurityContext`.

Une requête sans token ou avec un token invalide continue sans authentification
et est refusée par Spring Security pour les routes protégées.

### Déconnexion

`AuthController` extrait le token et les informations de la requête, puis
délègue à `AuthService.logout(token, idUtilisateur, adresseIp)`.

`AuthService` :

- recherche l'utilisateur avec `UtilisateurService` ;
- révoque la session avec `SessionAuthentificationService` ;
- écrit l'événement `DECONNEXION` dans l'historique.

Le contrôleur ne recherche pas l'utilisateur, ne révoque pas la session et
n'écrit pas directement l'historique.

### Services réutilisables

- `JwtService.extraireToken(HttpServletRequest)` : extraction commune du token.
- `SessionAuthentificationService.creer(...)` : création persistée.
- `SessionAuthentificationService.estValide(token)` : validation réutilisable.
- `SessionAuthentificationService.revoquer(token)` : révocation réutilisable.
- `UtilisateurService.findById(idUtilisateur)` : recherche centralisée.
- `UtilisateurService.findPublicById(idUtilisateur)` : utilisateur courant
  sans exposition de l'entité JPA.

Les contrôleurs et les filtres appellent les services ; ils n'appellent jamais
directement les repositories.

## Base

Table `SESSION_AUTHENTIFICATION` :

| Colonne | Rôle |
|---|---|
| `id_session` | identifiant préfixé généré par `generer_id('SES')` |
| `empreinte_token` | empreinte SHA-256 du JWT, jamais le JWT brut |
| `id_utilisateur` | utilisateur propriétaire de la session |
| `date_expiration` | date d'expiration du JWT |
| `date_creation` | date de création de la session |
| `date_revoquee` | date de révocation, vide pour une session active |

Une session est valide lorsque :

```text
empreinte_token correspondante
ET date_revoquee IS NULL
ET date_expiration > maintenant
```

Le schéma est défini dans `sql/01-socle/schema.sql` et validé par Hibernate
avec `ddl-auto=validate`. Le secret et la durée du JWT viennent des variables
d'environnement `JWT_SECRET` et `JWT_EXPIRATION_SECONDS`.

## Intégration

### Déconnexion

```http
POST /api/socle/auth/logout
Authorization: Bearer <token>
```

Réponse : `204 No Content`.

### Vérification de session

```http
POST /api/socle/auth/session/valid
Authorization: Bearer <token>
```

Réponse `200` :

```json
{ "valide": true }
```

### Utilisateur courant

```http
GET /api/socle/utilisateur/current
Authorization: Bearer <token>
```

Réponse `200` :

```json
{
  "idUtilisateur": "...",
  "email": "...",
  "nom": "...",
  "prenom": "..."
}
```

Les trois routes sont protégées. Le filtre JWT doit avoir placé
`idUtilisateur` dans le `SecurityContext` avant l'appel du contrôleur.

### Frontend

- `services/sessionStorage.ts` centralise la clé `commune.session`.
- `services/apiClient.ts` ajoute automatiquement `Authorization: Bearer`.
- `services/apiClient.ts` supprime la session locale sur une réponse `401`.
- `services/authApi.ts` contient `logout()`, `sessionValide()` et
  `utilisateurConnecte()`.
- `Layout.tsx` vérifie la session avant d'afficher le layout.

Le token n'est jamais écrit dans l'URL, dans les logs ou dans une réponse
autre que celle prévue par la connexion.

## Critères d'acceptation

- [ ] Une session est créée et persistée après une connexion réussie.
- [ ] Le JWT brut n'est jamais stocké en base.
- [ ] Un token absent, invalide ou expiré ne remplit pas le `SecurityContext`.
- [ ] Une session révoquée est refusée par le filtre JWT.
- [ ] Une session expirée est refusée par le filtre JWT.
- [ ] `POST /api/socle/auth/logout` révoque la session et historise la
  déconnexion.
- [ ] `POST /api/socle/auth/session/valid` confirme une session valide.
- [ ] `GET /api/socle/utilisateur/current` retourne l'utilisateur du
  `SecurityContext`, sans exposer l'entité JPA.
- [ ] Le layout redirige vers `/connexion` si la session est absente ou
  invalide.
- [ ] Une réponse `401` supprime `commune.session`.
- [ ] Les contrôleurs et filtres utilisent les services réutilisables, sans
  appel direct aux repositories.
