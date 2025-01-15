package com.iluncrypt.iluncryptapp.utils;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;

public class Dialog {

    private final MFXGenericDialog dialogContent;
    private final MFXStageDialog dialog;

    public Dialog(Stage owner, String contentText, String title) {
        this.dialogContent = MFXGenericDialogBuilder.build()
                .setContentText(contentText)
                .makeScrollable(true)
                .get();
        this.dialog = MFXGenericDialogBuilder.build(dialogContent)
                .toStageDialogBuilder()
                .initOwner(owner)
                .initModality(Modality.APPLICATION_MODAL)
                .setDraggable(true)
                .setTitle(title)
                .setScrimPriority(ScrimPriority.WINDOW)
                .setScrimOwner(true)
                .get();

        configureDefaultActions();
    }

    /**
     * Configura las acciones predeterminadas para el diálogo.
     */
    private void configureDefaultActions() {
        dialogContent.addActions(
                Map.entry(createButton("Confirm", () -> System.out.println("Confirmed")), null),
                Map.entry(createButton("Cancel", dialog::close), null)
        );
    }

    /**
     * Crea un botón con un texto y una acción asociada.
     *
     * @param text   Texto del botón.
     * @param action Acción al hacer clic en el botón.
     * @return Botón configurado.
     */
    private MFXButton createButton(String text, Runnable action) {
        MFXButton button = new MFXButton(text);
        button.setOnAction(event -> action.run());
        return button;
    }

    /**
     * Muestra un diálogo de información.
     *
     * @param title Título del diálogo.
     * @param content Contenido del diálogo.
     */
    public void showInfoDialog(String title, String content) {
        updateHeader("Information", "fas-circle-info");
        updateContentText(content);
        updateTitle(title);
        dialog.showDialog();
    }

    /**
     * Muestra un diálogo de advertencia.
     *
     * @param title Título del diálogo.
     * @param content Contenido del diálogo.
     */
    public void showWarningDialog(String title, String content) {
        updateHeader("Warning", "fas-circle-exclamation");
        updateContentText(content);
        updateTitle(title);
        dialog.showDialog();
    }

    /**
     * Muestra un diálogo de error.
     *
     * @param title Título del diálogo.
     * @param content Contenido del diálogo.
     */
    public void showErrorDialog(String title, String content) {
        updateHeader("Error", "fas-circle-xmark");
        updateContentText(content);
        updateTitle(title);
        dialog.showDialog();
    }

    /**
     * Actualiza el encabezado del diálogo.
     *
     * @param headerText Texto del encabezado.
     * @param icon       Icono para mostrar en el encabezado.
     */
    private void updateHeader(String headerText, String icon) {
        dialogContent.setHeaderText(headerText);
        dialogContent.setHeaderIcon(new MFXFontIcon(icon, 18));
    }

    /**
     * Actualiza el texto del contenido del diálogo.
     *
     * @param contentText Nuevo contenido.
     */
    public void updateContentText(String contentText) {
        dialogContent.setContentText(contentText);
    }

    /**
     * Actualiza el título del diálogo.
     *
     * @param title Nuevo título.
     */
    public void updateTitle(String title) {
        dialog.setTitle(title);
    }
}
