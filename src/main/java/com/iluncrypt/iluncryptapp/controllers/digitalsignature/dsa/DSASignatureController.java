package com.iluncrypt.iluncryptapp.controllers.digitalsignature.dsa;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * Controller for DSA digital signature.
 */
public class DSASignatureController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final Stage stage;

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaMessage;

    @FXML
    private TextArea textAreaSignature;

    @FXML
    private MFXTextField textFieldPrivateKey;

    @FXML
    private MFXTextField textFieldPublicKey;

    public DSASignatureController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoDialog.setOwnerNode(grid);
    }



    @Override
    public void saveCurrentState() {}

    @Override
    public void restorePreviousState() {}

    @Override
    public void switchEncryptionMethod(String methodView) {}

    @Override
    public void closeOptionsDialog() {}
}
