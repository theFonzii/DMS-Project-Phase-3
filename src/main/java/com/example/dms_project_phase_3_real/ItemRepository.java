package com.example.dms_project_phase_3_real;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * ItemRepository.java
 * This class creates an in-memory repository for testing in Phase 1.
 * It holds items in a list, and provides CRUD functionality with no void return values.
 */

public class ItemRepository {

    private final List<RunescapeItem> items = new ArrayList<>();

    /**
     * method: add
     * parameters: RunescapeItem
     * return: RunescapeItem
     * purpose: Create (Add). Rejects duplicate itemId. Returns added item snapshot
     */
    public RunescapeItem add(RunescapeItem item) throws ValidationException {
        if (findById(item.getItemId()).isPresent()) {
            throw new ValidationException("An item with itemId " + item.getItemId() + " already exists.");
        }
        items.add(item);
        return item;
    }

    /**
     * method: findAll
     * parameters: none
     * return: ArrayList
     * purpose: Read (All). Returns a copy of all items
     */
    public List<RunescapeItem> findAll() {
        return new ArrayList<>(items);
    }

    /**
     * method: findById
     * parameters: int
     * return: ArrayList
     * purpose: Read (by id)
     */
    public Optional<RunescapeItem> findById(int id) {
        return items.stream().filter(i -> i.getItemId() == id).findFirst();
    }


    /**
     * method: update
     * parameters: int, ItemUpdater
     * return: String
     * purpose: Update. Applies a mutator to the found item and returns the updated snapshot string
     */
    public RunescapeItem update(int id, ItemUpdater updater) throws ValidationException {
        RunescapeItem item = findById(id)
                .orElseThrow(() -> new ValidationException("No item found with itemId " + id + "."));
        updater.apply(item);
        return item;
    }

    /**
     * method: delete
     * parameters: int
     * return: String
     * purpose: Delete. Returns removed item so callers can show a confirmation snapshot
     */
    public RunescapeItem delete(int id) throws ValidationException {
        RunescapeItem item = findById(id)
                .orElseThrow(() -> new ValidationException("No item found with itemId " + id + "."));
        items.remove(item);
        return item;
    }

    /**
     * method: searchByNameOrType
     * parameters: String
     * return: ArrayList
     * purpose: Search for items, provided a keyword
     */
    public List<RunescapeItem> searchByNameOrType(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        return items.stream()
                .filter(i -> i.getName().toLowerCase().contains(q)
                        || i.getType().name().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /** Interface to perform validated updates on an item */
    @FunctionalInterface
    public interface ItemUpdater {
        void apply(RunescapeItem item) throws ValidationException;
    }
}
