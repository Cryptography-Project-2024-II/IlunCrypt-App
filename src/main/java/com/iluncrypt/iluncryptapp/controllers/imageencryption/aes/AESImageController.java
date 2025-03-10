package com.iluncrypt.iluncryptapp.controllers.imageencryption.aes;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.ContainerDialogController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.controllers.symmetrickey.aes.AdvancedOptionsController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.symmetrickey.AESManager;
import com.iluncrypt.iluncryptapp.models.SymmetricKeyConfig;
import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.*;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import com.iluncrypt.iluncryptapp.utils.config.ConfigManager;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunKeyManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Controller for AES encryption.
 */
public class AESImageController implements CipherController, Initializable {


    private SymmetricKeyConfig aesConfig;
    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final DialogHelper advancedOptionsDialog;
    private final DialogHelper alertDialog;
    private final Stage stage;
    private File decryptedFile;
    private File encryptedFile;
    private byte[] encryptedInfo;
    private byte[] decryptedInfo;
    private byte[] iv;


    @FXML
    private AnchorPane encryptedImageContainer, unencryptedImageContainer;
    @FXML
    private ImageView unencryptedImageView, encryptedImageView;
    @FXML
    private MFXButton btnAdvancedOptions;
    @FXML
    private MFXButton btnBack;
    @FXML
    private MFXButton btnClear;
    @FXML
    private VBox boxIV;
    @FXML
    private GridPane grid;
    @FXML
    private TextArea textAreaPathUnencryptedImage, textAreaPathEncryptedImage;
    @FXML
    private MFXTextField textFieldKey, textFieldIV;
    @FXML
    private MFXButton btnEncrypt, btnDecrypt;
    @FXML
    private MFXButton btnCopyUnencryptedImage, btnCopyEncryptedImage, btnCopyKey, btnCopyIV;
    @FXML
    private MFXButton btnShuffleIV, btnDeleteIV, btnDownloadIV, btnUploadIV;
    @FXML
    private MFXButton btnShuffleKey, btnDownloadKey, btnUploadKey, btnDeleteKey;
    @FXML
    private MFXButton btnImportUnencryptedImage, btnImportEncryptedImage;
    @FXML
    private MFXButton btnClearUnencryptedImage, btnClearEncryptedImage;
    @FXML
    private MFXButton btnSaveUnencryptedImage;
    @FXML
    private MFXButton btnSaveEncryptedImage;
    @FXML
    private Label lblMode;

    public AESImageController(Stage stage) {
        this.stage = stage;

        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
        this.advancedOptionsDialog = new DialogHelper(stage);
        this.alertDialog = new DialogHelper(stage);

        this.aesConfig = ConfigManager.loadSymmetricKeyConfig("AES");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDialogs();
        loadAESConfig();
        setupButtonActions();
    }

    private void loadAESConfig() {
        aesConfig.setPaddingScheme(PaddingScheme.NO_PADDING);
        aesConfig.setShowIV(true);
        aesConfig.setGenerateKey(true);
        aesConfig.setSaveAlgorithm(true);
        aesConfig.setGenerateIV(true);
        aesConfig.setKeySize(KeySize.AES_256);
        aesConfig.setMode(SymmetricKeyMode.CTR);
        aesConfig.setGCMTagSize(null);
        aesConfig.setAlgorithm(SymmetricKeyAlgorithm.AES);
        lblMode.setText("Mode: "+aesConfig.getTransformation());

        boxIV.setVisible(aesConfig.isShowIV());
        boxIV.setManaged(aesConfig.isShowIV());
    }

