package packetworld.utility;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author Lenovo
 */
public class Utility {

    private static final String STYLESHEET = "/packetworld/resources/styles/styles.css";

    public static void createNotification(String message, NotificationType type) {
        Label content = new Label(message);
        content.getStyleClass().add("notification-label");
        content.setContentDisplay(ContentDisplay.RIGHT);

        try {
            content.getStylesheets().add(Utility.class.getResource(STYLESHEET).toExternalForm());
        } catch (Exception e) {
            System.err.println("Error al cargar estilos para notificación: " + e.getMessage());
        }

        Image icon = type.getIcon();
        if (icon != null) {
            content.setGraphic(new ImageView(icon));
            content.setGraphicTextGap(22);
        }

        Notifications.create()
                .graphic(content)
                .hideAfter(Duration.seconds(3))
                .hideCloseButton()
                .position(Pos.BOTTOM_RIGHT)
                .show();
    }

    public static void createAlert(String title, String message, NotificationType type) {
        Alert alert = buildAlert(title, message, type);
        configureAlert(alert, type);
        alert.showAndWait();
    }

    private static Alert buildAlert(String title, String message, NotificationType type) {
        Alert alert;
        switch (type) {
            case DELETE:
                ButtonType deleteBtn = new ButtonType("Eliminar", ButtonBar.ButtonData.APPLY);
                ButtonType cancelBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert = new Alert(Alert.AlertType.NONE, message, deleteBtn, cancelBtn);
                styleButton(alert, deleteBtn, "alert-btn-delete");
                styleButton(alert, cancelBtn, "alert-btn-cancel");
                break;
            default:
                ButtonType okBtn = new ButtonType("Entendido", ButtonBar.ButtonData.OK_DONE);
                alert = new Alert(Alert.AlertType.NONE, message, okBtn);
                styleButton(alert, okBtn, "alert-btn-ok");
                break;
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert;
    }

    private static void configureAlert(Alert alert, NotificationType type) {
        alert.getDialogPane().getStylesheets()
                .add(Utility.class.getResource(STYLESHEET).toExternalForm());
        alert.getDialogPane().getStyleClass().add(type.getStyleClass());

        Image icon = type.getIcon();
        if (icon != null) {
            alert.setGraphic(new ImageView(icon));
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(icon);
        }
    }

    private static void styleButton(Alert alert, ButtonType buttonType, String cssClass) {
        Button button = (Button) alert.getDialogPane().lookupButton(buttonType);
        button.getStyleClass().add(cssClass);
    }
}
