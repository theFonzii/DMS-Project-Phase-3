import com.example.dms_project_phase_3_real.ItemType;
import com.example.dms_project_phase_3_real.RunescapeItem;
import com.example.dms_project_phase_3_real.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * RunescapeItemValidationTest.java
 * This class contains the test units for the RunescapeItem class.
 * It validates the object creation logic within the class,
 * and demonstrates exceptions for invalid attributes.
 */

public class RunescapeItemValidationTest {
    @Test
    void ctor_rejects_invalid_fields() {
        assertThrows(ValidationException.class,
                () -> new RunescapeItem(0, "X", ItemType.OTHER, 0, 0.0, 0)); // bad id
        assertThrows(ValidationException.class,
                () -> new RunescapeItem(1, " ", ItemType.OTHER, 0, 0.0, 0)); // blank name
        assertThrows(ValidationException.class,
                () -> new RunescapeItem(1, "X", ItemType.OTHER, -1, 0.0, 0)); // bad level
        assertThrows(ValidationException.class,
                () -> new RunescapeItem(1, "X", ItemType.OTHER, 0, -0.1, 0)); // bad weight
        assertThrows(ValidationException.class,
                () -> new RunescapeItem(1, "X", ItemType.OTHER, 0, 0.0, -5)); // bad price
    }

    @Test
    void ctor_valid_ok() throws Exception {
        var item = new RunescapeItem(1, "Okay", ItemType.OTHER, 0, 0.0, 0);
        assertEquals(1, item.getItemId());
        assertEquals("Okay", item.getName());
    }
}