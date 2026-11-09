#!/usr/bin/env php
<?php

declare(strict_types=1);

require dirname(__DIR__) . '/vendor/autoload.php';

use TodoSync\TodoSynchronizer;

$projectRoot = dirname(__DIR__, 3);
$xlsxPath = getenv('TODO_XLSX_PATH') ?: dirname($projectRoot) . DIRECTORY_SEPARATOR . 'Todo-stage-3327.xlsx';
$todoPath = getenv('TODO_MD_PATH') ?: $projectRoot . DIRECTORY_SEPARATOR . 'todo.md';
$lastModified = 0;

fwrite(STDOUT, "Surveillance de {$xlsxPath}" . PHP_EOL);

while (true) {
    clearstatcache(true, $xlsxPath);
    if (!is_file($xlsxPath)) {
        fwrite(STDERR, date('c') . " - Fichier Excel introuvable : {$xlsxPath}" . PHP_EOL);
        sleep(5);
        continue;
    }

    $currentModified = filemtime($xlsxPath);

    if ($currentModified !== $lastModified) {
        try {
            $count = (new TodoSynchronizer($xlsxPath, $todoPath))->synchronize();
            fwrite(STDOUT, date('c') . " - {$count} tâches synchronisées." . PHP_EOL);
            $lastModified = $currentModified;
        } catch (Throwable $exception) {
            fwrite(STDERR, date('c') . " - Erreur : {$exception->getMessage()}" . PHP_EOL);
        }
    }

    sleep(5);
}
