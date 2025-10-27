package com.example.dms_project_phase_3_real;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 24th, 2025
 * AppController.java
 * This class contains the JavaFX controller that connects
 * GUI events to the logic laid out in Phase 1.
 * It initializes table columns, handles CRUD + Custom functionality,
 * and displays status messages.
 */

public class AppController {

    // ====== Table + columns ======
    @FXML private TableView<RunescapeItem> table;
    @FXML private TableColumn<RunescapeItem, Integer> idCol;
    @FXML private TableColumn<RunescapeItem, String>  nameCol;
    @FXML private TableColumn<RunescapeItem, String>  typeCol;
    @FXML private TableColumn<RunescapeItem, Integer> levelCol;
    @FXML private TableColumn<RunescapeItem, String>  weightCol;
    @FXML private TableColumn<RunescapeItem, Integer> priceCol;

    // ====== Form fields ======
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private ComboBox<ItemType> typeBox;
    @FXML private TextField levelField;
    @FXML private TextField weightField;
    @FXML private TextField priceField;

    // ====== Top/Right buttons and status ======
    @FXML private Button btnLoadCsv, btnBackpack, btnExit, btnAdd, btnUpdate, btnDelete;
    @FXML private Label status;

    // ====== Services reused from Phase 1 ======
    private final ItemRepository repo = new ItemRepository();
    private final BackpackService backpack = new BackpackService(repo, 30.0 /* kg */);
    private final TextFileAdapter files = new TextFileAdapter();

