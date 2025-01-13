package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.ResourcesLoader;
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
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;


import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class IlunCryptController implements Initializable {
    private double xOffset = 0;
    private double yOffset = 0;
    private final Stage stage;
    private final ToggleGroup toggleGroup;

    private static final int TOP_INTERACTION_AREA = 50;

    private boolean isMaximized = false;


    private ResourceBundle bundle;

    @FXML
    private HBox windowHeader;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon maximizeIcon;

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
        enableTopBorderDrag();
        initializeLoader();
        setupTooltips();
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = !stage.isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            stage.setAlwaysOnTop(newVal);
        });

        maximizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> toggleMaximize());

        ScrollUtils.addSmoothScrolling(scrollPane);

        Image image = new Image(ResourcesLoader.load("assets/icons/icon-512.png"), 64, 64, true, true);
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

    private void setupTooltips() {
        Tooltip alwaysOnTopTooltip = new Tooltip("Toggle Always on Top");
        Tooltip minimizeTooltip = new Tooltip("Minimize Window");
        Tooltip maximizeTooltip = new Tooltip("Maximize/Restore Window");
        Tooltip closeTooltip = new Tooltip("Close Application");

        alwaysOnTopTooltip.setShowDelay(Duration.millis(100)); // Retraso antes de mostrar
        alwaysOnTopTooltip.setHideDelay(Duration.millis(200)); // Retraso antes de ocultar
        alwaysOnTopTooltip.setShowDuration(Duration.seconds(5)); // Tiempo máximo visible

        minimizeTooltip.setShowDelay(Duration.millis(100));
        minimizeTooltip.setHideDelay(Duration.millis(200));
        minimizeTooltip.setShowDuration(Duration.seconds(5));

        maximizeTooltip.setShowDelay(Duration.millis(100));
        maximizeTooltip.setHideDelay(Duration.millis(200));
        maximizeTooltip.setShowDuration(Duration.seconds(5));

        closeTooltip.setShowDelay(Duration.millis(100));
        closeTooltip.setHideDelay(Duration.millis(200));
        closeTooltip.setShowDuration(Duration.seconds(5));

        Tooltip.install(alwaysOnTopIcon, alwaysOnTopTooltip); // Tooltip para AlwaysOnTopIcon
        Tooltip.install(minimizeIcon, minimizeTooltip);       // Tooltip para MinimizeIcon
        Tooltip.install(maximizeIcon, maximizeTooltip);       // Tooltip para MaximizeIcon
        Tooltip.install(closeIcon, closeTooltip);             // Tooltip para CloseIcon
    }


    private void enableTopBorderDrag() {
        windowHeader.setOnMouseMoved(event -> {
            // Cambiar cursor a manita abierta solo si no está sobre los botones
            if (!isOverButton(event)) {
                windowHeader.setCursor(Cursor.OPEN_HAND);
            } else {
                windowHeader.setCursor(Cursor.DEFAULT);
            }
        });

        windowHeader.setOnMousePressed(event -> {
            if (!isOverButton(event)) { // Solo permitir arrastre si no se hace clic en un botón
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
                windowHeader.setCursor(Cursor.CLOSED_HAND); // Cambiar a manita cerrada
            }
        });

        windowHeader.setOnMouseDragged(event -> {
            if (!isOverButton(event)) { // Solo arrastrar si no está sobre un botón
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        windowHeader.setOnMouseReleased(event -> {
            if (!isOverButton(event)) {
                windowHeader.setCursor(Cursor.OPEN_HAND); // Restaurar a manita abierta
            } else {
                windowHeader.setCursor(Cursor.DEFAULT); // Restaurar a cursor por defecto
            }
        });

        windowHeader.setOnMouseExited(event -> windowHeader.setCursor(Cursor.DEFAULT)); // Restaurar cursor al salir
    }

    // Método para verificar si el ratón está sobre un botón
    private boolean isOverButton(MouseEvent event) {
        return alwaysOnTopIcon.getBoundsInParent().contains(event.getX(), event.getY()) ||
                minimizeIcon.getBoundsInParent().contains(event.getX(), event.getY()) ||
                maximizeIcon.getBoundsInParent().contains(event.getX(), event.getY()) ||
                closeIcon.getBoundsInParent().contains(event.getX(), event.getY());
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

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();
        //loader.addView(MFXLoaderBean.of("ENCRYPT-DECRYPT-OPTIONS", MFXDemoResourcesLoader.loadURL("views/encrypt-decrypt-options-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Encrypt/Decrypt")).setDefaultRoot(true).get());
        loader.addView(MFXLoaderBean.of("ENCRYPT-DECRYPT-OPTIONS", ResourcesLoader.loadURL("views/permutation-cipher-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Encrypt/Decrypt")).setDefaultRoot(true).get());

        loader.addView(MFXLoaderBean.of("CRIPTOANALYSIS-OPTIONS", ResourcesLoader.loadURL("views/cryptoanalysis-options-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Cryptoanalysis")).setDefaultRoot(true).get());

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

    private void toggleMaximize() {
        if (isMaximized) {
            // Restaurar tamaño de la ventana
            stage.setMaximized(false);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double stageWidth = screenBounds.getWidth() * 0.8;
            double stageHeight = screenBounds.getHeight() * 0.8;
            stage.setWidth(stageWidth); // Tamaño predeterminado
            stage.setHeight(stageHeight);
            stage.centerOnScreen(); // Centrar la ventana
            maximizeIcon.setDescription("fas-circle-chevron-up"); // Cambiar a icono de expandir
        } else {
            // Maximizar la ventana
            stage.setMaximized(true); // Configurar estado maximizado
            maximizeIcon.setDescription("fas-circle-chevron-down"); // Cambiar a icono de restaurar
        }
        isMaximized = !isMaximized; // Alternar el estado
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
