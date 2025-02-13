package com.iluncrypt.iluncryptapp;

import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import fr.brouillard.oss.cssfx.CSSFX;

import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;
import java.util.Objects;

public class IlunCrypt extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        CSSFX.start();
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        FXMLLoader loader = new FXMLLoader(ResourcesLoader.loadURL("views/ilun-crypt-view.fxml"));
        loader.setResources(LanguageManager.getInstance().getBundle());
        loader.setControllerFactory(c -> new IlunCryptController(stage));

        Parent root = loader.load();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        double stageWidth = screenWidth * 0.8;
        double stageHeight = screenHeight * 0.8;

        Scene scene = new Scene(root, stageWidth, stageHeight);

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/icons/icon-512.png")));
        stage.getIcons().add(icon);

        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        stage.setResizable(false);

        stage.setMinWidth(screenWidth * 0.5);
        stage.setMinHeight(screenHeight * 0.5);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
