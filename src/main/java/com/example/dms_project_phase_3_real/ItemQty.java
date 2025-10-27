package com.example.dms_project_phase_3_real;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * ItemQty.java
 * This class creates a small carrier for {itemId, quantity}.
 * It throws IllegalArgumentException for invalid values.
 */

public record ItemQty(int itemId, int qty) {
    public ItemQty {
        if (itemId <= 0) throw new IllegalArgumentException("itemId must be > 0.");
        if (qty <= 0)    throw new IllegalArgumentException("qty must be > 0.");
    }
}