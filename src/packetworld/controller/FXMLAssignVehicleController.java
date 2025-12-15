package packetworld.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import packetworld.domain.DriverAssignmentImp;
import packetworld.domain.VehicleImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Collaborator;
import packetworld.pojo.DriverAssignment;
import packetworld.pojo.Vehicle;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLAssignVehicleController implements Initializable {

    @FXML
    private Label lblMessage;
    @FXML
    private ComboBox<Vehicle> cbVehicles;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnAssign;
    @FXML
    private VBox vbCurrentAssignment;
    @FXML
    private Label lblCurrentVehicle;
    @FXML
    private Button btnUnassign;

    private boolean operationSuccess = false;
    private Collaborator currentDriver;

    private String onSuccessMessage = "Operación realizada correctamente";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vbCurrentAssignment.setVisible(false);
        vbCurrentAssignment.setManaged(false);
    }

    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    public String getOnSuccessMessage() {
        return onSuccessMessage;
    }

    public void initData(Collaborator driver) {
        this.currentDriver = driver;
        if (driver != null) {
            String fullName = driver.getName() + " " + driver.getLastname();
            lblMessage.setText("Conductor: " + fullName);
            loadData();
        }
    }

    private void loadData() {
        new Thread(() -> {
            HashMap<String, Object> availableResponse = VehicleImp.getAvailable();

            List<Vehicle> finalVehicleList = new ArrayList<>();
            Vehicle vehicleToSelect = null;
            String errorMessage = null;

            if (!(boolean) availableResponse.get("error")) {
                List<Vehicle> available = (List<Vehicle>) availableResponse.get("data");
                finalVehicleList.addAll(available);

                DriverAssignment assignment = DriverAssignmentImp.getAssignment(currentDriver.getIdCollaborator());

                if (assignment != null && assignment.getVehicleId() != null) {
                    int assignedId = assignment.getVehicleId();

                    vehicleToSelect = finalVehicleList.stream()
                            .filter(v -> v.getId() == assignedId)
                            .findFirst()
                            .orElse(null);

                    if (vehicleToSelect == null) {
                        Vehicle assignedVehicle = VehicleImp.getById(assignedId);
                        if (assignedVehicle != null) {
                            vehicleToSelect = assignedVehicle;
                        }
                    }
                }
            } else {
                errorMessage = (String) availableResponse.get("message");
            }

            final String msg = errorMessage;
            final Vehicle current = vehicleToSelect;

            Platform.runLater(() -> {
                if (msg == null) {
                    cbVehicles.setItems(FXCollections.observableArrayList(finalVehicleList));

                    if (current != null) {
                        vbCurrentAssignment.setVisible(true);
                        vbCurrentAssignment.setManaged(true);
                        lblCurrentVehicle.setText(current.toString());
                    } else {
                        vbCurrentAssignment.setVisible(false);
                        vbCurrentAssignment.setManaged(false);
                    }
                } else {
                    Utility.createAlert("Error", msg, NotificationType.FAILURE);
                }
            });
        }).start();
    }

    @FXML
    private void handleAssign(ActionEvent event) {
        Vehicle selectedVehicle = cbVehicles.getValue();

        if (selectedVehicle == null) {
            Utility.createAlert("Selección requerida", "Seleccione un vehículo de la lista.", NotificationType.INFORMATION);
            return;
        }

        MessageResponse response = DriverAssignmentImp.assignVehicle(
                currentDriver.getIdCollaborator(),
                selectedVehicle.getId()
        );

        if (!response.isError()) {
            this.operationSuccess = true;
            this.onSuccessMessage = "Vehículo asignado correctamente";
            closeWindow();
        } else {
            Utility.createAlert("Error al asignar", response.getMessage(), NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleUnassign(ActionEvent event) {
        boolean confirm = Utility.createAlert("Desasignar Vehículo",
                "¿Está seguro de quitar el vehículo al conductor?\nEl vehículo quedará disponible inmediatamente.",
                NotificationType.DELETE);

        if (confirm) {
            MessageResponse response = DriverAssignmentImp.unassignVehicle(currentDriver.getIdCollaborator());

            if (!response.isError()) {
                this.operationSuccess = true;
                Utility.createNotification("Vehículo desasignado", NotificationType.SUCCESS);

                this.onSuccessMessage = null;

                loadData();
            } else {
                Utility.createAlert("Error", response.getMessage(), NotificationType.FAILURE);
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        Node root = btnCancel.getScene().getRoot();
        Utility.animateExit(root, stage);
    }
}
