package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.MFXDemoResourcesLoader;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class IlunCryptController implements Initializable {
    private static final int RESIZE_MARGIN = 10; // Tamaño de la zona de redimensionamiento
    private boolean isResizing = false; // Indica si estamos redimensionando

    private final Stage stage;
    private double xOffset;
    private double yOffset;
    private final ToggleGroup toggleGroup;

    private ResourceBundle bundle;

    @FXML
    private HBox windowHeader;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon minimizeIcon;

    @FXML
    private MFXFontIcon alwaysOnTopIcon;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MFXScrollPane scrollPane;

    @FXML
    private VBox navBar;

    @FXML
    private StackPane contentPane;

    @FXML
    private StackPane logoContainer;

    @FXML
    private MFXToggleButton themeToggle;       // Botón MaterialFX para cambiar el tema

    @FXML
    private MFXComboBox<String> languageComboBox; // ComboBox MaterialFX para el cambio de idioma


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLanguage("en");
        setupThemeToggle();
        setupLanguageComboBox();
        enableResize();
        enableWindowDrag();
        initializeLoader();
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = !stage.isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            stage.setAlwaysOnTop(newVal);
        });


        ScrollUtils.addSmoothScrolling(scrollPane);

        Image image = new Image(MFXDemoResourcesLoader.load("assets/icons/icon-512.png"), 64, 64, true, true);
        ImageView logo = new ImageView(image);
        Circle clip = new Circle(30);
        clip.centerXProperty().bind(
                javafx.beans.binding.Bindings.createDoubleBinding(
                        () -> logo.getLayoutBounds().getCenterX(),
                        logo.layoutBoundsProperty()
                )
        );

        clip.centerYProperty().bind(
                javafx.beans.binding.Bindings.createDoubleBinding(
                        () -> logo.getLayoutBounds().getCenterY(),
                        logo.layoutBoundsProperty()
                )
        );
        logo.setClip(clip);
        logoContainer.getChildren().add(logo);
    }

    // Configura el botón de cambio de tema
    private void setupThemeToggle() {
        themeToggle.setText("Light Theme");
        themeToggle.setOnAction(event -> {
            if (themeToggle.isSelected()) {
                themeToggle.setText(bundle.getString("theme.dark"));
                rootPane.getStylesheets().clear();
                rootPane.getStylesheets().add(getClass().getResource("../assets/styles/DarkTheme.css").toExternalForm());
            } else {
                themeToggle.setText(bundle.getString("theme.light"));
                rootPane.getStylesheets().clear();
                rootPane.getStylesheets().add(getClass().getResource("../assets/styles/Demo.css").toExternalForm());
            }
        });
    }

    // Configura el ComboBox para el cambio de idioma
    private void setupLanguageComboBox() {
        languageComboBox.getItems().addAll("Español", "Português", "English", "Français");
        languageComboBox.setPromptText("");

        languageComboBox.setOnAction(event -> {
            String selectedLanguage = languageComboBox.getValue();
            switch (selectedLanguage) {
                case "Español" -> setLanguage("es");
                case "Português" -> setLanguage("pt");
                case "English" -> setLanguage("en");
                case "Français" -> setLanguage("fr");
            }
        });
    }

    private void enableWindowDrag() {
        windowHeader.setOnMousePressed(event -> {
            // Cambia el cursor a MOVE solo si no está en modo "resize"
            if (!isResizing) {
                windowHeader.setCursor(javafx.scene.Cursor.MOVE); // Cambiar el cursor a arrastrar
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        windowHeader.setOnMouseDragged(event -> {
            // Mueve la ventana solo si no está en modo "resize"
            if (!isResizing) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

        windowHeader.setOnMouseReleased(event -> {
            // Restaurar el cursor al valor predeterminado
            windowHeader.setCursor(javafx.scene.Cursor.DEFAULT);
        });
    }

    private void enableResize() {
        rootPane.setOnMouseMoved(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            if (mouseX < RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
                rootPane.setCursor(javafx.scene.Cursor.NW_RESIZE);
                isResizing = true;
            } else if (mouseX < RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
                rootPane.setCursor(javafx.scene.Cursor.SW_RESIZE);
                isResizing = true;
            } else if (mouseX > width - RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
                rootPane.setCursor(javafx.scene.Cursor.NE_RESIZE);
                isResizing = true;
            } else if (mouseX > width - RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
                rootPane.setCursor(javafx.scene.Cursor.SE_RESIZE);
                isResizing = true;
            } else if (mouseX < RESIZE_MARGIN) {
                rootPane.setCursor(javafx.scene.Cursor.W_RESIZE);
                isResizing = true;
            } else if (mouseX > width - RESIZE_MARGIN) {
                rootPane.setCursor(javafx.scene.Cursor.E_RESIZE);
                isResizing = true;
            } else if (mouseY < RESIZE_MARGIN) {
                rootPane.setCursor(javafx.scene.Cursor.N_RESIZE);
                isResizing = true;
            } else if (mouseY > height - RESIZE_MARGIN) {
                rootPane.setCursor(javafx.scene.Cursor.S_RESIZE);
                isResizing = true;
            } else {
                rootPane.setCursor(javafx.scene.Cursor.DEFAULT);
                isResizing = false;
            }
        });

        rootPane.setOnMouseDragged(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double newWidth = stage.getWidth();
            double newHeight = stage.getHeight();
            double newX = stage.getX();
            double newY = stage.getY();

            if (rootPane.getCursor() == javafx.scene.Cursor.W_RESIZE) {
                newWidth = stage.getWidth() - mouseX;
                if (newWidth >= stage.getMinWidth()) {
                    newX = stage.getX() + mouseX; // Solo mover el borde izquierdo
                } else {
                    return;
                }
            } else if (rootPane.getCursor() == javafx.scene.Cursor.E_RESIZE) {
                newWidth = mouseX;
                if (newWidth < stage.getMinWidth()) {
                    return;
                }
            } else if (rootPane.getCursor() == javafx.scene.Cursor.N_RESIZE) {
                newHeight = stage.getHeight() - mouseY;
                if (newHeight >= stage.getMinHeight()) {
                    newY = stage.getY() + mouseY; // Solo mover el borde superior
                } else {
                    return;
                }
            } else if (rootPane.getCursor() == javafx.scene.Cursor.S_RESIZE) {
                newHeight = mouseY;
                if (newHeight < stage.getMinHeight()) {
                    return;
                }
            } else if (rootPane.getCursor() == javafx.scene.Cursor.NW_RESIZE) {
                newWidth = stage.getWidth() - mouseX;
                newHeight = stage.getHeight() - mouseY;
                if (newWidth >= stage.getMinWidth() && newHeight >= stage.getMinHeight()) {
                    newX = stage.getX() + mouseX; // Ajustar solo el borde izquierdo
                    newY = stage.getY() + mouseY; // Ajustar solo el borde superior
                } else {
                    return;
                }
            } else if (rootPane.getCursor() == javafx.scene.Cursor.NE_RESIZE) {
                newWidth = mouseX;
                newHeight = stage.getHeight() - mouseY;
                if (newWidth >= stage.getMinWidth() && newHeight >= stage.getMinHeight()) {
                    newY = stage.getY() + mouseY; // Ajustar solo el borde superior
                } else {
                    return;
                }
            } else if (rootPane.getCursor() == javafx.scene.Cursor.SW_RESIZE) {
                newWidth = stage.getWidth() - mouseX;
                newHeight = mouseY;
                if (newWidth >= stage.getMinWidth() && newHeight >= stage.getMinHeight()) {
                    newX = stage.getX() + mouseX; // Ajustar solo el borde izquierdo
                } else {
                    return;
                }
            } else if (rootPane.getCursor() == javafx.scene.Cursor.SE_RESIZE) {
                newWidth = mouseX;
                newHeight = mouseY;
                if (newWidth < stage.getMinWidth() || newHeight < stage.getMinHeight()) {
                    return;
                }
            }

            // Aplicar los nuevos tamaños y posiciones
            if (newWidth >= stage.getMinWidth()) {
                stage.setWidth(newWidth);
            }
            if (newHeight >= stage.getMinHeight()) {
                stage.setHeight(newHeight);
            }
            stage.setX(newX);
            stage.setY(newY);
        });

        rootPane.setOnMouseReleased(event -> {
            rootPane.setCursor(javafx.scene.Cursor.DEFAULT);
            isResizing = false;
        });
    }

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();
        loader.addView(MFXLoaderBean.of("ENCRYPT-DECRYPT-OPTIONS", MFXDemoResourcesLoader.loadURL("views/encrypt-decrypt-options-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Encrypt/Decrypt")).setDefaultRoot(true).get());
        loader.addView(MFXLoaderBean.of("CRIPTOANALYSIS-OPTIONS", MFXDemoResourcesLoader.loadURL("views/cryptoanalysis-options-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Cryptoanalysis")).setDefaultRoot(true).get());

        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = beans.stream()
                    .map(bean -> {
                        ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                        toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
                        if (bean.isDefaultView()) {
                            contentPane.getChildren().setAll(bean.getRoot());
                            toggle.setSelected(true);
                        }
                        return toggle;
                    })
                    .toList();
            navBar.getChildren().setAll(nodes);
        });
        loader.start();
    }

    public IlunCryptController(Stage stage) {
        this.stage = stage;
        this.toggleGroup = new ToggleGroup();
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
    }

    public void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        bundle = ResourceBundle.getBundle("com.iluncrypt.iluncryptapp.locales.messages", locale);
        updateUI();
    }

    private void updateUI() {
        themeToggle.setText(bundle.getString("theme.light"));
        languageComboBox.setPromptText(bundle.getString("label.language"));
    }

    private ToggleButton createToggle(String icon, String text) {
        return createToggle(icon, text, 0);
    }

    private ToggleButton createToggle(String icon, String text, double rotate) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        if (rotate != 0) wrapper.getIcon().setRotate(rotate);
        return toggleNode;
    }

}
