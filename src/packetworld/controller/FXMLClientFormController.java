package packetworld.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import packetworld.pojo.Client;
import packetworld.domain.ClientImp;
import packetworld.dto.MessageResponse;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.ObservableList;

public class FXMLClientFormController implements Initializable {

    @FXML
    private TextField tfFirstName;
    @FXML
    private TextField tfLastName;
    @FXML
    private TextField tfStreet;
    @FXML
    private TextField tfNumber;
    @FXML
    private TextField tfColony;
    @FXML
    private TextField tfZipCode;
    @FXML
    private TextField tfPhone;
    @FXML
    private TextField tfEmail;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnDelete;

    private final ValidationSupport validationSupport = new ValidationSupport();

    private Client currentClient;
    private boolean isEditMode = false;
    private boolean operationSuccess = false;

    private ObservableList<Client> targetList;
    private static final AtomicInteger ID_GEN = new AtomicInteger(1000);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupValidation();
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);
    }

    private void setupValidation() {
        validationSupport.registerValidator(tfFirstName, Validator.createEmptyValidator("Nombre requerido"));
        validationSupport.registerValidator(tfLastName, Validator.createEmptyValidator("Apellido requerido"));
        validationSupport.registerValidator(tfEmail, Validator.createRegexValidator("Correo inválido",
                "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", null));
    }

    public void setTargetList(ObservableList<Client> list) {
        this.targetList = list;
        if (list != null && !list.isEmpty()) {
            list.stream()
                    .map(Client::getId)
                    .filter(id -> id != null && id > 0)
                    .max(Comparator.naturalOrder())
                    .ifPresent(max -> ID_GEN.set(Math.max(ID_GEN.get(), max + 1)));
        }
    }

    public void setClient(Client c) {
        if (c == null) {
            return;
        }
        this.currentClient = c;
        this.isEditMode = true;
        populateFields(c);
        btnDelete.setVisible(true);
        btnDelete.setManaged(true);
    }

    private void populateFields(Client c) {
        tfFirstName.setText(c.getFirstName());
        tfLastName.setText(c.getLastName());
        tfStreet.setText(c.getStreet());
        tfNumber.setText(c.getNumber());
        tfColony.setText(c.getColony());
        tfZipCode.setText(c.getZipCode());
        tfPhone.setText(c.getPhone());
        tfEmail.setText(c.getEmail());
    }

    private void fillClientFromFields(Client c) {
        c.setFirstName(tfFirstName.getText());
        c.setLastName(tfLastName.getText());
        c.setStreet(tfStreet.getText());
        c.setNumber(tfNumber.getText());
        c.setColony(tfColony.getText());
        c.setZipCode(tfZipCode.getText());
        c.setPhone(tfPhone.getText());
        c.setEmail(tfEmail.getText());
        if (c.getStatus() == null) {
            c.setStatus("Activo");
        }
    }

    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    @FXML
    private void handleSave() {
        if (validationSupport.isInvalid()) {
            Utility.createNotification("Corrige los campos requeridos", NotificationType.INFORMATION);
            return;
        }

        new Thread(() -> {
            MessageResponse resp;
            if (isEditMode) {
                fillClientFromFields(currentClient);
                resp = ClientImp.edit(currentClient);
            } else {
                Client nuevo = new Client();
                fillClientFromFields(nuevo);
                resp = ClientImp.register(nuevo);
            }

            final MessageResponse finalResp = resp;
            javafx.application.Platform.runLater(() -> {
                if (!finalResp.isError()) {
                    operationSuccess = true;
                    closeWindow();
                } else {
                    Utility.createAlert("Error", finalResp.getMessage(), NotificationType.FAILURE);
                }
            });
        }).start();
    }

    @FXML
    private void handleDelete() {
        if (!isEditMode || currentClient == null) {
            return;
        }
        boolean confirm = Utility.createAlert("Eliminar cliente", "¿Eliminar este cliente?", NotificationType.DELETE);
        if (!confirm) {
            return;
        }

        new Thread(() -> {
            MessageResponse resp = ClientImp.delete(currentClient.getId());
            javafx.application.Platform.runLater(() -> {
                if (!resp.isError()) {
                    operationSuccess = true;
                    Utility.createNotification(resp.getMessage() == null ? "Cliente eliminado" : resp.getMessage(), NotificationType.DELETE);
                    closeWindow();
                } else {
                    Utility.createAlert("Error", resp.getMessage(), NotificationType.FAILURE);
                }
            });
        }).start();
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
