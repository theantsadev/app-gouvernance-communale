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

    public function synchronize(int $sens = 1): int
    {
        if ($sens !== 1 && $sens !== -1) {
            throw new \InvalidArgumentException('Le sens de synchronisation doit être 1 ou -1.');
        }

        return $sens === 1
            ? $this->synchronizeExcelToMarkdown()
            : $this->synchronizeMarkdownToExcel();
    }

    private function synchronizeExcelToMarkdown(): int
    {
        $worksheet = $this->loadWorksheet();

        $rows = $this->readRows($worksheet->toArray(null, true, true, true));
        if ($rows === []) {
            throw new RuntimeException("La feuille « " . self::SHEET_NAME . " » ne contient aucune tâche.");
        }

        $markdown = $this->renderMarkdown($rows);
        $this->writeAtomically($markdown);

        return count($rows);
    }

    private function synchronizeMarkdownToExcel(): int
    {
        if (!is_file($this->todoPath)) {
            throw new RuntimeException("Le fichier Markdown est introuvable : {$this->todoPath}");
        }

        $markdown = file_get_contents($this->todoPath);
        if ($markdown === false) {
            throw new RuntimeException("Impossible de lire le fichier Markdown : {$this->todoPath}");
        }

        $rows = $this->readMarkdownRows($markdown);
        if ($rows === []) {
            throw new RuntimeException('Le fichier Markdown ne contient aucune tâche.');
        }

        $worksheet = $this->loadWorksheet();
        $sheetRows = $worksheet->toArray(null, false, true, true);
        $headerRow = array_shift($sheetRows);
        if (!is_array($headerRow)) {
            throw new RuntimeException('La ligne d’en-tête Excel est invalide.');
        }

        $columns = $this->findColumns($headerRow, [
            'id' => ['id', 'ligne'],
            'categorie' => ['categorie'],
            'module' => ['module'],
            'page' => ['page'],
            'taches' => ['taches', 'tache detaillee'],
            'type' => ['type'],
            'qui' => ['qui'],
            'estimation' => ['estimation'],
            'temps passe' => ['temps passe'],
            'reste a faire' => ['reste a faire'],
            'avancement' => ['avancement'],
        ]);

        $firstDataRow = 2;
        foreach ($rows as $offset => $row) {
            $excelRow = $firstDataRow + $offset;
            foreach ($columns as $key => $column) {
                if ($key === 'avancement') {
                    continue;
                }

                $worksheet->setCellValue(
                    $column . $excelRow,
                    $this->formatExcelValue($row[$key], $key)
                );
            }

            $tempsPasseColumn = $columns['temps passe'];
            $resteAFaireColumn = $columns['reste a faire'];
            $avancementColumn = $columns['avancement'];
            $worksheet->setCellValue(
                $avancementColumn . $excelRow,
                '=(' . $tempsPasseColumn . $excelRow . '/('
                . $tempsPasseColumn . $excelRow . '+'
                . $resteAFaireColumn . $excelRow . '))'
            );
        }

        $lastDataRow = $firstDataRow + count($rows) - 1;
        $worksheet->getStyle($columns['avancement'] . $firstDataRow . ':' . $columns['avancement'] . $lastDataRow)
            ->getNumberFormat()
            ->setFormatCode('0.00%');
        $worksheet->getStyle($columns['page'] . $firstDataRow . ':' . $columns['page'] . $lastDataRow)
            ->getFont()
            ->getColor()
            ->setARGB('FF0000FF');

        $highestRow = $worksheet->getHighestRow();
        $extraRows = $highestRow - ($firstDataRow + count($rows) - 1);
        if ($extraRows > 0) {
            $worksheet->removeRow($firstDataRow + count($rows), $extraRows);
        }

        $spreadsheet = $worksheet->getParent();
        if ($spreadsheet === null) {
            throw new RuntimeException('Impossible d’enregistrer le classeur Excel.');
        }
        $writer = IOFactory::createWriter($spreadsheet, 'Xlsx');
        $writer->setPreCalculateFormulas(false);
        $writer->save($this->xlsxPath);

        return count($rows);
    }

    private function loadWorksheet()
    {
        if (!is_file($this->xlsxPath)) {
            throw new RuntimeException("Le fichier Excel est introuvable : {$this->xlsxPath}");
        }

        $spreadsheet = IOFactory::load($this->xlsxPath);
        $worksheet = $spreadsheet->getSheetByName(self::SHEET_NAME);
        if ($worksheet === null) {
            $worksheet = $spreadsheet->createSheet();
            $worksheet->setTitle(self::SHEET_NAME);
            $worksheet->fromArray([self::HEADERS], null, 'A1');
        }

        return $worksheet;
    }

    private function readRows(array $sheetRows): array
    {
        $headerRow = array_shift($sheetRows);
        if (!is_array($headerRow)) {
            throw new RuntimeException('La ligne d’en-tête Excel est invalide.');
        }

        $columns = $this->findColumns($headerRow, [
            'ligne' => ['ligne', 'id'],
            'categorie' => ['categorie'],
            'module' => ['module'],
            'page' => ['page'],
            'taches' => ['taches', 'tache detaillee'],
            'type' => ['type'],
            'qui' => ['qui'],
            'estimation' => ['estimation'],
            'temps passe' => ['temps passe'],
            'reste a faire' => ['reste a faire'],
            'avancement' => ['avancement'],
        ]);

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

    private function readMarkdownRows(string $markdown): array
    {
        $lines = preg_split('/\R/', trim($markdown)) ?: [];
        $tableRows = [];
        foreach ($lines as $line) {
            if (trim($line) !== '' && str_starts_with(trim($line), '|')) {
                $tableRows[] = $this->parseMarkdownRow(trim($line));
            }
        }

        if (count($tableRows) < 2) {
            throw new RuntimeException('Le tableau Markdown est invalide ou incomplet.');
        }

        $header = array_shift($tableRows);
        array_shift($tableRows);
        $columns = [];
        foreach ($header as $index => $value) {
            $key = $this->normalizeHeader($value);
            if ($key !== '') {
                if ($key === 'tache detaillee') {
                    $key = 'taches';
                }
                $columns[$key] = $index;
            }
        }

        $requiredColumns = ['id', 'categorie', 'module', 'page', 'taches', 'type', 'qui', 'estimation', 'temps passe', 'reste a faire', 'avancement'];
        foreach ($requiredColumns as $column) {
            if (!isset($columns[$column])) {
                throw new RuntimeException("La colonne Markdown requise est absente : {$column}");
            }
        }

        $rows = [];
        foreach ($tableRows as $tableRow) {
            if (count($tableRow) === 1 && $tableRow[0] === '') {
                continue;
            }
            if ($this->isMarkdownSeparator($tableRow)) {
                continue;
            }
            if (count($tableRow) !== count($header)) {
                throw new RuntimeException('Une ligne du tableau Markdown contient un nombre de colonnes invalide.');
            }

            $row = [];
            foreach ($requiredColumns as $column) {
                $row[$column] = trim($tableRow[$columns[$column]]);
            }
            if ($row['id'] !== '') {
                $rows[] = $row;
            }
        }

        return $rows;
    }

    private function isMarkdownSeparator(array $row): bool
    {
        return $row !== [] && array_reduce(
            $row,
            fn (bool $valid, string $cell): bool => $valid && preg_match('/^:?-+:?$/', trim($cell)) === 1,
            true
        );
    }

    private function parseMarkdownRow(string $line): array
    {
        $line = trim($line);
        if (str_ends_with($line, '|')) {
            $line = substr($line, 0, -1);
        }
        $line = ltrim($line, '|');

        $cells = [];
        $cell = '';
        $escaped = false;
        for ($index = 0, $length = strlen($line); $index < $length; $index++) {
            $character = $line[$index];
            if ($escaped) {
                $cell .= $character;
                $escaped = false;
            } elseif ($character === '\\') {
                $escaped = true;
            } elseif ($character === '|') {
                $cells[] = trim($cell);
                $cell = '';
            } else {
                $cell .= $character;
            }
        }
        if ($escaped) {
            $cell .= '\\';
        }
        $cells[] = trim($cell);

        return $cells;
    }

    private function findColumns(array $headerRow, array $aliases): array
    {
        $available = [];
        foreach ($headerRow as $column => $header) {
            $key = $this->normalizeHeader((string) $header);
            if ($key !== '') {
                $available[$key] = $column;
            }
        }

        $columns = [];
        foreach ($aliases as $key => $possibleNames) {
            foreach ($possibleNames as $possibleName) {
                if (isset($available[$possibleName])) {
                    $columns[$key] = $available[$possibleName];
                    break;
                }
            }
            if (!isset($columns[$key])) {
                throw new RuntimeException("La colonne Excel requise est absente : {$key}");
            }
        }

        return $columns;
    }

    private function formatExcelValue(string $value, string $column): string|int|float
    {
        if ($column === 'avancement' && str_ends_with($value, '%')) {
            $percentage = str_replace(',', '.', rtrim($value, '%'));
            if (is_numeric($percentage)) {
                return (float) $percentage / 100;
            }
        }

        if (in_array($column, ['estimation', 'temps passe', 'reste a faire'], true)) {
            $number = str_replace(',', '.', $value);
            if (is_numeric($number)) {
                return (float) $number;
            }
        }

        return $value;
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
