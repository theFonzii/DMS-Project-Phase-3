package com.example.dms_project_phase_3_real;

import java.util.Objects;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * RunescapeItem.java
 * This class contains the constructors and mutators for
 * the RunescapeItem object, which will be the focal point
 * of the planned database. It includes all 6 attributes.
 */

public class RunescapeItem {
    private int itemId;
    private String name;
    private ItemType type;
    private int requiredLevel;
    private double weightKg;
    private int gePrice;

    public RunescapeItem(int itemId,
                         String name,
                         ItemType type,
                         int requiredLevel,
                         double weightKg,
                         int gePrice) throws ValidationException {
        setItemId(itemId);
        setName(name);
        setType(type);
        setRequiredLevel(requiredLevel);
        setWeightKg(weightKg);
        setGePrice(gePrice);
    }

    // ----- setters -----

    public void setItemId(int itemId) throws ValidationException {
        if (itemId <= 0) throw new ValidationException("itemId must be > 0.");
        this.itemId = itemId;
    }

    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("name cannot be blank.");
        this.name = name.trim();
    }

    public void setType(ItemType type) throws ValidationException {
        if (type == null) throw new ValidationException("type cannot be null.");
        this.type = type;
    }

    public void setRequiredLevel(int requiredLevel) throws ValidationException {
        if (requiredLevel < 0)
            throw new ValidationException("requiredLevel must be >= 0.");
        this.requiredLevel = requiredLevel;
    }

    public void setWeightKg(double weightKg) throws ValidationException {
        if (weightKg < 0)
            throw new ValidationException("weightKg must be >= 0.");
        this.weightKg = round2(weightKg);
    }

    public void setGePrice(int gePrice) throws ValidationException {
        if (gePrice < 0)
            throw new ValidationException("gePrice must be >= 0.");
        this.gePrice = gePrice;
    }

    // ----- Getters -----

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public ItemType getType() { return type; }
    public int getRequiredLevel() { return requiredLevel; }
    public double getWeightKg() { return weightKg; }
    public int getGePrice() { return gePrice; }

    // ----- Helpers -----

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }


    /**
     * method: toRow
     * parameters: none
     * return: String
     * purpose: returns a compact row for CLI tables
     */
    public String toRow() {
        return String.format("%-6d | %-18s | %-8s | %3d | %6.2f kg | %7d gp",
                itemId, name, type, requiredLevel, weightKg, gePrice);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunescapeItem)) return false;
        RunescapeItem that = (RunescapeItem) o;
        return itemId == that.itemId;
    }
    @Override public int hashCode() { return Objects.hash(itemId); }
}
