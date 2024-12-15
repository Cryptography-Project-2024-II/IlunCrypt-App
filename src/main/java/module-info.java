module com.iluncrypt.iluncryptapp {
    requires MaterialFX;
    requires javafx.controls;
    requires javafx.fxml;
    requires fr.brouillard.oss.cssfx;

    opens com.iluncrypt.iluncryptapp to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers to javafx.fxml;
    exports com.iluncrypt.iluncryptapp;
}