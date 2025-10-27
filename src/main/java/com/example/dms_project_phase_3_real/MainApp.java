package com.example.dms_project_phase_3_real;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * October 24th, 2025
 * MainApp.java
 * This application is the entry point for all JavaFX functionality,
 * loading the FXML layout, applying the brown/tan theme,
 * and displaying the primary window to the user. No database connectivity
 * is utilized in this phase of the project.
 */

public class MainApp extends Application {

    /**
     * method: start
     * parameters: primaryStage
     * return: void
     * purpose: Loads the FXML file, Loads the CSS file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL fxmlLocation = getClass().getResource("/com/example/dms_project_phase_3_real/app.fxml");
        Objects.requireNonNull(fxmlLocation, "FXML file not found at /com/example/dms_project_phase_3_real/app.fxml");

        Parent root = FXMLLoader.load(fxmlLocation);

        Scene scene = new Scene(root, 1000, 600);


        URL css = getClass().getResource("/com/example/dms_project_phase_3_real/theme.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        primaryStage.setTitle("DMS Project - Phase 3");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * method: main
     * parameters: args
     * return: void
     * purpose: Launch method required by JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
