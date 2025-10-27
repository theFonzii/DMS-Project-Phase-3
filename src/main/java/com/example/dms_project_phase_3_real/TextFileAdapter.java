package com.example.dms_project_phase_3_real;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * TextFileAdapter.java
 * This class creates a minimal CSV reader for Phase 1 batch creation.
 * Expected header:
 * itemId,name,type,requiredLevel,weightKg,gePrice
 * Lines with errors are skipped, and errors are collected for display.
 */

public class TextFileAdapter {

    public LoadResult loadCsv(String path) {
        List<RunescapeItem> items = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = br.readLine();
            if (header == null || !header.trim().toLowerCase()
                    .startsWith("itemid,name,type,requiredlevel,weightkg,geprice")) {
                errors.add("Invalid or missing header row.");
                return new LoadResult(items, errors);
            }

            String line;
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                String[] parts = splitCsv(line);
                if (parts.length != 6) {
                    errors.add("Line " + lineNo + ": wrong number of columns.");
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    ItemType type = ItemType.fromString(parts[2]);
                    int lvl = Integer.parseInt(parts[3].trim());
                    double w = Double.parseDouble(parts[4].trim());
                    int price = Integer.parseInt(parts[5].trim());

                    RunescapeItem item = new RunescapeItem(id, name, type, lvl, w, price);
                    items.add(item);
                } catch (Exception ex) {
                    errors.add("Line " + lineNo + ": " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            errors.add("Failed to read file: " + ex.getMessage());
        }

        return new LoadResult(items, errors);
    }

    /**
     * method: splitCsv
     * parameters: String
     * return: Array
     * purpose: CSV splitter that handles quoted names with commas.
     * Temporary solution for phase 1
     */
    private String[] splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                out.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(c);
            }
        }
        out.add(cell.toString());
        return out.toArray(new String[0]);
    }

    /** Bundle of loaded items + any row errors to show in CLI. */
    public record LoadResult(List<RunescapeItem> items, List<String> errors) {}
}
