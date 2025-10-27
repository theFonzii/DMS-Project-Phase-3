import com.example.dms_project_phase_3_real.ItemRepository;
import com.example.dms_project_phase_3_real.ItemType;
import com.example.dms_project_phase_3_real.RunescapeItem;
import com.example.dms_project_phase_3_real.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * ItemRepositoryTest.java
 * This class contains the test units for the ItemRepository class.
 * It validates the functionality of the Create, Update, and Delete operations,
 * and demonstrates exceptions for invalid/non-existent/duplicate data.
 */

public class ItemRepositoryTest {

    private ItemRepository repo;

    @BeforeEach
    void setup() { repo = new ItemRepository(); }

    private RunescapeItem make(int id) throws ValidationException {
        return new RunescapeItem(id, "Rune Scimitar", ItemType.WEAPON, 40, 2.5, 12000);
    }

    // -------- CREATE (ADD) --------
    @Test
    @DisplayName("AFFIRMATIVE: Add succeeds with unique itemId")
    void add_unique_success() throws Exception {
        var added = repo.add(make(1001));
        assertEquals(1, repo.findAll().size());
        assertEquals(1001, added.getItemId());
    }

    @Test
    @DisplayName("NEGATIVE: Add fails on duplicate itemId")
    void add_duplicate_error() throws Exception {
        repo.add(make(1001));
        var ex = assertThrows(ValidationException.class, () -> repo.add(make(1001)));
        assertTrue(ex.getMessage().toLowerCase().contains("already"));
    }

    // -------- REMOVE (DELETE) --------
    @Test
    @DisplayName("AFFIRMATIVE: Delete removes existing item")
    void delete_existing_success() throws Exception {
        repo.add(make(1001));
        var removed = repo.delete(1001);
        assertEquals(1001, removed.getItemId());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    @DisplayName("NEGATIVE: Delete non-existent item throws")
    void delete_missing_error() {
        var ex = assertThrows(ValidationException.class, () -> repo.delete(9999));
        assertTrue(ex.getMessage().toLowerCase().contains("no item"));
    }

    // -------- UPDATE --------
    @Test
    @DisplayName("AFFIRMATIVE: Update persists changed GE price")
    void update_success() throws Exception {
        repo.add(make(1001));
        var updated = repo.update(1001, it -> it.setGePrice(11000));
        assertEquals(11000, updated.getGePrice());
        assertEquals(11000, repo.findById(1001).orElseThrow().getGePrice());
    }

    @Test
    @DisplayName("NEGATIVE: Update against missing item throws")
    void update_missing_error() {
        var ex = assertThrows(ValidationException.class,
                () -> repo.update(7777, it -> it.setGePrice(1)));
        assertTrue(ex.getMessage().toLowerCase().contains("no item"));
    }

    @Test
    @DisplayName("NEGATIVE: Update rejects invalid value (e.g., negative price)")
    void update_invalidValue_error() throws Exception {
        repo.add(make(1001));
        var ex = assertThrows(ValidationException.class,
                () -> repo.update(1001, it -> it.setGePrice(-5)));
        assertTrue(ex.getMessage().toLowerCase().contains("geprice"));
    }
}