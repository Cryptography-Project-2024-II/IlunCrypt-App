package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.ResourcesLoader;
import com.iluncrypt.iluncryptapp.controllers.classic.affine.AffineCipherController;
import com.iluncrypt.iluncryptapp.controllers.classic.hill.HillCipherController;
import com.iluncrypt.iluncryptapp.controllers.classic.multiplicative.MultiplicativeCipherController;
import com.iluncrypt.iluncryptapp.controllers.classic.permutation.PermutationCipherController;
import com.iluncrypt.iluncryptapp.controllers.classic.shift.ShiftCipherController;
import com.iluncrypt.iluncryptapp.controllers.classic.substitution.SubstitutionCipherController;
import com.iluncrypt.iluncryptapp.controllers.classic.vigenere.VigenereCipherController;
import com.iluncrypt.iluncryptapp.controllers.cryptanalysis.brauer.BrauerAnalysisController;
import com.iluncrypt.iluncryptapp.controllers.cryptanalysis.bruteforce.BruteForceAnalysisController;
import com.iluncrypt.iluncryptapp.controllers.cryptanalysis.frequency.FrequencyAnalysisController;
import com.iluncrypt.iluncryptapp.controllers.cryptanalysis.friedman.FriedmanAnalysisController;
import com.iluncrypt.iluncryptapp.controllers.digitalsignature.dsa.DSASignatureController;
import com.iluncrypt.iluncryptapp.controllers.digitalsignature.rsa.RSASignatureController;
import com.iluncrypt.iluncryptapp.controllers.imageencryption.aes.AESImageController;
import com.iluncrypt.iluncryptapp.controllers.imageencryption.des.DESImageController;
import com.iluncrypt.iluncryptapp.controllers.imageencryption.hill.HillImageController;
import com.iluncrypt.iluncryptapp.controllers.imageencryption.permutation.PermutationImageController;
import com.iluncrypt.iluncryptapp.controllers.publickey.elgamal.ElGamalController;
import com.iluncrypt.iluncryptapp.controllers.publickey.menezesvanstone.MenezesVanstoneController;
import com.iluncrypt.iluncryptapp.controllers.publickey.rabin.RabinController;
import com.iluncrypt.iluncryptapp.controllers.publickey.rsa.RSAController;
import com.iluncrypt.iluncryptapp.controllers.symmetrickey.aes.AESController;
import com.iluncrypt.iluncryptapp.controllers.symmetrickey.des.DESController;
import com.iluncrypt.iluncryptapp.controllers.symmetrickey.sdes.SDESController;
import com.iluncrypt.iluncryptapp.models.enums.Language;
import com.iluncrypt.iluncryptapp.utils.ConfigManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Main controller for the IlunCrypt application.
 * Manages the application views, events, and global settings.
 */
public class IlunCryptController implements Initializable {
    private static IlunCryptController instance;
    private final MFXLoader loader = new MFXLoader();
    private final Stage stage;
    private final ToggleGroup toggleGroup;
    private double xOffset = 0;
    private double yOffset = 0;

    private final DialogHelper dialog;

    private static final int TOP_INTERACTION_AREA = 50;

    private boolean isMaximized = false;
    private ResourceBundle bundle;

    // FXML bindings
    @FXML
    private VBox sidebar;

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
    private MFXToggleButton themeToggle;

    @FXML
    private MFXComboBox<String> languageComboBox;

    /**
     * Constructor with stage injection.
     *
     * @param stage The main application stage.
     */
    public IlunCryptController(Stage stage) {
        this.stage = stage;
        this.toggleGroup = new ToggleGroup();
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
        instance = this;
        this.dialog = new DialogHelper(stage);
    }

