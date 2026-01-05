package packetworld.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import packetworld.pojo.Delivery;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class FXMLDeliveryFormController implements Initializable {

    @FXML private TextField tfTracking;
    @FXML private TextField tfSender;
    @FXML private TextField tfRecipient;
    @FXML private TextField tfOrigin;
    @FXML private TextField tfDestination;
    @FXML private TextField tfWeight;
    @FXML private TextField tfDate;
    @FXML private ComboBox<String> cbStatus;

    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Button btnDelete;

    private final ValidationSupport validationSupport = new ValidationSupport();

    private ObservableList<Delivery> targetList;
    private Delivery currentDelivery;
    private boolean isEditMode = false;

    private static final AtomicInteger ID_GEN = new AtomicInteger(5000);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupValidation();
        cbStatus.getItems().addAll("Pendiente", "Enviado", "Entregado", "Cancelado");
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);
    }

    private void setupValidation() {
        validationSupport.registerValidator(tfTracking, Validator.createEmptyValidator("Tracking requerido"));
        validationSupport.registerValidator(tfRecipient, Validator.createEmptyValidator("Destinatario requerido"));
    }

    public void setTargetList(ObservableList<Delivery> list) {
        this.targetList = list;
        if (list != null && !list.isEmpty()) {
            list.stream()
                .map(Delivery::getId)
                .filter(id -> id > 0)
                .max(Comparator.naturalOrder())
                .ifPresent(max -> ID_GEN.set(Math.max(ID_GEN.get(), max + 1)));
        }
    }

    public void setDelivery(Delivery d) {
        if (d == null) return;
        this.currentDelivery = d;
        this.isEditMode = true;
        populateFields(d);
        btnDelete.setVisible(true);
        btnDelete.setManaged(true);
    }

    private void populateFields(Delivery d) {
        tfTracking.setText(d.getTrackingNumber());
        tfSender.setText(d.getSenderName());
        tfRecipient.setText(d.getRecipientName());
        tfOrigin.setText(d.getOrigin());
        tfDestination.setText(d.getDestination());
        tfWeight.setText(d.getWeight());
        tfDate.setText(d.getDate());
        cbStatus.setValue(d.getStatus());
    }

    private void fillDeliveryFromFields(Delivery d) {
        d.setTrackingNumber(tfTracking.getText());
        d.setSenderName(tfSender.getText());
        d.setRecipientName(tfRecipient.getText());
        d.setOrigin(tfOrigin.getText());
        d.setDestination(tfDestination.getText());
        d.setWeight(tfWeight.getText());
        d.setDate(tfDate.getText());
        d.setStatus(cbStatus.getValue());
    }

    @FXML
    private void handleSave() {
        if (validationSupport.isInvalid()) {
            Utility.createNotification("Corrige los campos requeridos", NotificationType.INFORMATION);
            return;
        }

        if (isEditMode) {
            fillDeliveryFromFields(currentDelivery);
            Utility.createNotification("Envío actualizado", NotificationType.SUCCESS);
        } else {
            Delivery nuevo = new Delivery();
            fillDeliveryFromFields(nuevo);
            nuevo.setId(ID_GEN.getAndIncrement());
            if (targetList != null) targetList.add(nuevo);
            Utility.createNotification("Envío registrado", NotificationType.SUCCESS);
        }
        closeWindow();
    }

    @FXML
    private void handleDelete() {
        if (!isEditMode || currentDelivery == null) return;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este envío?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        Optional<ButtonType> opt = a.showAndWait();
        if (opt.isPresent() && opt.get() == ButtonType.YES) {
            if (targetList != null) targetList.removeIf(d -> d.getId() == currentDelivery.getId());
            Utility.createNotification("Envío eliminado", NotificationType.SUCCESS);
            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        try {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            Utility.animateExit(btnCancel.getScene().getRoot(), stage);
        } catch (Exception ex) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }
}