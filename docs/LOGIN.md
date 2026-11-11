# LOGIN001-LOGIN006 : Connexion sécurisée

* Source : AUTH (connexion et session sécurisée)
* Maquette : `maquettes/login-layout.jpg`
* Module : `socle/auth`
* Permission : aucune (route publique)
* Statut : non authentifié vers authentifié

Hors fiche : déconnexion (USR-003-SECURITE), rôles et permissions (USR-002).

## Affichage

Page publique `/connexion`, hors du layout authentifié.

- **E-mail** : obligatoire, type `email`.
- **Mot de passe** : obligatoire, type `password`.
- Bouton **Se connecter** : désactivé pendant l'envoi (« Connexion... »).
- Erreur liée à un champ : affichée sous ce champ. Autre erreur : message général au-dessus du bouton.
- Succès : session enregistrée sous `commune.session`, redirection vers `/`.
- Aucun champ prérempli, aucun bouton de création de compte. Le mot de passe n'est jamais conservé.
- Utilisable sur smartphone et desktop.

## Métier

Ordre : contrôle unitaire, contrôle complexe, règle métier, persistance, retour de l'objet.

Messages d'erreur exacts :

| Code | Message | Champ |
|---|---|---|
| `FORMULAIRE_INVALIDE` | « L'e-mail est obligatoire. » | `email` |
| `FORMULAIRE_INVALIDE` | « Le format de l'e-mail est invalide. » | `email` |
| `FORMULAIRE_INVALIDE` | « Le mot de passe est obligatoire. » | `motDePasse` |
| `AUTH_IDENTIFIANTS_INVALIDES` | « E-mail ou mot de passe incorrect. » | |
| `AUTH_COMPTE_DESACTIVE` | « Ce compte est désactivé. » | |

E-mail inconnu et mot de passe incorrect : même code, même message (ne révèle pas quels e-mails existent).
Le compte actif se vérifie après le mot de passe.

```java
public class LoginRequest {
    private String email;
    private String motDePasse;

    public LoginRequest(String email, String motDePasse) throws AuthException {
        this.setEmail(email);
        this.setMotDePasse(motDePasse);
    }

    public void setEmail(String email) throws AuthException {
        FieldController.controleNotNull(email, "email", "L'e-mail est obligatoire.");
        FieldController.controleFormatEmail(email, "email", "Le format de l'e-mail est invalide.");
        this.email = email;
    }

    public void setMotDePasse(String motDePasse) throws AuthException {
        FieldController.controleNotNull(motDePasse, "motDePasse", "Le mot de passe est obligatoire.");
        this.motDePasse = motDePasse;
    }
}

public record UtilisateurLib(String idUtilisateur, String email, String nom, String prenom) {}

public class AuthService {
    private UtilisateurService utilisateurService;
    private HistoriqueConnexionService historiqueConnexionService;

    @Transactional
    public UtilisateurLib login(LoginRequest requete) throws AuthException {
        // Contrôle unitaire : fait dans le constructeur et les setters de LoginRequest

        try {
            // Contrôle complexe : l'utilisateur existe (le throw est dans le service)
            Utilisateur utilisateur = utilisateurService.findByEmail(requete.getEmail());

            // Contrôle complexe : le mot de passe correspond
            if (!PassEncoder.match(requete.getMotDePasse(), utilisateur.getMotDePasseHash())) {
                throw AuthException.identifiantsInvalides();
            }

            // Contrôle complexe : le compte est actif
            if (!utilisateur.isActif()) {
                throw new AuthException("AUTH_COMPTE_DESACTIVE", "Ce compte est désactivé.");
            }

            // Persistance : ligne CONNEXION
            historiqueConnexionService.enregistrerConnexion(utilisateur);

            // Retour de l'objet
            return new UtilisateurLib(utilisateur.getIdUtilisateur(), utilisateur.getEmail(),
                utilisateur.getNom(), utilisateur.getPrenom());
        } catch (AuthException e) {
            // Persistance : ligne ECHEC, dans une transaction séparée (REQUIRES_NEW)
            historiqueConnexionService.enregistrerEchec(requete.getEmail(), e);
            throw e;
        }
    }
}
```

Le mot de passe saisi et le hash ne sont jamais retournés ni journalisés. Un contrôle unitaire en échec n'écrit rien dans l'historique.

## Base

Lecture : `UTILISATEUR` (`id_utilisateur`, `email`, `mot_de_passe_hash`, `nom`, `prenom`, `actif`), recherche par l'index unique `uq_utilisateur_email` sur `lower(email)`.

Écriture : `HISTORIQUE_CONNEXION`.

- `id_connexion` : `generer_id('HCO')`
- `email_saisi`, `type` (`CONNEXION` ou `ECHEC`), `date_connexion`, `adresse_ip`
- `id_utilisateur` : vide si l'e-mail est inconnu

La ligne `ECHEC` s'écrit dans sa propre transaction, sinon le rollback de `login` l'annule.
Aucune nouvelle table : le jeton JWT n'est pas stocké.
Le secret et la durée du jeton viennent des variables d'environnement.

## Intégration

```http
POST /api/socle/auth/login
```

Corps :

```json
{ "email": "...", "motDePasse": "..." }
```

Réponse `200` :

```json
{
  "token": "...",
  "expiresIn": 3600,
  "utilisateur": { "idUtilisateur": "...", "email": "...", "nom": "...", "prenom": "..." }
}
```

Erreur : `{ "code": "...", "message": "...", "champ": "..." }` (`champ` seulement pour une erreur de champ).

| HTTP | Code |
|---:|---|
| 400 | `FORMULAIRE_INVALIDE` |
| 401 | `AUTH_IDENTIFIANTS_INVALIDES` |
| 403 | `AUTH_COMPTE_DESACTIVE` |

Les services lancent `AuthException`. Le gestionnaire global (`@RestControllerAdvice`) la transforme en réponse, sans `try/catch` dans le contrôleur. Permission requise : aucune. Périmètre de service : aucun.

Front :

- `services/authApi.ts` : `login(email, motDePasse)` appelle l'API et lève une `ApiError` en cas d'erreur.
- `LoginPage.tsx` : appelle `login`, enregistre `commune.session`, redirige vers `/`, affiche l'erreur sous le champ indiqué par `champ` ou en message général.
- Ni le mot de passe ni le token ne sont journalisés.

## Critères d'acceptation

- [ ] Le cas normal fonctionne de l'écran jusqu'à la base (token reçu, ligne `CONNEXION` écrite).
- [ ] Chaque message d'erreur du métier a son test.
- [ ] E-mail inconnu et mauvais mot de passe : même code et même message.
- [ ] Compte inactif : erreur 403 distincte.
- [ ] Tentative refusée : ligne `ECHEC` écrite et conservée malgré le rollback (`id_utilisateur` vide si e-mail inconnu).
- [ ] Mot de passe et hash absents de la réponse et des logs.
- [ ] Page conforme à la maquette, utilisable sur smartphone et desktop.