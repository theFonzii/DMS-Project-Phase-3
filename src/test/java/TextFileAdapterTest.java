import com.example.dms_project_phase_3_real.TextFileAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 7th, 2025
 * TextFileAdapterTest.java
 * This class contains the test units for the TextFileAdapter class.
 * It verifies that the system can read a CSV file correctly,
 * and demonstrates the exception for invalid formatting.
 */

public class TextFileAdapterTest {

    @TempDir Path tempDir;

    @Test
    @DisplayName("AFFIRMATIVE: CSV with valid header & rows loads successfully")
    void loadCsv_valid_success() throws IOException {
        Path csv = tempDir.resolve("items.csv");
        Files.writeString(csv, String.join("\n",
                "itemId,name,type,requiredLevel,weightKg,gePrice",
                "1001,Rune Scimitar,WEAPON,40,2.5,12000",
                "1002,Lobster,FOOD,1,0.2,180"
        ));
        TextFileAdapter adapter = new TextFileAdapter();
        var result = adapter.loadCsv(csv.toString());

        assertEquals(2, result.items().size());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("NEGATIVE: Bad header -> no items loaded, error reported")
    void loadCsv_badHeader_error() throws IOException {
        Path csv = tempDir.resolve("bad.csv");
        Files.writeString(csv, "wrong,header\n1,x,y,z,a,b\n");
        TextFileAdapter adapter = new TextFileAdapter();
        var result = adapter.loadCsv(csv.toString());

        assertTrue(result.items().isEmpty());
        assertFalse(result.errors().isEmpty());
    }
}