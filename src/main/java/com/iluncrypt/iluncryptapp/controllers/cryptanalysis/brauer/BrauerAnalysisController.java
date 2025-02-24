package com.iluncrypt.iluncryptapp.controllers.cryptanalysis.brauer;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.attacks.brauer.BrauerAnalysis;
import com.iluncrypt.iluncryptapp.utils.ConversionUtils;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Brauer Cryptanalysis.
 */
public class BrauerAnalysisController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final Stage stage;
    @FXML
    private MFXComboBox comboModes;
    @FXML
    private MFXButton btnMinus;
    @FXML
    private MFXButton btnPlus;
    @FXML
    private MFXButton btnBack;
    @FXML
    private MFXButton btnInfo;
    @FXML
    private MFXButton btnChangeMethod;
    @FXML
    private MFXButton btnImportText;
    @FXML
    private MFXButton btnClearText;
    @FXML
    private MFXButton btnCopyText;
    @FXML
    private TextArea textAreaText;
    @FXML
    private MFXButton btnPerformAnalysis;
    @FXML
    private MFXButton btnClearAnalysis;
    @FXML
    private MFXButton btnSeeQuiver;
    @FXML
    private MFXButton btnSeeNerve;
    @FXML
    private MFXTextField textFielPoligonSize;
    @FXML
    private MFXButton btnCopyValMulText;
    @FXML
    private TextArea textAreaMulValText;
    @FXML
    private MFXButton btnCopySucSeqText;
    @FXML
    private TextArea textAreaSucSeqText;
    @FXML
    private MFXButton btnCopyDimText;
    @FXML
    private TextArea textAreaDimText;

    @FXML
    private GridPane grid;

    public BrauerAnalysisController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoDialog.setOwnerNode(grid);
        setupButtonActions();
        comboModes.getItems().addAll(
                "Printable Characters",
                "Printable Characters to Binary",
                "Printable Characters to Hex",
                "Printable Characters to Base64",
                "Base64",
                "Base64 to Binary",
                "Base64 to Hex",
                "Hex",
                "Hex to Binary",
                "Hex to Base64",
                "Binary",
                "Binary to Base64",
                "Binary to Hex"
        );
        comboModes.getSelectionModel().selectFirst();

    }

    private void setupButtonActions() {
        btnBack.setOnAction(e -> handleBackButton());
        btnInfo.setOnAction(e -> showInfoDialog());
        btnChangeMethod.setOnAction(e -> showChangeMethodDialog());
        btnImportText.setOnAction(e -> importText());
        btnClearText.setOnAction(e -> clearText());
        btnCopyText.setOnAction(e -> copyText());
        btnPerformAnalysis.setOnAction(e -> PerformAnalysis());
        btnClearAnalysis.setOnAction(e -> clearAnalysis());
        btnSeeQuiver.setOnAction(e -> seeQuiver());
        btnSeeNerve.setOnAction(e -> seeNerve());
        btnPlus.setOnAction(e -> plusPoligonSize());
        btnMinus.setOnAction(e -> minusPoligonSize());
        btnCopyValMulText.setOnAction(e -> copyValText());
        btnCopySucSeqText.setOnAction(e -> copySucSeqText());
        btnCopyDimText.setOnAction(e -> copyDimText());
    }

    private void copyValText() {
        copyToClipboard(textAreaMulValText.getText());
    }

    private void copyDimText() {
        copyToClipboard(textAreaDimText.getText());
    }

    private void copySucSeqText() {
        copyToClipboard(textAreaSucSeqText.getText());
    }

    private void minusPoligonSize() {
        int poligonSize = Integer.parseInt(textFielPoligonSize.getText());
        poligonSize-=1;
        textFielPoligonSize.setText(poligonSize + "");
    }

    private void plusPoligonSize() {
        int poligonSize = Integer.parseInt(textFielPoligonSize.getText());
        poligonSize+=1;
        textFielPoligonSize.setText(poligonSize + "");
    }

    private void seeNerve() {
        BrauerAnalysis.showNerve(textAreaText.getText(),Integer.parseInt(textFielPoligonSize.getText()));
    }

    private void seeQuiver() {
        BrauerAnalysis.showQuiver(textAreaText.getText(),Integer.parseInt(textFielPoligonSize.getText()));
    }

    private void clearAnalysis() {
        textAreaMulValText.clear();
        textAreaSucSeqText.clear();
        textAreaDimText.clear();
    }

    private void PerformAnalysis() {
        String mode = (String) comboModes.getValue(); // e.g. "Printable Characters to Binary"
        String input = textAreaText.getText();
        String result = ConversionUtils.convert(mode, input);
        textAreaDimText.setText(BrauerAnalysis.getDimensionsInfo(result,Integer.parseInt(textFielPoligonSize.getText())));
        textAreaSucSeqText.setText(BrauerAnalysis.getSuccessorSequences(result,Integer.parseInt(textFielPoligonSize.getText())));
        textAreaMulValText.setText(BrauerAnalysis.getVerticesInfo(result,Integer.parseInt(textFielPoligonSize.getText())));
    }

    private void copyText() {
        copyToClipboard(textAreaText.getText());
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

    private void clearText() {
        textAreaText.clear();
    }

    private void importText() {
    }

    private void showChangeMethodDialog() {
    }

    private void showInfoDialog() {
    }

    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("CRIPTANALYSIS-OPTIONS");
    }

    @Override
    public void saveCurrentState() {}

    @Override
    public void restorePreviousState() {}

    @Override
    public void switchEncryptionMethod(String methodView) {}

    @Override
    public void closeDialog(DialogHelper dialog) {}

    @Override
    public void setConfig(CryptosystemConfig config) {}
}
