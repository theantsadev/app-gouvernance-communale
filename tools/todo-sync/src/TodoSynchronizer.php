<?php

declare(strict_types=1);

namespace TodoSync;

use PhpOffice\PhpSpreadsheet\IOFactory;
use RuntimeException;

final class TodoSynchronizer
{
    private const SHEET_NAME = 'liste des taches';
    private const HEADERS = [
        'ID',
        'Catégorie',
        'Module',
        'Page',
        'Tâche détaillée',
        'Type',
        'Qui',
        'Estimation',
        'Temps passé',
        'Reste à faire',
        'Avancement',
    ];

    public function __construct(
        private readonly string $xlsxPath,
        private readonly string $todoPath,
    ) {
    }

    public function synchronize(): int
    {
        if (!is_file($this->xlsxPath)) {
            throw new RuntimeException("Le fichier Excel est introuvable : {$this->xlsxPath}");
        }

        $spreadsheet = IOFactory::load($this->xlsxPath);
        $worksheet = $spreadsheet->getSheetByName(self::SHEET_NAME);
        if ($worksheet === null) {
            throw new RuntimeException("La feuille « " . self::SHEET_NAME . " » est introuvable.");
        }

        $rows = $this->readRows($worksheet->toArray(null, true, true, true));
        if ($rows === []) {
            throw new RuntimeException("La feuille « " . self::SHEET_NAME . " » ne contient aucune tâche.");
        }

        $markdown = $this->renderMarkdown($rows);
        $this->writeAtomically($markdown);

        return count($rows);
    }

    private function readRows(array $sheetRows): array
    {
        $headerRow = array_shift($sheetRows);
        if (!is_array($headerRow)) {
            throw new RuntimeException('La ligne d’en-tête Excel est invalide.');
        }

        $columns = [];
        foreach ($headerRow as $column => $header) {
            $key = $this->normalizeHeader((string) $header);
            if ($key !== '') {
                $columns[$key] = $column;
            }
        }

        $requiredColumns = [
            'ligne',
            'categorie',
            'module',
            'page',
            'taches',
            'type',
            'qui',
            'estimation',
            'temps passe',
            'reste a faire',
            'avancement',
        ];
        foreach ($requiredColumns as $column) {
            if (!isset($columns[$column])) {
                throw new RuntimeException("La colonne Excel requise est absente : {$column}");
            }
        }

        $rows = [];
        foreach ($sheetRows as $sheetRow) {
            $id = $this->cell($sheetRow, $columns['ligne']);
            if ($id === '') {
                continue;
            }

            $rows[] = [
                $id,
                $this->cell($sheetRow, $columns['categorie']),
                $this->cell($sheetRow, $columns['module']),
                $this->cell($sheetRow, $columns['page']),
                $this->cell($sheetRow, $columns['taches']),
                $this->cell($sheetRow, $columns['type']),
                $this->cell($sheetRow, $columns['qui']),
                $this->cell($sheetRow, $columns['estimation']),
                $this->cell($sheetRow, $columns['temps passe']),
                $this->cell($sheetRow, $columns['reste a faire']),
                $this->formatProgress($this->cell($sheetRow, $columns['avancement'])),
            ];
        }

        return $rows;
    }

    private function renderMarkdown(array $rows): string
    {
        $lines = [
            '| ' . implode(' | ', self::HEADERS) . ' |',
            '|---:|---|---|---|---|---|---|---:|---:|---:|---:|',
        ];

        foreach ($rows as $row) {
            $escapedRow = array_map(
                fn (string $cell): string => $this->escapeMarkdownCell($cell),
                $row
            );
            $lines[] = '| ' . implode(' | ', $escapedRow) . ' |';
        }

        return implode(PHP_EOL, $lines) . PHP_EOL;
    }

    private function writeAtomically(string $content): void
    {
        $directory = dirname($this->todoPath);
        $temporaryPath = tempnam($directory, 'todo-sync-');
        if ($temporaryPath === false) {
            throw new RuntimeException("Impossible de créer un fichier temporaire dans {$directory}");
        }

        try {
            if (file_put_contents($temporaryPath, $content, LOCK_EX) === false) {
                throw new RuntimeException("Impossible d’écrire le fichier temporaire {$temporaryPath}");
            }
            if (!rename($temporaryPath, $this->todoPath)) {
                throw new RuntimeException("Impossible de remplacer {$this->todoPath}");
            }
        } finally {
            if (is_file($temporaryPath)) {
                unlink($temporaryPath);
            }
        }
    }

    private function cell(array $row, string $column): string
    {
        return trim((string) ($row[$column] ?? ''));
    }

    private function formatProgress(string $value): string
    {
        if ($value === '') {
            return '';
        }

        if (str_ends_with($value, '%')) {
            return str_replace('.', ',', $value);
        }

        if (is_numeric($value)) {
            $number = (float) $value;
            if ($number >= 0 && $number <= 1) {
                return number_format($number * 100, 2, ',', '') . '%';
            }
        }

        return $value;
    }

    private function normalizeHeader(string $header): string
    {
        $normalized = mb_strtolower(trim($header));
        $normalized = strtr($normalized, [
            'é' => 'e',
            'è' => 'e',
            'ê' => 'e',
            'ë' => 'e',
            'à' => 'a',
            'â' => 'a',
            'ä' => 'a',
            'î' => 'i',
            'ï' => 'i',
            'ô' => 'o',
            'ö' => 'o',
            'ù' => 'u',
            'û' => 'u',
            'ü' => 'u',
            'ç' => 'c',
        ]);
        return preg_replace('/\s+/', ' ', $normalized) ?? $normalized;
    }

    private function escapeMarkdownCell(string $value): string
    {
        return str_replace(["|", "\r", "\n"], ["\\|", '', ' '], $value);
    }
}
