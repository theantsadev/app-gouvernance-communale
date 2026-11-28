<?php

declare(strict_types=1);

$projectRoot = dirname(__DIR__, 3);
$uploadDirectory = $projectRoot . DIRECTORY_SEPARATOR . 'maquettes';
$maxFiles = 10;
$maxFileSize = 20 * 1024 * 1024;
$uploadToken = getenv('MAQUETTE_UPLOAD_TOKEN') ?: '';

function respond(array $body, int $status = 200): never
{
    http_response_code($status);
    header('Content-Type: application/json; charset=utf-8');
    header('Cache-Control: no-store');
    echo json_encode($body, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

function uploadErrorMessage(int $error): string
{
    return match ($error) {
        UPLOAD_ERR_INI_SIZE, UPLOAD_ERR_FORM_SIZE => 'Le fichier dépasse la limite autorisée.',
        UPLOAD_ERR_PARTIAL => 'Le transfert du fichier est incomplet.',
        UPLOAD_ERR_NO_FILE => 'Aucun fichier sélectionné.',
        UPLOAD_ERR_NO_TMP_DIR, UPLOAD_ERR_CANT_WRITE, UPLOAD_ERR_EXTENSION => 'Le serveur n’a pas pu enregistrer le fichier.',
        default => 'Erreur inconnue pendant le transfert.',
    };
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postLimit = ini_get('post_max_size');
    if (preg_match('/^\s*(\d+(?:\.\d+)?)\s*([KMG]?)\s*$/i', $postLimit, $matches) === 1) {
        $units = ['' => 1, 'K' => 1024, 'M' => 1024 ** 2, 'G' => 1024 ** 3];
        $postLimitBytes = (int) ((float) $matches[1] * $units[strtoupper($matches[2])]);
        if ((int) ($_SERVER['CONTENT_LENGTH'] ?? 0) > $postLimitBytes) {
            respond(['error' => 'Le transfert dépasse la limite totale configurée sur le serveur.'], 413);
        }
    }

    if ($uploadToken !== '' && !hash_equals($uploadToken, (string) ($_SERVER['HTTP_X_UPLOAD_TOKEN'] ?? ''))) {
        respond(['error' => 'Le code d’accès est incorrect.'], 401);
    }

    if (!isset($_FILES['files']) || !is_array($_FILES['files']['name'] ?? null)) {
        respond(['error' => 'Choisissez au moins un fichier image.'], 400);
    }

    if (!is_dir($uploadDirectory) || !is_writable($uploadDirectory)) {
        respond(['error' => 'Le dossier maquettes est introuvable ou non accessible en écriture.'], 500);
    }

    $names = $_FILES['files']['name'];
    if (count($names) > $maxFiles) {
        respond(['error' => "Vous pouvez envoyer jusqu’à {$maxFiles} fichiers à la fois."], 400);
    }

    $mimeExtensions = [
        'image/jpeg' => 'jpg',
        'image/png' => 'png',
        'image/webp' => 'webp',
        'image/avif' => 'avif',
        'image/heic' => 'heic',
        'image/heif' => 'heif',
        'image/heic-sequence' => 'heic',
        'image/heif-sequence' => 'heif',
    ];
    $fileInfo = new finfo(FILEINFO_MIME_TYPE);
    $pendingFiles = [];
    $results = [];

    foreach ($names as $index => $originalName) {
        $name = (string) $originalName;
        $error = (int) ($_FILES['files']['error'][$index] ?? UPLOAD_ERR_NO_FILE);
        $temporaryPath = (string) ($_FILES['files']['tmp_name'][$index] ?? '');
        $size = (int) ($_FILES['files']['size'][$index] ?? 0);

        if ($error !== UPLOAD_ERR_OK) {
            $results[] = ['name' => $name, 'success' => false, 'message' => uploadErrorMessage($error)];
            continue;
        }

        if (!is_uploaded_file($temporaryPath) || $size < 1 || $size > $maxFileSize) {
            $results[] = ['name' => $name, 'success' => false, 'message' => 'Le fichier est vide ou dépasse 20 Mo.'];
            continue;
        }

        $mimeType = $fileInfo->file($temporaryPath);
        if (!is_string($mimeType) || !isset($mimeExtensions[$mimeType])) {
            $results[] = ['name' => $name, 'success' => false, 'message' => 'Format non accepté. Utilisez une image JPG, PNG, WebP, AVIF ou HEIC.'];
            continue;
        }

        $baseName = pathinfo(basename(str_replace('\\', '/', $name)), PATHINFO_FILENAME);
        $safeName = preg_replace('/[^A-Za-z0-9_-]+/', '-', $baseName);
        $safeName = trim(substr((string) $safeName, 0, 80), '-_');
        if ($safeName === '') {
            $safeName = 'maquette';
        }

        $pendingFiles[] = [
            'name' => $name,
            'temporaryPath' => $temporaryPath,
            'safeName' => $safeName,
            'extension' => $mimeExtensions[$mimeType],
        ];
    }

    foreach ($pendingFiles as $file) {
        $suffix = 1;
        do {
            $candidateName = $file['safeName']
                . ($suffix === 1 ? '' : '-' . $suffix)
                . '.' . $file['extension'];
            $destination = $uploadDirectory . DIRECTORY_SEPARATOR . $candidateName;
            $reservation = @fopen($destination, 'x');
            $suffix++;
        } while ($reservation === false && file_exists($destination));

        if ($reservation === false) {
            $results[] = ['name' => $file['name'], 'success' => false, 'message' => 'Impossible de créer le fichier dans maquettes.'];
            continue;
        }

        fclose($reservation);
        if (move_uploaded_file($file['temporaryPath'], $destination)) {
            $results[] = ['name' => $candidateName, 'success' => true];
        } else {
            unlink($destination);
            $results[] = ['name' => $file['name'], 'success' => false, 'message' => 'Le serveur n’a pas pu enregistrer le fichier.'];
        }
    }

    $successfulUploads = count(array_filter($results, static fn (array $result): bool => $result['success']));
    $failedUploads = count($results) - $successfulUploads;
    respond([
        'uploaded' => $successfulUploads,
        'failed' => $failedUploads,
        'results' => $results,
    ], $failedUploads > 0 ? 400 : 200);
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET, POST');
    respond(['error' => 'Méthode non autorisée.'], 405);
}

header('Content-Type: text/html; charset=utf-8');
header('Cache-Control: no-store');
header('X-Content-Type-Options: nosniff');
header('Referrer-Policy: no-referrer');
?>
<!doctype html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#f4f6f2">
    <title>Ajouter une maquette</title>
    <style>
        :root {
            color-scheme: light;
            font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
            color: #17241d;
            background: #f4f6f2;
            font-synthesis: none;
            text-rendering: optimizeLegibility;
        }
        * { box-sizing: border-box; }
        body { margin: 0; min-height: 100vh; }
        main { width: min(100% - 32px, 640px); margin: 0 auto; padding: 48px 0 32px; }
        .eyebrow { margin: 0 0 10px; color: #52705c; font-size: .8rem; font-weight: 700; letter-spacing: .12em; text-transform: uppercase; }
        h1 { margin: 0; font-size: clamp(1.8rem, 8vw, 2.5rem); letter-spacing: -.04em; }
        .intro { margin: 12px 0 28px; color: #59665e; line-height: 1.6; }
        .card { padding: clamp(20px, 6vw, 32px); border: 1px solid #e0e6df; border-radius: 20px; background: #fff; box-shadow: 0 12px 36px rgb(22 46 30 / 6%); }
        .drop-zone { display: grid; min-height: 190px; place-content: center; gap: 8px; padding: 20px; border: 2px dashed #b5c5b8; border-radius: 14px; background: #fafcf9; text-align: center; cursor: pointer; transition: border-color .15s, background .15s; }
        .drop-zone:focus-within, .drop-zone.dragging { border-color: #287044; background: #f0f7f1; }
        .drop-zone svg { width: 36px; height: 36px; margin: 0 auto 4px; color: #287044; }
        .drop-title { font-weight: 700; }
        .drop-help, .limits { color: #68756c; font-size: .9rem; line-height: 1.5; }
        input[type=file] { position: absolute; width: 1px; height: 1px; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; clip-path: inset(50%); }
        input[type=file]:focus-visible + .focus-outline { outline: 3px solid #287044; outline-offset: 4px; }
        .selected { margin: 18px 0 0; padding: 0; list-style: none; }
        .selected:empty { display: none; }
        .selected li { display: flex; justify-content: space-between; gap: 12px; padding: 10px 0; border-bottom: 1px solid #edf0ec; font-size: .9rem; overflow-wrap: anywhere; }
        .file-size { flex: 0 0 auto; color: #68756c; }
        .limits { margin: 14px 0 0; }
        .token { margin-top: 20px; }
        .token label { display: block; margin-bottom: 7px; font-size: .9rem; font-weight: 600; }
        .token input { width: 100%; min-height: 48px; padding: 10px 12px; border: 1px solid #cbd4cc; border-radius: 10px; font: inherit; }
        button { width: 100%; min-height: 52px; margin-top: 22px; border: 0; border-radius: 12px; background: #24663b; color: #fff; font: inherit; font-weight: 700; cursor: pointer; }
        button:hover { background: #194f2d; }
        button:focus-visible, .token input:focus-visible { outline: 3px solid #8cc39b; outline-offset: 3px; }
        button:disabled { background: #87958a; cursor: wait; }
        .status { margin: 18px 0 0; padding: 12px 14px; border-radius: 10px; line-height: 1.5; overflow-wrap: anywhere; white-space: pre-line; }
        .status:empty { display: none; }
        .status.success { background: #eaf5ec; color: #194f2d; }
        .status.error { background: #fff0ed; color: #8c2c1d; }
        @media (max-width: 480px) {
            main { width: min(100% - 24px, 640px); padding-top: 28px; }
            .intro { margin-bottom: 20px; }
            .drop-zone { min-height: 170px; }
        }
    </style>
</head>
<body>
<main>
    <p class="eyebrow">Outil de partage</p>
    <h1>Ajouter une maquette</h1>
    <p class="intro">Envoyez des images directement dans le dossier <strong>maquettes</strong> du projet, depuis votre téléphone ou votre ordinateur.</p>

    <section class="card" aria-label="Envoi de fichiers">
        <form id="upload-form">
            <label class="drop-zone" id="drop-zone" for="files">
                <svg aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 16V4m0 0L7 9m5-5 5 5"></path>
                    <path d="M20 16.5v2A1.5 1.5 0 0 1 18.5 20h-13A1.5 1.5 0 0 1 4 18.5v-2"></path>
                </svg>
                <span class="drop-title">Choisir des images</span>
                <span class="drop-help">Touchez pour parcourir ou prendre une photo</span>
                <input id="files" name="files[]" type="file" accept="image/*" multiple required>
                <span class="focus-outline" aria-hidden="true"></span>
            </label>
            <ul class="selected" id="selected-files" aria-label="Fichiers sélectionnés"></ul>
            <p class="limits">JPG, PNG, WebP, AVIF ou HEIC · 20 Mo maximum par image · 10 images par envoi</p>
            <?php if ($uploadToken !== ''): ?>
                <div class="token">
                    <label for="access-token">Code d’accès</label>
                    <input id="access-token" name="token" type="password" autocomplete="current-password" required>
                </div>
            <?php endif; ?>
            <button id="submit-button" type="submit">Envoyer les images</button>
            <p id="status" class="status" role="status" aria-live="polite"></p>
        </form>
    </section>
</main>
<script>
    const form = document.querySelector('#upload-form');
    const fileInput = document.querySelector('#files');
    const fileList = document.querySelector('#selected-files');
    const dropZone = document.querySelector('#drop-zone');
    const submitButton = document.querySelector('#submit-button');
    const status = document.querySelector('#status');
    const maxFiles = <?= $maxFiles ?>;
    const maxFileSize = <?= $maxFileSize ?>;

    function formatSize(bytes) {
        return bytes < 1024 * 1024
            ? `${Math.max(1, Math.round(bytes / 1024))} Ko`
            : `${(bytes / (1024 * 1024)).toFixed(1)} Mo`;
    }

    function renderFiles(files) {
        fileList.replaceChildren();
        [...files].forEach((file) => {
            const item = document.createElement('li');
            const name = document.createElement('span');
            const size = document.createElement('span');
            name.textContent = file.name;
            size.className = 'file-size';
            size.textContent = formatSize(file.size);
            item.append(name, size);
            fileList.append(item);
        });
    }

    fileInput.addEventListener('change', () => {
        renderFiles(fileInput.files);
        status.textContent = '';
        status.className = 'status';
    });

    ['dragenter', 'dragover'].forEach((eventName) => {
        dropZone.addEventListener(eventName, (event) => {
            event.preventDefault();
            dropZone.classList.add('dragging');
        });
    });
    ['dragleave', 'drop'].forEach((eventName) => {
        dropZone.addEventListener(eventName, (event) => {
            event.preventDefault();
            dropZone.classList.remove('dragging');
        });
    });
    dropZone.addEventListener('drop', (event) => {
        fileInput.files = event.dataTransfer.files;
        renderFiles(fileInput.files);
    });

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const files = [...fileInput.files];
        status.className = 'status';

        if (files.length === 0 || files.length > maxFiles) {
            status.textContent = `Choisissez entre 1 et ${maxFiles} images.`;
            status.classList.add('error');
            return;
        }
        if (files.some((file) => file.size > maxFileSize)) {
            status.textContent = 'Chaque image doit faire 20 Mo maximum.';
            status.classList.add('error');
            return;
        }

        const data = new FormData();
        files.forEach((file) => data.append('files[]', file));
        const token = document.querySelector('#access-token');
        submitButton.disabled = true;
        submitButton.textContent = 'Envoi en cours…';

        try {
            const response = await fetch(window.location.href, {
                method: 'POST',
                headers: token ? { 'X-Upload-Token': token.value } : {},
                body: data,
            });
            const result = await response.json();
            const details = (result.results || [])
                .map((file) => file.success ? `✓ ${file.name}` : `✗ ${file.name} : ${file.message}`)
                .join('\n');

            if (response.ok) {
                status.textContent = `${result.uploaded} image(s) envoyée(s) dans maquettes.\n${details}`;
                status.classList.add('success');
                form.reset();
                renderFiles([]);
            } else {
                status.textContent = [result.error, details].filter(Boolean).join('\n');
                status.classList.add('error');
            }
        } catch {
            status.textContent = 'Impossible de joindre le serveur. Vérifiez votre connexion au réseau local.';
            status.classList.add('error');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Envoyer les images';
        }
    });
</script>
</body>
</html>