    /**
     * method: initialize
     * parameters: none
     * return: void
     * purpose: Sets up table columns and default control values.
     */
    @FXML
    public void initialize() {
        // Populate Type combo
        typeBox.setItems(FXCollections.observableArrayList(ItemType.values()));
        typeBox.getSelectionModel().select(ItemType.OTHER);

        // Bind table columns to RunescapeItem getters
        idCol.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getType().name()));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("requiredLevel"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("gePrice"));
        // Weight formatted to two decimals
        weightCol.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.format("%.2f", cell.getValue().getWeightKg())));

        refreshTable();
        status.setText("Ready.");
    }

    // ======================= Menu actions =======================

    /**
     * method: onLoadCsv
     * parameters: none
     * return: void
     * purpose: Load CSV via FileChooser, skip invalid rows.
     */
    @FXML
    public void onLoadCsv() {
        Window w = table.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File f = fc.showOpenDialog(w);
        if (f == null) return;

        var result = files.loadCsv(f.getAbsolutePath());
        int added = 0;
        for (RunescapeItem it : result.items()) {
            try { repo.add(it); added++; }
            catch (ValidationException ignored) {} /* duplicate IDs are reported below */
        }
        refreshTable();
        status.setText("Loaded " + added + " item(s). Errors: " + result.errors().size());
    }


    /**
     * method: onBackpack
     * parameters: none
     * return: void
     * purpose: Compute totals, show encumbrance message.
     */
    @FXML
    public void onBackpack() {
        TextInputDialog dialog = new TextInputDialog("1001:1,1002:2");
        dialog.setTitle("Backpack Calculator");
        dialog.setHeaderText("Enter itemId:qty pairs (comma-separated)");
        dialog.setContentText("Selection:");

        dialog.showAndWait().ifPresent(raw -> {
            try {
                List<ItemQty> picks = new ArrayList<>();
                for (String token : raw.split(",")) {
                    String[] p = token.trim().split(":");
                    int id = Integer.parseInt(p[0].trim());
                    int q  = Integer.parseInt(p[1].trim());
                    picks.add(new ItemQty(id, q));
                }
                var summary = backpack.summarize(picks);
                status.setText(summary.toDisplayString());
            } catch (Exception ex) {
                status.setText("Backpack failed: " + ex.getMessage());
            }
        });
    }

    /**
     * method: onExit
     * parameters: none
     * return: void
     * purpose: Graceful exit.
     */
    @FXML
    public void onExit() {
        table.getScene().getWindow().hide();
    }

    // ======================= CRUD actions =======================

    /**
     * method: onAdd
     * parameters: none
     * return: void
     * purpose: Add a new item from the form with validation.
     */
    @FXML
    public void onAdd() {
        try {
            RunescapeItem it = buildFromForm(true);
            repo.add(it);
            clearForm();
            refreshTable();
            status.setText("Added: " + it.getName() + " (#" + it.getItemId() + ")");
        } catch (Exception ex) {
            status.setText("Add failed: " + ex.getMessage());
        }
    }

    /**
     * method: onUpdateSelected
     * parameters: none
     * return: void
     * purpose: Update the selected row; blank fields keep their current values.
     */
    @FXML
    public void onUpdateSelected() {
        RunescapeItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { status.setText("Select a row to update."); return; }
        try {
            repo.update(sel.getItemId(), item -> {

                String nm = nameField.getText().trim();
                if (!nm.isEmpty()) item.setName(nm);

                ItemType t = typeBox.getValue();
                if (t != null) item.setType(t);

                String lvl = levelField.getText().trim();
                if (!lvl.isEmpty()) item.setRequiredLevel(parseIntMin(lvl, 0, "requiredLevel"));

                String w = weightField.getText().trim();
                if (!w.isEmpty()) item.setWeightKg(parseDoubleMin(w, 0.0, "weightKg"));

                String p = priceField.getText().trim();
                if (!p.isEmpty()) item.setGePrice(parseIntMin(p, 0, "gePrice"));
            });
            refreshTable();
            status.setText("Updated item #" + sel.getItemId());
        } catch (Exception ex) {
            status.setText("Update failed: " + ex.getMessage());
        }
    }

    /**
     * method: onDeleteSelected
     * parameters: none
     * return: void
     * purpose: Delete the currently selected row with confirmation.
     */
    @FXML
    public void onDeleteSelected() {
        RunescapeItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { status.setText("Select a row to delete."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete item #" + sel.getItemId() + " (" + sel.getName() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirm Deletion");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    repo.delete(sel.getItemId());
                    refreshTable();
                    status.setText("Deleted item #" + sel.getItemId());
                } catch (ValidationException ex) {
                    status.setText("Delete failed: " + ex.getMessage());
                }
            }
        });
    }

    // ======================= Helpers =======================

    /**
     * method: buildFromForm
     * parameters: requireId
     * return: RunescapeItem
     * purpose: Pulls values from form fields and creates a validated item.
     */
    private RunescapeItem buildFromForm(boolean requireId) throws ValidationException {
        String idText = idField.getText().trim();
        if (requireId && idText.isEmpty())
            throw new ValidationException("itemId is required.");
        int id = Integer.parseInt(idText);

        String name = nameField.getText().trim();
        ItemType type = typeBox.getValue();

        int level = parseIntMin(levelField.getText(), 0, "requiredLevel");
        double weight = parseDoubleMin(weightField.getText(), 0.0, "weightKg");
        int price = parseIntMin(priceField.getText(), 0, "gePrice");

        return new RunescapeItem(id, name, type, level, weight, price);
    }

    private void clearForm() {
        idField.clear(); nameField.clear(); levelField.clear();
        weightField.clear(); priceField.clear(); typeBox.getSelectionModel().select(ItemType.OTHER);
    }

    private void refreshTable() {
        table.getItems().setAll(repo.findAll());
        table.refresh();
    }

    /**
     * method: parseIntMin
     * parameters: raw, min, field
     * return: v
     * purpose: Simple parse helper with user-friendly message.
     */
    private int parseIntMin(String raw, int min, String field) throws ValidationException {
        try {
            int v = Integer.parseInt(raw.trim());
            if (v < min) throw new NumberFormatException();
            return v;
        } catch (Exception ex) {
            throw new ValidationException(field + " must be an integer >= " + min + ".");
        }
    }

    /**
     * method: parseDoubleMin
     * parameters: raw, min, field
     * return: v
     * purpose: Simple parse helper with user-friendly message.
     */
    private double parseDoubleMin(String raw, double min, String field) throws ValidationException {
        try {
            double v = Double.parseDouble(raw.trim());
            if (v < min) throw new NumberFormatException();
            return v;
        } catch (Exception ex) {
            throw new ValidationException(field + " must be a number >= " + min + ".");
        }
    }
}
