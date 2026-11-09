<?php

declare(strict_types=1);

require dirname(__DIR__) . '/vendor/autoload.php';

use TodoSync\TodoSynchronizer;

$projectRoot = dirname(__DIR__, 3);
$xlsxPath = getenv('TODO_XLSX_PATH') ?: dirname($projectRoot) . DIRECTORY_SEPARATOR . 'Todo-stage-3327.xlsx';
$todoPath = getenv('TODO_MD_PATH') ?: $projectRoot . DIRECTORY_SEPARATOR . 'todo.md';
$syncToken = getenv('TODO_SYNC_TOKEN') ?: '';

header('Content-Type: application/json; charset=utf-8');

if ($syncToken !== '' && !hash_equals($syncToken, (string) ($_SERVER['HTTP_AUTHORIZATION'] ?? ''))) {
    http_response_code(401);
    echo json_encode(['error' => 'Authentification requise.'], JSON_UNESCAPED_UNICODE);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST' && $_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    header('Allow: GET, POST');
    echo json_encode(['error' => 'Méthode non autorisée.'], JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    $count = (new TodoSynchronizer($xlsxPath, $todoPath))->synchronize();
    echo json_encode([
        'message' => 'Synchronisation terminée.',
        'tasks' => $count,
        'todo' => $todoPath,
    ], JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
} catch (Throwable $exception) {
    http_response_code(500);
    echo json_encode(['error' => $exception->getMessage()], JSON_UNESCAPED_UNICODE);
}
