package com.example.dms_project_phase_3_real;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * ItemType.java
 * This enumerator encapsulates allowed item categories.
 * It is primarily used for validation and avoiding free-form strings.
 */

public enum ItemType {
    WEAPON, ARMOR, FOOD, POTION, RESOURCE, OTHER;

    /**
     * method: fromString
     * parameters: String
     * return: String
     * purpose: Parser with case-insensitive functionality and fallback category (OTHER).
     */
    public static ItemType fromString(String raw) {
        if (raw == null) return OTHER;
        try {
            return ItemType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return OTHER;
        }
    }
}