module com.iluncrypt.iluncryptapp {
    requires MaterialFX;
    requires javafx.controls;
    requires javafx.fxml;
    requires fr.brouillard.oss.cssfx;
    requires jlatexmath;
    requires com.google.gson;

    opens com.iluncrypt.iluncryptapp to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.controllers to javafx.fxml;
    opens com.iluncrypt.iluncryptapp.utils to com.google.gson;    exports com.iluncrypt.iluncryptapp;
}