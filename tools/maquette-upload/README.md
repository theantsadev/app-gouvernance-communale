# Upload des maquettes

Petit site mobile pour envoyer des images dans le dossier `maquettes/` du
projet.

## Démarrer le serveur

Depuis `tools/maquette-upload/` :

```powershell
php -d upload_max_filesize=20M -d post_max_size=205M -S 0.0.0.0:8091 -t public
```

Sur l’ordinateur, ouvrir `http://localhost:8091`. Pour l’utiliser depuis un
téléphone connecté au même réseau Wi-Fi, ouvrir `http://ADRESSE-IP-DU-PC:8091`
sur le téléphone. L’adresse IP locale du PC est affichée par
`ipconfig` (IPv4 de la connexion Wi-Fi ou Ethernet). Le pare-feu Windows peut
demander d’autoriser PHP sur le réseau privé.

Le serveur écoute sur le réseau local et ne fournit pas de chiffrement. Ne
l’utilisez que sur un réseau de confiance et arrêtez-le après les envois.

Pour demander un code d’accès, le définir avant le démarrage du serveur :

```powershell
$env:MAQUETTE_UPLOAD_TOKEN="votre-code"
php -d upload_max_filesize=20M -d post_max_size=205M -S 0.0.0.0:8091 -t public
```

Les images JPG, PNG, WebP, AVIF et HEIC sont acceptées (20 Mo maximum par
fichier, 10 fichiers par envoi). Les fichiers déjà présents ne sont jamais
remplacés ; un suffixe est ajouté en cas de nom identique.
