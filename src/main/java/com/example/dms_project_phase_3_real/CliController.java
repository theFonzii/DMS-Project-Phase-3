package com.example.dms_project_phase_3_real;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * CliController.java
 * This class creates the menu-driven CLI controller for Phase 1.
 * All action methods return strings (or objects) in the interest of avoiding 'void'.
 * The start() loop returns an exit code (0 = normal).
 */

public class CliController {

    private final Scanner in;
    private final ItemRepository repo;
    private final BackpackService backpack;
    private final TextFileAdapter fileAdapter;

    public CliController(Scanner in, ItemRepository repo, BackpackService backpack, TextFileAdapter fileAdapter) {
        this.in = in;
        this.repo = repo;
        this.backpack = backpack;
        this.fileAdapter = fileAdapter;
    }


    /**
     * method: start
     * parameters: none
     * return: int
     * purpose: Main interaction loop for CLI. Returns 0 when user chooses Exit.
     */
    public int start() {
        while (true) {
            System.out.println("""
                    
                    ================== Runescape Item DMS (Phase 1) ==================
                    [1] Add Item        [2] View Items       [3] Update Item
                    [4] Delete Item     [5] Load From CSV    [6] Backpack Summary
                    [0] Exit
                    -------------------------------------------------------------------
                    """);
            System.out.print("Choose an option: ");
            String choice = in.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> System.out.println(addFlow());
                    case "2" -> System.out.println(viewFlow());
                    case "3" -> System.out.println(updateFlow());
                    case "4" -> System.out.println(deleteFlow());
                    case "5" -> System.out.println(loadCsvFlow());
                    case "6" -> System.out.println(backpackFlow());
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return 0;
                    }
                    default -> System.out.println("Unknown option. Try again.");
                }
            } catch (ValidationException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
    }

    // -------------------- Menu Flows  ----------------------

    /**
     * method: addFlow
     * parameters: none
     * return: String
     * purpose: Workflow for adding object
     */
    private String addFlow() throws ValidationException {
        System.out.println("Add Item - please enter fields.");
        int id = askInt("itemId (>0): ");
        String name = askNonEmpty("name: ");
        ItemType type = ItemType.fromString(askNonEmpty("type (WEAPON/ARMOR/FOOD/POTION/RESOURCE/OTHER): "));
        int level = askIntMin("requiredLevel (>=0): ", 0);
        double weight = askDoubleMin("weightKg (>=0): ", 0.0);
        int price = askIntMin("gePrice (>=0): ", 0);

        RunescapeItem item = new RunescapeItem(id, name, type, level, weight, price);
        repo.add(item);
        return "Added: " + item.toRow();
    }

    /**
     * method: viewFLow
     * parameters: none
     * return: String
     * purpose: Workflow for viewing object
     */
    private String viewFlow() {
        List<RunescapeItem> list = repo.findAll();
        if (list.isEmpty()) return "No items stored.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s | %-18s | %-8s | %3s | %8s | %9s%n",
                "ID", "Name", "Type", "Lvl", "Weight", "GE Price"));
        sb.append("----------------------------------------------------------------------\n");
        list.forEach(i -> sb.append(i.toRow()).append('\n'));
        return sb.toString();
    }

    /**
     * method: updateFLow
     * parameters: none
     * return: String
     * purpose: Workflow for updating object
     */
    private String updateFlow() throws ValidationException {
        int id = askInt("Enter itemId to update: ");
        RunescapeItem updated = repo.update(id, item -> {
            System.out.println("Leave field blank to keep current value.");
            String name = askOptional("name [" + item.getName() + "]: ");
            if (!name.isBlank()) item.setName(name);

            String typeRaw = askOptional("type [" + item.getType() + "]: ");
            if (!typeRaw.isBlank()) item.setType(ItemType.fromString(typeRaw));

            String lvlRaw = askOptional("requiredLevel [" + item.getRequiredLevel() + "]: ");
            if (!lvlRaw.isBlank()) item.setRequiredLevel(parseIntMin(lvlRaw, 0, "requiredLevel"));

            String wRaw = askOptional("weightKg [" + item.getWeightKg() + "]: ");
            if (!wRaw.isBlank()) item.setWeightKg(parseDoubleMin(wRaw, 0.0, "weightKg"));

            String pRaw = askOptional("gePrice [" + item.getGePrice() + "]: ");
            if (!pRaw.isBlank()) item.setGePrice(parseIntMin(pRaw, 0, "gePrice"));
        });
        return "Updated: " + updated.toRow();
    }

    /**
     * method: deleteFLow
     * parameters: none
     * return: String
     * purpose: Workflow for deleting object
     */
    private String deleteFlow() throws ValidationException {
        int id = askInt("Enter itemId to delete: ");
        RunescapeItem removed = repo.delete(id);
        return "Deleted: " + removed.toRow();
    }

    /**
     * method: loadCsvFlow
     * parameters: none
     * return: String
     * purpose: Workflow for loading CSV file
     */
    private String loadCsvFlow() {
        System.out.print("Enter CSV path: ");
        String path = in.nextLine();
        TextFileAdapter.LoadResult r = fileAdapter.loadCsv(path);
        int added = 0;
        for (RunescapeItem it : r.items()) {
            try {
                repo.add(it);
                added++;
            } catch (ValidationException ex) {
                r.errors().add("Duplicate itemId " + it.getItemId() + ": " + ex.getMessage());
            }
        }
        StringBuilder sb = new StringBuilder("Loaded ").append(added).append(" item(s).\n");
        if (!r.errors().isEmpty()) {
            sb.append("Errors:\n");
            r.errors().forEach(e -> sb.append(" - ").append(e).append('\n'));
        }
        return sb.toString();
    }

    /**
     * method: backpackFlow
     * parameters: none
     * return: String
     * purpose: Workflow for custom backpack calculation action
     */
    private String backpackFlow() throws ValidationException {
        System.out.println("Enter itemId:quantity pairs (example: 1001:2,1002:3).");
        String raw = askNonEmpty("Selection: ");
        List<ItemQty> picks = new ArrayList<>();
        for (String token : raw.split(",")) {
            String[] pair = token.trim().split(":");
            if (pair.length != 2) throw new ValidationException("Bad pair: " + token);
            int id = parseIntMin(pair[0], 1, "itemId");
            int qty = parseIntMin(pair[1], 1, "qty");
            picks.add(new ItemQty(id, qty));
        }
        BackpackSummary summary = backpack.summarize(picks);
        return summary.toDisplayString();
    }

    // ------------------------ Prompters ---------------------------

    private String askNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine();
            if (!s.trim().isEmpty()) return s.trim();
            System.out.println("Value cannot be blank.");
        }
    }

    private String askOptional(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private int askInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine();
            try { return Integer.parseInt(s.trim()); }
            catch (Exception ex) { System.out.println("Please enter an integer."); }
        }
    }

    private int askIntMin(String prompt, int min) {
        while (true) {
            int v = askInt(prompt);
            if (v >= min) return v;
            System.out.println("Value must be >= " + min + ".");
        }
    }

    private double askDoubleMin(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine();
            try {
                double v = Double.parseDouble(s.trim());
                if (v >= min) return v;
                System.out.println("Value must be >= " + min + ".");
            } catch (Exception ex) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private int parseIntMin(String raw, int min, String fieldName) throws ValidationException {
        try {
            int v = Integer.parseInt(raw.trim());
            if (v < min) throw new NumberFormatException();
            return v;
        } catch (Exception ex) {
            throw new ValidationException(fieldName + " must be an integer >= " + min + ".");
        }
    }

    private double parseDoubleMin(String raw, double min, String fieldName) throws ValidationException {
        try {
            double v = Double.parseDouble(raw.trim());
            if (v < min) throw new NumberFormatException();
            return v;
        } catch (Exception ex) {
            throw new ValidationException(fieldName + " must be a number >= " + min + ".");
        }
    }
}
