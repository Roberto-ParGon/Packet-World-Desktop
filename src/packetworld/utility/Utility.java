package packetworld.utility;

import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author Lenovo
 */
public class Utility {

    private static final String STYLESHEET = "/packetworld/resources/styles/styles.css";

    public static Stage createTransparentModalStage(Parent root) {
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    public static void animateEntrance(Node node) {
        ZoomIn zoomIn = new ZoomIn(node);
        zoomIn.setSpeed(1.4);
        zoomIn.play();
    }

    public static void animateExit(Node node, Stage stageToClose) {
        ZoomOut zoomOut = new ZoomOut(node);
        zoomOut.setSpeed(1.7);
        zoomOut.setOnFinished(e -> stageToClose.close());
        zoomOut.play();
    }

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

    public static boolean createAlert(String title, String message, NotificationType type) {
        Alert alert = buildAlert(title, message, type);
        configureAlert(alert, type);

        java.util.Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY;
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

    public static <T> void openAnimatedModal(
            String fxmlPath,
            Consumer<T> initializer,
            Predicate<T> successChecker,
            Function<T, String> messageProvider) {

        try {
            FXMLLoader loader = new FXMLLoader(Utility.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            if (initializer != null) {
                initializer.accept(controller);
            }

            Stage stage = createTransparentModalStage(root);
            animateEntrance(root);
            stage.showAndWait();

            if (successChecker.test(controller)) {
                String msg = messageProvider.apply(controller);
                createNotification(msg, NotificationType.SUCCESS);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al abrir modal (" + fxmlPath + "): " + ex.getMessage());
        }
    }

}
