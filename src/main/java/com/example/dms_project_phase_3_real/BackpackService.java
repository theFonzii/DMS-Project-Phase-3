package com.example.dms_project_phase_3_real;

import java.util.List;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * BackpackService.java
 * This class contains the logic for the custom action.
 * Given selected itemIds + quantities,
 * compute total weight and value, and flag encumbrance.
 */

public class BackpackService {

    private final ItemRepository repo;
    private final double encumbranceThresholdKg;

    public BackpackService(ItemRepository repo, double encumbranceThresholdKg) {
        this.repo = repo;
        this.encumbranceThresholdKg = encumbranceThresholdKg;
    }

    /**
     * method: summarize
     * parameters: List
     * return: BackpackSummary
     * purpose: Calculates totals. Throws ValidationException if any itemId is unknown
     */
    public BackpackSummary summarize(List<ItemQty> picks) throws ValidationException {
        double totalWeight = 0.0;
        int totalValue = 0;

        for (ItemQty pick : picks) {
            var item = repo.findById(pick.itemId())
                    .orElseThrow(() -> new ValidationException("Unknown itemId: " + pick.itemId()));
            totalWeight += item.getWeightKg() * pick.qty();
            totalValue  += item.getGePrice() * pick.qty();
        }

        totalWeight = Math.round(totalWeight * 100.0) / 100.0;
        boolean encumbered = totalWeight > encumbranceThresholdKg;
        return new BackpackSummary(totalWeight, totalValue, encumbered);
    }
}