    private void configureDialogs() {
        infoDialog.setOwnerNode(grid);
        changeMethodDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);
        advancedOptionsDialog.setOwnerNode(grid);
        alertDialog.setOwnerNode(grid);
    }

    /**
     * Configures button actions for copying, regenerating, and importing files.
     */
    private void setupButtonActions() {
        btnBack.setOnAction(e -> handleBackButton());
        btnCopyUnencryptedImage.setOnAction(e -> copyToClipboard(textAreaPathUnencryptedImage.getText()));
        btnCopyEncryptedImage.setOnAction(e -> copyToClipboard(textAreaPathEncryptedImage.getText()));
        btnCopyKey.setOnAction(e -> copyToClipboard(textFieldKey.getText()));
        btnCopyIV.setOnAction(e -> copyToClipboard(textFieldIV.getText()));

        btnShuffleKey.setOnAction(e -> regenerateKey());
        btnUploadKey.setOnAction(e -> uploadKey());
        btnDownloadKey.setOnAction(e -> downloadKey());
        btnDeleteKey.setOnAction(e -> deleteKey());

        btnShuffleIV.setOnAction(e -> regenerateIV());
        btnUploadIV.setOnAction(e -> uploadIV());
        btnDownloadIV.setOnAction(e -> downloadIV());
        btnDeleteIV.setOnAction(e -> deleteIV());

        btnClearUnencryptedImage.setOnAction(e -> clearDecryptArea());
        btnClearEncryptedImage.setOnAction(e -> clearEncryptArea());
        btnClear.setOnAction(e -> clearAll());

        btnImportUnencryptedImage.setOnAction(e -> importNoEncryptedFile());
        btnImportEncryptedImage.setOnAction(e -> importEncryptedFile());
        btnSaveUnencryptedImage.setOnAction(e -> saveDecryptedInformation());
        btnSaveEncryptedImage.setOnAction(e -> saveEncryptedInformation());

        btnEncrypt.setOnAction(e -> encrypt());
        btnDecrypt.setOnAction(e -> decrypt());

        btnAdvancedOptions.setOnAction(e->showAdvancedOptions());
    }

    private void saveEncryptedInformation() {
        // Verificar que exista una imagen encriptada
        if (encryptedImageView.getImage() == null) {
            showError("No encrypted image available to save.");
            return;
        }

        // Forzar que el guardado de imagen encriptada sea solo en PNG
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Encrypted Image");
        // Sólo se permite PNG
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        fileChooser.setInitialFileName("encrypted_image.png");
        File fileToSave = fileChooser.showSaveDialog(stage);
        if (fileToSave == null) {
            return; // Usuario canceló la operación.
        }

        try {
            // Convertir la imagen JavaFX a BufferedImage
            javafx.scene.image.Image fxImage = encryptedImageView.getImage();
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

            // Obtener el IV desde el campo y verificar que no esté vacío
            String ivBase64 = textFieldIV.getText().trim();
            if (ivBase64.isEmpty()) {
                showError("No IV available to embed.");
                return;
            }

            // Preparar el ImageWriter para PNG
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
            if (!writers.hasNext()) {
                showError("No PNG ImageWriter found.");
                return;
            }
            ImageWriter writer = writers.next();

            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            String nativeFormat = metadata.getNativeMetadataFormatName();

            // Crear un nodo de metadata para insertar el IV como tEXtEntry
            IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
            textEntry.setAttribute("keyword", "IV");
            textEntry.setAttribute("value", ivBase64);

            IIOMetadataNode textNode = new IIOMetadataNode("tEXt");
            textNode.appendChild(textEntry);

            IIOMetadataNode root = new IIOMetadataNode(nativeFormat);
            root.appendChild(textNode);

            try {
                metadata.mergeTree(nativeFormat, root);
            } catch (IIOInvalidTreeException e) {
                showError("Failed to embed IV metadata: " + e.getMessage());
                return;
            }

            // Escribir la imagen en formato PNG con la metadata incrustada
            ImageOutputStream ios = ImageIO.createImageOutputStream(fileToSave);
            writer.setOutput(ios);
            IIOImage iioImage = new IIOImage(bufferedImage, null, metadata);
            writer.write(null, iioImage, writeParam);
            ios.close();
            writer.dispose();

            infoDialog.showInfoDialog("Success", "Encrypted image saved successfully with IV metadata.");
        } catch (IOException e) {
            showError("Failed to save encrypted image: " + e.getMessage());
        }
    }



    @FXML
    private void saveDecryptedInformation() {
        // Verificar que exista una imagen desencriptada
        if (unencryptedImageView.getImage() == null) {
            showError("No decrypted image available to save.");
            return;
        }

        // Permitir múltiples formatos para la imagen desencriptada
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Decrypted Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Image", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Image", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("BMP Image", "*.bmp")
        );
        fileChooser.setInitialFileName("decrypted_image.png");
        File fileToSave = fileChooser.showSaveDialog(stage);
        if (fileToSave == null) {
            return; // Usuario canceló la operación.
        }

        // Convertir la imagen JavaFX a BufferedImage
        javafx.scene.image.Image fxImage = unencryptedImageView.getImage();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

        try {
            // Determinar el formato a partir de la extensión (por defecto PNG)
            String format = getFileExtension(fileToSave);
            if (format.isEmpty()) {
                format = "png";
            }
            ImageIO.write(bufferedImage, format, fileToSave);
            infoDialog.showInfoDialog("Success", "Decrypted image saved successfully.");
        } catch (IOException e) {
            showError("Failed to save decrypted image: " + e.getMessage());
        }
    }




    /**
     * Gets the extension of a file.
     *
     * @param file File from which to obtain the extension.
     * @return The file extension without the period, or an empty string if it has no extension.
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf(".");

        if (lastIndex == -1 || lastIndex == name.length() - 1) {
            return "";
        }

        return name.substring(lastIndex + 1);
    }

    public static String getNameFile(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf(".");

        if (lastIndex == -1) {
            return name;
        }

        return name.substring(0, lastIndex);
    }


    private void clearEncryptArea() {
        textAreaPathEncryptedImage.clear();
        encryptedImageView.setImage(null);
        encryptedImageContainer.setStyle("-fx-background-color: #D3D3D3;");
        encryptedFile = null;
    }

    private void clearDecryptArea() {
        textAreaPathUnencryptedImage.clear();
        unencryptedImageView.setImage(null);
        unencryptedImageContainer.setStyle("-fx-background-color: #D3D3D3;");
        decryptedFile = null;
    }

    private void deleteIV() {
        textFieldIV.clear();
        iv=null;
    }

    private void downloadIV() {
        try {
            String ivText = textFieldIV.getText().trim();
            if (ivText.isEmpty()) {
                showError("There is no IV available for download.");
                return;
            }
            FileChooser fc = new FileChooser();
            fc.setTitle("Save IV");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("IV (*.iluniv)", "*.iluniv"));
            fc.setInitialFileName("vector" + ".iluniv");
            File file = fc.showSaveDialog(stage);
            if (file == null) {
                return; // Usuario canceló la operación
            }
            // Escribir el contenido del IV en el archivo (texto plano)
            java.nio.file.Files.write(file.toPath(), ivText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            infoDialog.showInfoDialog("Success", "The IV was saved successfully.");
        } catch (Exception e) {
            showError("Error saving IV: " + e.getMessage());
        }
    }

    private void uploadIV() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select IV");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("IV (*.iluniv)", "*.iluniv"));
            File file = fc.showOpenDialog(stage);
            if (file == null) {
                return; // Usuario canceló la operación
            }
            // Leer el archivo como texto (se espera que el IV esté guardado en Base64 o en texto plano)
            String ivText = new String(java.nio.file.Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8).trim();
            if (ivText.isEmpty()) {
                showError("File IV is empty.");
                return;
            }
            // Actualizar el campo con el contenido del IV
            textFieldIV.setText(ivText);
            infoDialog.showInfoDialog("Success", "IV loaded successfully.");
        } catch (Exception e) {
            showError("Error loading IV: " + e.getMessage());
        }
    }


    private void deleteKey() {
        textFieldKey.clear();
    }

    private void downloadKey() {
        try {
            // Se verifica que exista una clave en el campo
            String keyText = textFieldKey.getText().trim();
            if (keyText.isEmpty()) {
                showError("There is no key available to save.");
                return;
            }
            // Convertir la clave (Base64) a bytes
            byte[] keyBytes = Base64.getDecoder().decode(keyText);
            // Guardar la clave en un archivo .ilunk usando el IlunKeyManager
            byte[] fileData = IlunKeyManager.writeUniqueKeyFile(keyBytes, stage);
            if (fileData != null) {
                infoDialog.showInfoDialog("Success", "The key was saved successfully.");
            }
        } catch (Exception e) {
            showError("Error saving key: " + e.getMessage());
        }
    }

    private void uploadKey() {
        try {
            // Abrir el FileChooser para seleccionar un archivo de llave (.ilunpbk, .ilunpvk o .ilunk)
            byte[] fileData = IlunKeyManager.readKeyFile(stage);
            if (fileData == null || fileData.length == 0) {
                showError("No file has been selected or the file is empty.");
                return;
            }
            // Suponiendo que para una clave única, la llave está al principio del archivo y el tamaño
            // de la clave se conoce según la configuración (por ejemplo, 128 bits = 16 bytes, 256 bits = 32 bytes, etc.)
            int keySizeBytes = aesConfig.getKeySize().getSize() / 8;
            if (fileData.length < keySizeBytes) {
                showError("The content of the key file is very short.");
                return;
            }
            // Extraer únicamente la parte correspondiente a la clave (se omite el checksum al final)
            byte[] keyBytes = new byte[keySizeBytes];
            System.arraycopy(fileData, 0, keyBytes, 0, keySizeBytes);
            // Mostrar la clave en el campo (codificada en Base64)
            textFieldKey.setText(Base64.getEncoder().encodeToString(keyBytes));
            infoDialog.showInfoDialog("Success", "The key was loaded successfully.");
        } catch (Exception e) {
            showError("Error loading key: " + e.getMessage());
        }
    }


    /**
     * Copies the given text to the clipboard.
     */
    private void copyToClipboard(String text) {
        if (!text.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(text);
            clipboard.setContent(content);
        }
    }

    /**
     * Regenerates a new random key and updates the key field.
     */
    private void regenerateKey() {
        try {
            SecretKey generatedKey = AESManager.generateKey(aesConfig);
            textFieldKey.setText(Base64.getEncoder().encodeToString(generatedKey.getEncoded()));
        } catch (Exception e) {
            showError("Key generation failed: " + e.getMessage());
        }
    }

    /**
     * Generates a new random IV and updates the IV field.
     */
    private void regenerateIV() {
        System.out.println(aesConfig.getMode().getFixedIVSize());
        byte[] iv = new byte[aesConfig.getMode().getFixedIVSize()];
        new java.security.SecureRandom().nextBytes(iv);
        textFieldIV.setText(Base64.getEncoder().encodeToString(iv));
    }

    public void displayImage(Image image, ImageView imageView) {
        imageView.setImage(image);

        imageView.setOnMouseClicked(event -> {
            if (imageView.getImage() != null) {
                showEnlargedImage(imageView.getImage());
            }
        });
    }

    /**
     * Imports a file for encryption or decryption and locks text fields.
     */
    private void importNoEncryptedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image to Encrypt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        decryptedFile = fileChooser.showOpenDialog(stage);
        if (decryptedFile != null) {
            textAreaPathUnencryptedImage.clear();
            textAreaPathEncryptedImage.clear();
            textFieldKey.clear();
            textFieldIV.clear();

            textAreaPathUnencryptedImage.setText(decryptedFile.getAbsolutePath());
            textAreaPathUnencryptedImage.setEditable(false);
            try {
                Image image = new Image(new FileInputStream(decryptedFile));
                displayImage(image, unencryptedImageView);
                unencryptedImageContainer.setStyle("-fx-background-color: transparent;");
            } catch (FileNotFoundException e) {
                showError("Error loading image: " + e.getMessage());
            }
        }
    }

    /**
     * Imports a file for encryption or decryption and locks text fields.
     */
    private void importEncryptedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image to Decrypt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        encryptedFile = fileChooser.showOpenDialog(stage);

        if (encryptedFile != null) {
            textAreaPathUnencryptedImage.clear();
            textAreaPathEncryptedImage.clear();
            textFieldKey.clear();
            textFieldIV.clear();

            textAreaPathEncryptedImage.setText(encryptedFile.getAbsolutePath());
            textAreaPathEncryptedImage.setEditable(false);
            try {
                Image image = new Image(new FileInputStream(encryptedFile));
                displayImage(image, encryptedImageView);
                encryptedImageContainer.setStyle("-fx-background-color: transparent;");
            } catch (FileNotFoundException e) {
                showError("Error loading image: " + e.getMessage());
            }
        }
    }

    private void showEnlargedImage(Image image) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxWidth = screenBounds.getWidth() * 0.6;
        double maxHeight = screenBounds.getHeight() * 0.6;

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        imageView.setFitWidth(maxWidth);
        imageView.setFitHeight(maxHeight);

        StackPane pane = new StackPane(imageView);
        Scene scene = new Scene(pane);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Expanded view");

        stage.setWidth(maxWidth);
        stage.setHeight(maxHeight);
        stage.show();
    }

    /**
     * Encrypts either a image.
     */
    @FXML
    private void encrypt() {
        try {
            Boolean isValidKey = false;

            String keyText = textFieldKey.getText().trim();

            byte[] keyBytes = null;
            if (!keyText.isEmpty()) {
                try {
                    keyBytes = Base64.getDecoder().decode(keyText);
                    int keySizeBits = keyBytes.length * 8;

                    if (keySizeBits == aesConfig.getKeySize().getSize()) {
                        isValidKey = true;
                    }
                } catch (IllegalArgumentException e) {
                    showError("Invalid Base64 key format. Please enter a valid key.");
                    return;
                }
            }

            if (!isValidKey && aesConfig.isGenerateKey()) {
                SecretKey generatedKey = AESManager.generateKey(aesConfig);
                keyBytes = generatedKey.getEncoded();
                String base64Key = Base64.getEncoder().encodeToString(keyBytes);
                textFieldKey.setText(base64Key);
            }

            if (textFieldKey.getText().trim().isEmpty()) {
                showError("No key provided.");
                return;
            }

            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            if(aesConfig.getMode().requiresIV()) {
                if (aesConfig.isGenerateIV() && textFieldIV.getText().trim().isEmpty()) {
                    iv = aesConfig.getMode().requiresIV() ? AESManager.generateIV(aesConfig) : null;

                    if (aesConfig.isShowIV() && iv != null) {
                        textFieldIV.setText(Base64.getEncoder().encodeToString(iv));
                    }
                } else {
                    String ivText = textFieldIV.getText().trim();
                    if (ivText.isEmpty()) {
                        showError("No Initial Vector provided.");
                        return;
                    }

                    try {
                        iv = Base64.getDecoder().decode(ivText);

                        int expectedIVSize = aesConfig.getMode().getFixedIVSize();
                        if (expectedIVSize == -1) {
                            expectedIVSize = aesConfig.getAlgorithm().getBaseIVSize();
                        }

                        if (iv.length != expectedIVSize) {
                            showError("Invalid IV size. Expected " + expectedIVSize + " bytes, but got " + iv.length + " bytes.");
                            return;
                        }
                    } catch (IllegalArgumentException e) {
                        showError("Invalid IV format. Please enter a valid Base64-encoded IV.");
                        return;
                    }

                }
            }

            BufferedImage inputImage = ImageIO.read(decryptedFile);
            if (inputImage == null) {
                showError("Could not read image from the selected file.");
                return;
            }

// Encriptar la imagen a nivel de píxeles usando AES/CTR/NoPadding
            BufferedImage encryptedImage = AESManager.encryptImage(inputImage, key, iv);

// Convertir la imagen encriptada a formato JavaFX y actualizar la vista
            javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(encryptedImage, null);
            displayImage(fxImage, encryptedImageView);
            encryptedImageContainer.setStyle("-fx-background-color: transparent;");
            textAreaPathEncryptedImage.setText("Your image was successfully encrypted. You can save it.");
            textAreaPathEncryptedImage.setEditable(false);



        } catch (Exception e) {
            showError("Encryption failed: " + e.getMessage());
        }
    }


    /**
     * Decrypts either a image.
     */
    @FXML
    private void decrypt() {
        try {
            // Verificar que se haya ingresado una clave válida
            String keyText = textFieldKey.getText().trim();
            if (keyText.isEmpty()) {
                showError("No key provided.");
                return;
            }

            byte[] keyBytes;
            try {
                keyBytes = Base64.getDecoder().decode(keyText);
            } catch (IllegalArgumentException e) {
                showError("Invalid Base64 key format. Please enter a valid key.");
                return;
            }

            SecretKey key = new SecretKeySpec(keyBytes, "AES");

            // Leer la imagen encriptada desde el archivo
            BufferedImage encryptedImage = ImageIO.read(encryptedFile);
            if (encryptedImage == null) {
                showError("Could not read encrypted image from the selected file.");
                return;
            }


            // Leer el IV desde la metadata del archivo PNG
            String ivBase64 = null;
            try (ImageInputStream iis = ImageIO.createImageInputStream(encryptedFile)) {
                Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");
                if (!readers.hasNext()) {
                    showError("No PNG reader available.");
                    return;
                }
                ImageReader reader = readers.next();
                reader.setInput(iis, true);
                IIOMetadata metadata = reader.getImageMetadata(0);
                String nativeFormat = metadata.getNativeMetadataFormatName();
                Node root = metadata.getAsTree(nativeFormat);

                // Recorrer el árbol de metadata para encontrar el nodo tEXtEntry con keyword "IV"
                ivBase64 = null;
                NodeList nodeList = root.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    ivBase64 = searchForIV(nodeList.item(i));
                    if (ivBase64 != null) {
                        break;
                    }
                }
                reader.dispose();
            } catch (IOException e) {
                showError("Error reading image metadata: " + e.getMessage());
                return;
            }

            if (ivBase64 == null || ivBase64.isEmpty()) {
                showError("IV not found in image metadata.");
                return;
            }

// Actualizar el campo IV en la interfaz
            textFieldIV.setText(ivBase64);

// Decodificar el IV
            byte[] iv;
            try {
                iv = Base64.getDecoder().decode(ivBase64);
            } catch (IllegalArgumentException e) {
                showError("Invalid IV format in metadata.");
                return;
            }

// Desencriptar la imagen usando el IV obtenido
            BufferedImage decryptedImage = AESManager.decryptImage(encryptedImage, key, iv);
            javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(decryptedImage, null);
            displayImage(fxImage, unencryptedImageView);
            unencryptedImageView.setStyle("-fx-background-color: transparent;");
            textAreaPathUnencryptedImage.setText("Your image was successfully decrypted. You can save it.");
            textAreaPathUnencryptedImage.setEditable(false);



        } catch (Exception e) {
            showError("Decryption failed: " + e.getMessage());
        }
    }

    private String searchForIV(Node node) {
        if (node.getNodeName().equals("tEXtEntry")) {
            NamedNodeMap attributes = node.getAttributes();
            Node keywordNode = attributes.getNamedItem("keyword");
            Node valueNode = attributes.getNamedItem("value");
            if (keywordNode != null && "IV".equals(keywordNode.getNodeValue()) && valueNode != null) {
                return valueNode.getNodeValue();
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            String result = searchForIV(children.item(i));
            if (result != null) {
                return result;
            }
        }
        return null;
    }



    /**
     * Displays an error message in an alert dialog.
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            errorDialog.showInfoDialog("Error", message);
        });
    }

    @Override
    public void saveCurrentState() {

    }

    @Override
    public void restorePreviousState() {

    }

    @Override
    public void switchEncryptionMethod(String methodView) {

    }

    @Override
    public void closeDialog(DialogHelper dialog) {
        dialog.closeDialog();
        lblMode.setText("Mode: "+aesConfig.getTransformation());
        boxIV.setVisible(aesConfig.isShowIV());
        boxIV.setManaged(aesConfig.isShowIV());
        if(!aesConfig.isShowIV()) {
            textFieldIV.clear();
        }else if(iv != null){
            textFieldIV.setText(Base64.getEncoder().encodeToString(iv));
        }

    }

    @Override
    public void setConfig(CryptosystemConfig config) {

    }

    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("IMAGE-ENCRYPTION");
    }

    @FXML
    private void showInfoDialog() {
        infoDialog.enableDynamicSize(0.6, 0.6);

        infoDialog.showFXMLDialog(
                "Advanced Encryption Standard (AES) Information",
                "views/container-dialog-view.fxml",  // Load the container
                ContainerDialogController::new,
                new MFXFontIcon("fas-circle-info", 18),
                "mfx-dialog",
                false,
                false,
                controller -> {
                    ResourceBundle bundle = LanguageManager.getInstance().getBundle();
                    controller.loadContent("views/symmetric-key/aes/aes-description-view.fxml", bundle); // Load the description view inside the container
                }
        );
    }

    public void showChangeMethodDialog(ActionEvent actionEvent) {
    }

    @FXML
    private void clearAll() {
        Platform.runLater(() -> {
            alertDialog.applyDialogChanges(dialog -> {
                alertDialog.getDialogContent().clearActions();
                alertDialog.getDialogContent().setHeaderText("Clear All");
                alertDialog.getDialogContent().setContentText("Are you sure you want to clear all fields? This action cannot be undone.");
                alertDialog.getDialogContent().setHeaderIcon(new MFXFontIcon("fas-circle-exclamation", 18));

                MFXButton btnConfirm = new MFXButton("Yes");
                btnConfirm.getStyleClass().add("mfx-primary");
                btnConfirm.setOnAction(event -> {
                    clearDecryptArea();
                    clearEncryptArea();
                    textFieldKey.clear();
                    textFieldIV.clear();

                    // Cierra el diálogo
                    alertDialog.closeDialog();
                    textAreaPathEncryptedImage.setEditable(true);
                    textAreaPathUnencryptedImage.setEditable(true);
                    textFieldKey.setEditable(true);
                    textFieldIV.setEditable(true);
                });

                MFXButton btnCancel = new MFXButton("Cancel");
                btnCancel.setOnAction(event -> alertDialog.closeDialog());

                // Agregar botones al diálogo
                alertDialog.getDialogContent().addActions(btnConfirm, btnCancel);
            });

            // Muestra el diálogo
            alertDialog.getDialogContent().setShowMinimize(false);
            alertDialog.getDialogContent().setShowAlwaysOnTop(false);
            alertDialog.getDialogContent().getStyleClass().add("mfx-warning-dialog");
            alertDialog.getDialog().setDraggable(false);
            alertDialog.getDialog().showDialog();
        });
    }

    private void showAdvancedOptions() {
        advancedOptionsDialog.showFXMLDialog(
                "Advanced Options (AES)",
                "views/symmetric-key/aes/advanced-options-view.fxml",
                () -> new AdvancedOptionsController(stage, aesConfig,advancedOptionsDialog),
                new MFXFontIcon("fas-gear", 18),
                "mfx-dialog",
                false,
                false,
                controller -> {
                    controller.setParentController(this);
                }
        );

    }
}
