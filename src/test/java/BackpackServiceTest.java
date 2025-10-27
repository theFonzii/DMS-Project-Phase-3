import com.example.dms_project_phase_3_real.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * BackpackServiceTest.java
 * This class contains the test units for the BackpackService class.
 * It validates the math for calculating the weight limit,
 * and demonstrates exceptions for unknown item ID or invalid quantities.
 */

public class BackpackServiceTest {

    private ItemRepository repo;
    private BackpackService svc;

    @BeforeEach
    void setup() throws Exception {
        repo = new ItemRepository();
        // Create two simple items for a clean boundary case
        repo.add(new RunescapeItem(1, "Heavy Thing", ItemType.OTHER, 0, 2.0, 100)); // 2.0 kg
        repo.add(new RunescapeItem(2, "Light Thing", ItemType.OTHER, 0, 1.0, 50)); // 1.0 kg
        // Threshold set to 3.0 kg for clear boundary tests
        svc = new BackpackService(repo, 3.0);
    }

    @Test
    @DisplayName("AFFIRMATIVE: Totals correct; NOT encumbered at threshold boundary (== 3.0kg)")
    void summarize_totals_notEncumberedAtBoundary() throws Exception {
        // 2.0 + 1.0 = 3.0 kg exactly (100 + 50 = 150 gp)
        var s = svc.summarize(List.of(new ItemQty(1, 1), new ItemQty(2, 1)));
        assertEquals(3.00, s.totalWeightKg(), 1e-9);
        assertEquals(150, s.totalValueGp());
        assertFalse(s.encumbered(), "Exactly 3.0 kg should NOT exceed the 3.0 threshold");
    }

    @Test
    @DisplayName("AFFIRMATIVE: Encumbrance true when total weight exceeds threshold")
    void summarize_encumbered_whenOverThreshold() throws Exception {
        // 2 x 2.0 kg = 4.0 kg (over 3.0), 2 x 100 gp = 200 gp
        var s = svc.summarize(List.of(new ItemQty(1, 2)));
        assertTrue(s.encumbered());
        assertEquals(4.0, s.totalWeightKg(), 1e-9);
        assertEquals(200, s.totalValueGp());
    }

    @Test
    @DisplayName("NEGATIVE: Unknown itemId causes ValidationException")
    void summarize_unknownItem_error() {
        assertThrows(ValidationException.class, () -> svc.summarize(List.of(new ItemQty(9999, 1))));
    }

    @Test
    @DisplayName("NEGATIVE: Invalid quantity (<=0) rejected by ItemQty record")
    void summarize_invalidQty_error() {
        assertThrows(IllegalArgumentException.class, () -> new ItemQty(1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ItemQty(1, -3));
    }
}