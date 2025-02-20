module com.iluncrypt.iluncryptapp {
    requires MaterialFX;
    requires javafx.controls;
    requires javafx.fxml;
    requires fr.brouillard.oss.cssfx;
    requires jlatexmath;
    requires java.logging;
    requires com.google.gson;

    opens com.iluncrypt.iluncryptapp to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.models.enums to com.google.gson;
    opens com.iluncrypt.iluncryptapp.utils to com.google.gson;
    exports com.iluncrypt.iluncryptapp.models.enums.symmetrickey.aes to com.google.gson;

    exports com.iluncrypt.iluncryptapp;
    opens com.iluncrypt.iluncryptapp.controllers.classic to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.symmetrickey to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.cryptanalysis to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.digitalsignature to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.publickey to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.imageencryption to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.classic.affine to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.classic.hill to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.classic.multiplicative to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.classic.permutation to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.classic.shift to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.classic.substitution to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.classic.vigenere to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.cryptanalysis.brauer to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.cryptanalysis.bruteforce to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.cryptanalysis.frequency to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.cryptanalysis.friedman to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.digitalsignature.rsa to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.digitalsignature.dsa to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.imageencryption.hill to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.imageencryption.permutation to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.imageencryption.aes to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.imageencryption.des to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.publickey.rsa to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.publickey.elgamal to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.publickey.menezesvanstone to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.publickey.rabin to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.symmetrickey.aes to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.symmetrickey.des to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers.symmetrickey.sdes to javafx.fxml;
}