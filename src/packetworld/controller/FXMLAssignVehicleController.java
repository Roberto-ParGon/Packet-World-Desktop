/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package packetworld.controller;

import animatefx.animation.ZoomOut;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import packetworld.pojo.Collaborator;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FXMLAssignVehicleController implements Initializable {

    @FXML
    private Label lblMessage;
    @FXML
    private ComboBox<String> cbVehicles;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnAssign;

    private boolean operationSuccess = false;
    private Collaborator currentDriver;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadVehicles();
    }

    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    public void initData(Collaborator driver) {
        this.currentDriver = driver;
        if (driver != null) {
            String fullName = driver.getName() + " " + driver.getLastname() + " " + driver.getSurname();
            lblMessage.setText("¿A qué vehículo desea asignarle el conductor \n" + fullName + "?");
        }
    }

    private void loadVehicles() {
        cbVehicles.getItems().addAll(
                "Nissan Versa 2022 - VIN: 12345",
                "Opel Corsa 2005 - VIN: 67890",
                "Ford Transit 2021 - VIN: 54321",
                "Chevrolet Aveo 2020 - VIN: 09876"
        );
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void handleAssign(ActionEvent event) {
        if (cbVehicles.getValue() == null) {
            Utility.createAlert("Selección requerida", "Por favor seleccione un vehículo de la lista.", NotificationType.INFORMATION);
            return;
        }

        this.operationSuccess = true;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        Node root = btnCancel.getScene().getRoot();
        Utility.animateExit(root, stage);
    }
}
