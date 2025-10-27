package com.example.dms_project_phase_3_real;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * BackpackSummary.java
 * This class creates output for the custom action.
 * It provides a user-friendly summary string for the CLI.
 */

public record BackpackSummary(double totalWeightKg, int totalValueGp, boolean encumbered) {
    public String toDisplayString() {
        String warn = encumbered ? " - Encumbered!" : "";
        return String.format("Backpack: %.2f kg, %d gp%s", totalWeightKg, totalValueGp, warn);
    }
}