    /**
     * Get the main controller instance (singleton).
     *
     * @return The main controller instance.
     */
    public static IlunCryptController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bundle = LanguageManager.getInstance().getBundle();
        dialog.setOwnerNode(contentPane);
        initializeUIComponents();
        registerViews();
        initializeLoader();
        Platform.runLater(() -> {
            Language savedLanguage = ConfigManager.getApplicationLanguage();
            languageComboBox.setValue(null);
            languageComboBox.requestLayout();
            languageComboBox.setValue(savedLanguage.getDisplayName());
        });
    }

    /**
     * Initialize UI components and listeners.
     */
    private void initializeUIComponents() {
        setupLanguageComboBox();
        setupThemeToggle();
        enableTopBorderDrag();
        setupTooltips();
        setupLogo();
        setupWindowControls();
        sidebar.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.2));
        rootPane.widthProperty().addListener((observable, oldValue, newValue) -> adjustContentPaneAnchors(newValue.doubleValue(), rootPane.getHeight()));
        rootPane.heightProperty().addListener((observable, oldValue, newValue) -> adjustContentPaneAnchors(rootPane.getWidth(), newValue.doubleValue()));
    }


    /**
     * Setup the views loader and navigation system.
     */
    private void initializeLoader() {
        loader.setOnLoadedAction(beans -> {
            // Process each bean and handle nodes safely
            List<ToggleButton> nodes = beans.stream()
                    .map(bean -> {
                        // Check if a node mapper exists and returns a valid node
                        if (bean.getBeanToNodeMapper() != null) {
                            ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                            if (toggle != null) {
                                toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
                                if (bean.isDefaultView()) {
                                    contentPane.getChildren().setAll(bean.getRoot());
                                    toggle.setSelected(true);
                                }
                                return toggle;
                            }
                        }
                        return null; // Return null if no valid node exists
                    })
                    .filter(toggle -> toggle != null) // Filter out null nodes
                    .toList();

            // Add valid nodes to the navigation bar
            navBar.getChildren().setAll(nodes);

            // Automatically load the default root view if specified
            beans.stream()
                    .filter(MFXLoaderBean::isDefaultView)
                    .findFirst()
                    .ifPresent(bean -> contentPane.getChildren().setAll(bean.getRoot()));
        });

        loader.start();
    }


    /**
     * Register application views with their respective FXML files.
     */
    private void registerViews() {
        loader.addView(MFXLoaderBean.of("CLASSIC-OPTIONS", ResourcesLoader.loadURL("views/classic/classic-ciphers-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Classical Ciphers")).setDefaultRoot(true).get());
        loader.addView(MFXLoaderBean.of("SYMMETRIC-KEY-ENCRYPTION", ResourcesLoader.loadURL("views/symmetric-key/symmetric-key-encryption-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Symmetric-Key Encryption")).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("PUBLIC-KEY-ENCRYPTION", ResourcesLoader.loadURL("views/public-key/public-key-encryption-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Public-Key Encription")).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("IMAGE-ENCRYPTION", ResourcesLoader.loadURL("views/image-encryption/image-encryption-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Image Encryption")).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("DIGITAL-SIGNATURE", ResourcesLoader.loadURL("views/digital-signature/digital-signature-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Digital Signature")).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("CRIPTANALYSIS-OPTIONS", ResourcesLoader.loadURL("views/cryptanalysis/cryptanalysis-options-view.fxml")).setBeanToNodeMapper(() -> createToggle("fas-circle-dot", "Cryptanalysis")).setDefaultRoot(false).get());

        // Classic Ciphers
        loader.addView(MFXLoaderBean.of("AFFINE-CIPHER", ResourcesLoader.loadURL("views/classic/affine/affine-cipher-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new AffineCipherController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("MULTIPLICATIVE-CIPHER", ResourcesLoader.loadURL("views/classic/multiplicative/multiplicative-cipher-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new MultiplicativeCipherController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("SHIFT-CIPHER", ResourcesLoader.loadURL("views/classic/shift/shift-cipher-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new ShiftCipherController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("HILL-CIPHER", ResourcesLoader.loadURL("views/classic/hill/hill-cipher-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new HillCipherController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("PERMUTATION-CIPHER", ResourcesLoader.loadURL("views/classic/permutation/permutation-cipher-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new PermutationCipherController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("SUBSTITUTION-CIPHER", ResourcesLoader.loadURL("views/classic/substitution/substitution-cipher-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new SubstitutionCipherController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("VIGENERE-CIPHER", ResourcesLoader.loadURL("views/classic/vigenere/vigenere-cipher-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new VigenereCipherController(stage)).setDefaultRoot(false).get());

        // Symmetric-key Encryption
        loader.addView(MFXLoaderBean.of("AES", ResourcesLoader.loadURL("views/symmetric-key/aes/aes-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new AESController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("DES", ResourcesLoader.loadURL("views/symmetric-key/des/des-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new DESController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("SDES", ResourcesLoader.loadURL("views/symmetric-key/sdes/sdes-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new SDESController(stage)).setDefaultRoot(false).get());

        // Public-key Encryption
        loader.addView(MFXLoaderBean.of("ELGAMAL-ENCRYPTION", ResourcesLoader.loadURL("views/public-key/elgamal/elgamal-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new ElGamalController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("MENEZES-VANSTONE-ENCRYPTION", ResourcesLoader.loadURL("views/public-key/menezes-vanstone/menezes-vanstone-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new MenezesVanstoneController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("RABIN-ENCRYPTION", ResourcesLoader.loadURL("views/public-key/rabin/rabin-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new RabinController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("RSA-ENCRYPTION", ResourcesLoader.loadURL("views/public-key/rsa/rsa-view.fxml"))
                .setBeanToNodeMapper(() -> null).setControllerFactory(c -> new RSAController(stage)).setDefaultRoot(false).get());

        // Image Encryption
        loader.addView(MFXLoaderBean.of("AES-IMAGE", ResourcesLoader.loadURL("views/image-encryption/aes/aes-image-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new AESImageController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("DES-IMAGE", ResourcesLoader.loadURL("views/image-encryption/des/des-image-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new DESImageController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("HILL-IMAGE", ResourcesLoader.loadURL("views/image-encryption/hill/hill-image-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new HillImageController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("PERMUTATION-IMAGE", ResourcesLoader.loadURL("views/image-encryption/permutation/permutation-image-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new PermutationImageController(stage)).setDefaultRoot(false).get());

        // Digital Signatures
        loader.addView(MFXLoaderBean.of("DSA-SIGNATURE", ResourcesLoader.loadURL("views/digital-signature/dsa/dsa-signature-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new DSASignatureController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("RSA-SIGNATURE", ResourcesLoader.loadURL("views/digital-signature/rsa/rsa-signature-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new RSASignatureController(stage)).setDefaultRoot(false).get());

        // Cryptanalysis Methods
        loader.addView(MFXLoaderBean.of("FRIEDMAN-ANALYSIS", ResourcesLoader.loadURL("views/cryptanalysis/friedman/friedman-analysis-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new FriedmanAnalysisController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("BRAUER-ANALYSIS", ResourcesLoader.loadURL("views/cryptanalysis/brauer/brauer-analysis-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new BrauerAnalysisController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("BRUTE-FORCE-ANALYSIS", ResourcesLoader.loadURL("views/cryptanalysis/brute-force/brute-force-analysis-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new BruteForceAnalysisController(stage)).setDefaultRoot(false).get());
        loader.addView(MFXLoaderBean.of("FREQUENCY-ANALYSIS", ResourcesLoader.loadURL("views/cryptanalysis/frequency/frequency-analysis-view.fxml")).setBeanToNodeMapper(() -> null)
                .setControllerFactory(c -> new FrequencyAnalysisController(stage)).setDefaultRoot(false).get());
    }

    /**
     * Register a single view.
     *
     * @param id       The unique ID of the view.
     * @param fxmlPath The path to the FXML file.
     */
    private void registerView(String id, String fxmlPath) {
        URL fxmlUrl = ResourcesLoader.loadURL(fxmlPath);
        if (fxmlUrl == null) {
            throw new IllegalArgumentException("FXML file not found at path: " + fxmlPath);
        }
        loader.addView(MFXLoaderBean.of(id, fxmlUrl).setDefaultRoot(false).get());
    }

    /**
     * Load a registered view into the content pane.
     *
     * @param id The unique ID of the view.
     */
    public void loadView(String id) {
        if (contentPane == null) {
            throw new IllegalStateException("contentPane is not initialized. Check the FXML bindings.");
        }

        Optional<MFXLoaderBean> bean = Optional.ofNullable(loader.getView(id));
        if (bean.isEmpty()) {
            throw new IllegalStateException("View not registered with ID: " + id);
        }

        if (bean.get().getRoot() == null) {
            throw new IllegalStateException("Root node for view with ID '" + id + "' is null. Check the FXML file.");
        }

        contentPane.getChildren().setAll(bean.get().getRoot());
    }

    /**
     * Create a toggle button for navigation.
     *
     * @param icon The icon to display.
     * @param text The button text.
     * @return The created toggle button.
     */
    private ToggleButton createToggle(String icon, String text) {
        return createToggle(icon, text, 0);
    }

    /**
     * Create a toggle button for navigation with rotation.
     *
     * @param icon   The icon to display.
     * @param text   The button text.
     * @param rotate Rotation angle for the icon.
     * @return The created toggle button.
     */
    private ToggleButton createToggle(String icon, String text, double rotate) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        if (rotate != 0) wrapper.getIcon().setRotate(rotate);
        return toggleNode;
    }

    /**
     * Setup window control buttons for minimize, maximize, close, and always-on-top.
     */
    private void setupWindowControls() {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());

        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> stage.setIconified(true));

        maximizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> toggleMaximize());

        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean isAlwaysOnTop = !stage.isAlwaysOnTop();
            stage.setAlwaysOnTop(isAlwaysOnTop);
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), isAlwaysOnTop);
        });
    }

    /**
     * Toggle maximize and restore for the application window.
     */
    private void toggleMaximize() {
        if (isMaximized) {
            stage.setMaximized(false);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setWidth(screenBounds.getWidth() * 0.8);
            stage.setHeight(screenBounds.getHeight() * 0.8);
            stage.centerOnScreen();
            maximizeIcon.setDescription("fas-circle-chevron-up");
        } else {
            stage.setMaximized(true);
            maximizeIcon.setDescription("fas-circle-chevron-down");
        }
        isMaximized = !isMaximized;
    }

    /**
     * Setup the application logo and its clipping shape.
     */
    private void setupLogo() {
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

    /**
     * Setup tooltips for window controls.
     */
    private void setupTooltips() {
        Tooltip.install(alwaysOnTopIcon, createTooltip("Toggle Always on Top"));
        Tooltip.install(minimizeIcon, createTooltip("Minimize Window"));
        Tooltip.install(maximizeIcon, createTooltip("Maximize/Restore Window"));
        Tooltip.install(closeIcon, createTooltip("Close Application"));
    }

    /**
     * Create a custom tooltip with standardized delays.
     *
     * @param text The tooltip text.
     * @return The configured tooltip.
     */
    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(100));
        tooltip.setHideDelay(Duration.millis(200));
        tooltip.setShowDuration(Duration.seconds(5));
        return tooltip;
    }

    /**
     * Enable dragging functionality for the top border of the window.
     */
    private void enableTopBorderDrag() {
        windowHeader.setOnMouseMoved(event -> {
            // Change cursor to open hand only if not over buttons
            if (!isOverButton(event)) {
                windowHeader.setCursor(Cursor.OPEN_HAND);
            } else {
                windowHeader.setCursor(Cursor.DEFAULT);
            }
        });

        windowHeader.setOnMousePressed(event -> {
            if (!isOverButton(event)) { // Only allow drag if not clicking on a button
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
                windowHeader.setCursor(Cursor.CLOSED_HAND); // Change to closed hand cursor
            }
        });

        windowHeader.setOnMouseDragged(event -> {
            if (!isOverButton(event)) { // Only drag if not over a button
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        windowHeader.setOnMouseReleased(event -> {
            if (!isOverButton(event)) {
                windowHeader.setCursor(Cursor.OPEN_HAND); // Restore to open hand cursor
            } else {
                windowHeader.setCursor(Cursor.DEFAULT); // Restore to default cursor
            }
        });

        windowHeader.setOnMouseExited(event -> windowHeader.setCursor(Cursor.DEFAULT)); // Restore cursor on exit
    }

    /**
     * Check if the mouse event is over a button in the window header.
     *
     * @param event The mouse event.
     * @return True if the mouse is over a button, false otherwise.
     */
    private boolean isOverButton(MouseEvent event) {
        return alwaysOnTopIcon.getBoundsInParent().contains(event.getX(), event.getY()) ||
                minimizeIcon.getBoundsInParent().contains(event.getX(), event.getY()) ||
                maximizeIcon.getBoundsInParent().contains(event.getX(), event.getY()) ||
                closeIcon.getBoundsInParent().contains(event.getX(), event.getY());
    }

    /**
     * Setup the theme toggle functionality.
     */
    private void setupThemeToggle() {
        themeToggle.setText("Light Theme");
        themeToggle.setOnAction(event -> {
            if (themeToggle.isSelected()) {
                themeToggle.setText("Dark Theme");
                rootPane.getStylesheets().clear();
                rootPane.getStylesheets().add(ResourcesLoader.load("/styles/DarkTheme.css"));
            } else {
                themeToggle.setText("Light Theme");
                rootPane.getStylesheets().clear();
                rootPane.getStylesheets().add(ResourcesLoader.load("/styles/LightTheme.css"));
            }
        });
    }

    /**
     * Setup the language selection combo box.
     */
    private void setupLanguageComboBox() {
        for (Language lang : Language.values()) {
            languageComboBox.getItems().add(lang.getDisplayName());
        }

        // 🔹 Seleccionar idioma guardado
        Language savedLanguage = ConfigManager.getApplicationLanguage();
        languageComboBox.setValue(savedLanguage.getDisplayName());

        languageComboBox.setOnAction(event -> {
            String selected = languageComboBox.getValue();
            Language newLanguage = Language.fromDisplayName(selected);
            setLanguage(newLanguage);
        });
    }

    /**
     * Changes the application language and updates the configuration.
     *
     * @param language The new application language (enum).
     */
    private void setLanguage(Language language) {
        if (ConfigManager.getApplicationLanguage() == language) return;
        LanguageManager.getInstance().setLanguage(language.getCode());
        this.bundle = LanguageManager.getInstance().getBundle();
        ConfigManager.setApplicationLanguage(language);
    }

    private void adjustContentPaneAnchors(double width, double height) {
        double leftAnchor = width * 0.225;
        double rightAnchor = width * 0.02;
        double topAnchor = height * 0.06;
        double bottomAnchor = height * 0.02;

        AnchorPane.setLeftAnchor(contentPane, leftAnchor);
        AnchorPane.setRightAnchor(contentPane, rightAnchor);
        AnchorPane.setTopAnchor(contentPane, topAnchor);
        AnchorPane.setBottomAnchor(contentPane, bottomAnchor);
    }

    @FXML
    public void openManageAlphabetsDialog() {
        dialog.enableDynamicSize(0.6, 0.6); // Enable dynamic size (60% of the main window)
        dialog.showFXMLDialog(
                "Manage Alphabets",
                "views/manage-alphabet-view.fxml",
                new MFXFontIcon("fas-gear", 18),
                "mfx-dialog",
                false,
                false,
                null
        );
    }

    public void importIlunMessage(ActionEvent actionEvent) {
        dialog.showFXMLDialog(
                "Select an Ilun file",
                "views/import-ilun-dialog-view.fxml",
                new MFXFontIcon("fas-gear", 18),
                "mfx-dialog",
                false,
                false,
                null
        );
    }
}
