#!/usr/bin/env php
<?php

declare(strict_types=1);

require dirname(__DIR__) . '/vendor/autoload.php';

use TodoSync\TodoSynchronizer;

$projectRoot = dirname(__DIR__, 3);
$xlsxPath = getenv('TODO_XLSX_PATH') ?: dirname($projectRoot) . DIRECTORY_SEPARATOR . 'Todo-stage-3327.xlsx';
$todoPath = getenv('TODO_MD_PATH') ?: $projectRoot . DIRECTORY_SEPARATOR . 'todo.md';

try {
    $count = (new TodoSynchronizer($xlsxPath, $todoPath))->synchronize();
    fwrite(STDOUT, "Synchronisation terminée : {$count} tâches écrites dans {$todoPath}" . PHP_EOL);
} catch (Throwable $exception) {
    fwrite(STDERR, "Erreur de synchronisation : {$exception->getMessage()}" . PHP_EOL);
    exit(1);
}
