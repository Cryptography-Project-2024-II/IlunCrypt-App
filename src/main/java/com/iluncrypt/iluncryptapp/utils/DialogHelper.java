package com.iluncrypt.iluncryptapp.utils;

import com.iluncrypt.iluncryptapp.ResourcesLoader;
import com.iluncrypt.iluncryptapp.controllers.OtherSettingsController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DialogHelper {

    private final Stage stage;
    private Pane ownerNode;
    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;

    public DialogHelper(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the owner node for the dialog and initializes dialog components.
     *
     * @param ownerNode The owner node (must be a Pane or derived type).
     */
    public void setOwnerNode(Pane ownerNode) {
        this.ownerNode = ownerNode;

        Platform.runLater(() -> {
            dialogContent = MFXGenericDialogBuilder.build()
                    .makeScrollable(true)
                    .get();

            dialog = MFXGenericDialogBuilder.build(dialogContent)
                    .toStageDialogBuilder()
                    .initOwner(stage)
                    .setOwnerNode(ownerNode)
                    .initModality(Modality.APPLICATION_MODAL)
                    .setScrimPriority(ScrimPriority.WINDOW)
                    .setScrimOwner(true)
                    .get();
        });
    }

    /**
     * Applies custom changes to the dialog before showing it.
     *
     * @param changes A Consumer that defines the changes to apply to the dialog.
     */
    public void applyDialogChanges(Consumer<MFXStageDialog> changes) {
        Platform.runLater(() -> {
            if (dialog == null) {
                throw new IllegalStateException("DialogHelper is not initialized yet.");
            }
            changes.accept(dialog);
        });
    }

    /**
     * Displays a basic information dialog with the specified title and content.
     *
     * @param title   The title of the dialog.
     * @param content The message content.
     */
    public void showInfoDialog(String title, String content) {
        Platform.runLater(() -> {
            if (dialogContent == null || dialog == null) {
                throw new IllegalStateException("DialogHelper is not initialized yet.");
            }

            dialogContent.setHeaderText(title);
            dialogContent.setContentText(content);
            dialogContent.setHeaderIcon(new MFXFontIcon("fas-circle-info", 18));
            dialogContent.getStyleClass().add("mfx-info-dialog");
            dialogContent.clearActions();
            dialogContent.setShowMinimize(false);
            dialogContent.setShowAlwaysOnTop(false);
            dialog.setDraggable(false);
            dialog.showDialog();
        });
    }

    /**
     * Displays a custom dialog with user-defined changes, including optional body content.
     *
     * @param title         The title of the dialog.
     * @param content       The message content (optional, can be null if customContent is used).
     * @param customContent Custom content to display in the body of the dialog (optional, can be null).
     * @param icon          The icon to display in the dialog header.
     * @param styleClass    The CSS style class for the dialog.
     * @param movable       Whether the dialog can be dragged.
     */
    public void showCustomDialog(String title, String content, Node customContent, MFXFontIcon icon, String styleClass, boolean movable) {
        Platform.runLater(() -> {
            if (dialogContent == null || dialog == null) {
                throw new IllegalStateException("DialogHelper is not initialized yet.");
            }

            dialogContent.setHeaderText(title);

            // Add either the message content or the custom body content
            if (customContent != null) {
                dialogContent.setContent(customContent);
            } else {
                dialogContent.setContentText(content);
            }

            dialogContent.setHeaderIcon(icon);
            dialogContent.getStyleClass().add(styleClass);
            dialogContent.clearActions();
            dialogContent.addActions(
                    createAction(new MFXButton("Close"), event -> dialog.close())
            );

            dialog.setDraggable(movable);
            dialog.showDialog();
        });
    }

    public <T> void showFXMLDialog(String title, String fxmlPath, MFXFontIcon icon, String styleClass, boolean movable, boolean actions, Consumer<T> controllerConsumer) {
        loadAndShowDialog(title, fxmlPath, null, icon, styleClass, movable, actions, controllerConsumer);
    }

    public <T> void showFXMLDialog(String title, String fxmlPath, Supplier<T> controllerFactory, MFXFontIcon icon, String styleClass, boolean movable, boolean actions, Consumer<T> controllerConsumer) {
        loadAndShowDialog(title, fxmlPath, controllerFactory, icon, styleClass, movable, actions, controllerConsumer);
    }

    private <T> void loadAndShowDialog(String title, String fxmlPath, Supplier<T> controllerFactory, MFXFontIcon icon, String styleClass, boolean movable, boolean actions, Consumer<T> controllerConsumer) {
        Platform.runLater(() -> {
            if (dialogContent == null || dialog == null) {
                throw new IllegalStateException("DialogHelper is not initialized yet.");
            }

            FXMLLoader loader = new FXMLLoader(ResourcesLoader.loadURL(fxmlPath));

            // Si se proporciona un factory, se usa para inyectar el controlador
            if (controllerFactory != null) {
                loader.setControllerFactory(param -> controllerFactory.get());
            }

            Node customContent;
            try {
                customContent = loader.load();
                T controller = loader.getController();

                if (controllerConsumer != null && controller != null) {
                    controllerConsumer.accept(controller);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load FXML file: " + fxmlPath, e);
            }

            dialogContent.setHeaderText(title);
            dialogContent.setContent(customContent);
            dialogContent.setHeaderIcon(icon);
            dialogContent.getStyleClass().add(styleClass);
            dialogContent.clearActions();
            dialogContent.setShowMinimize(false);
            dialogContent.setShowAlwaysOnTop(false);
            if (actions) {
                dialogContent.addActions(
                        createAction(new MFXButton("Close"), event -> dialog.close())
                );
            }

            dialog.setDraggable(movable);
            dialog.showDialog();
        });
    }




    /**
     * Provides access to the MFXGenericDialog for external modifications.
     *
     * @return The MFXGenericDialog instance.
     */
    public MFXGenericDialog getDialogContent() {
        return dialogContent;
    }

    /**
     * Creates a button-action pair compatible with MFXGenericDialog.
     *
     * @param button The button to add.
     * @param action The action to associate with the button.
     * @return A Map.Entry containing the button and its action.
     */
    private Map.Entry<Node, javafx.event.EventHandler<MouseEvent>> createAction(MFXButton button, javafx.event.EventHandler<MouseEvent> action) {
        button.setOnMouseClicked(action);
        return new AbstractMap.SimpleEntry<>(button, action);
    }

    /**
     * Closes the currently active dialog if it exists.
     */
    public void closeDialog() {
        Platform.runLater(() -> {
            if (dialog != null) {
                dialog.close();
            }
        });
    }

}
