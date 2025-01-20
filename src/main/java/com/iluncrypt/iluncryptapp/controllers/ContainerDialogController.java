package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.ResourcesLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Controller for the dialog container with scrolling capability.
 * It dynamically loads another FXML inside the scroll container.
 */
public class ContainerDialogController {

    @FXML
    private VBox contentBox;

    /**
     * Loads an FXML file inside the scrollable container.
     *
     * @param fxmlPath The path to the FXML file to load.
     * @param bundle   The resource bundle for localization.
     */
    public void loadContent(String fxmlPath, ResourceBundle bundle) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourcesLoader.loadURL(fxmlPath), bundle);
            Node content = loader.load();
            contentBox.getChildren().setAll(content); // Replace existing content with the new one
        } catch (IOException e) {
            throw new RuntimeException("Failed to load content FXML: " + fxmlPath, e);
        }
    }
}
