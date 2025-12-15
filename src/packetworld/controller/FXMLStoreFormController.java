/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package packetworld.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import packetworld.domain.StoreImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Store;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FXMLStoreFormController implements Initializable {

    @FXML
    private HBox headerContainer;
    @FXML
    private Label lblTitle;
    @FXML
    private TextField tfCode;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfStreet;
    @FXML
    private TextField tfNumber;
    @FXML
    private TextField tfColony;
    @FXML
    private TextField tfZipCode;
    @FXML
    private TextField tfCity;
    @FXML
    private TextField tfState;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

    private boolean isEditMode = false;
    private boolean operationSuccess = false;
    private Store currentStore;
    private final ValidationSupport validationSupport = new ValidationSupport();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupValidation();
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);
        lblStatus.setVisible(false);
    }

    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    private void setupValidation() {
        validationSupport.registerValidator(tfCode, Validator.createEmptyValidator("Código requerido"));
        validationSupport.registerValidator(tfName, Validator.createEmptyValidator("Nombre requerido"));
        validationSupport.registerValidator(tfStreet, Validator.createEmptyValidator("Calle requerida"));
        validationSupport.registerValidator(tfZipCode, Validator.createRegexValidator("CP inválido", "\\d{5}", null));
        validationSupport.registerValidator(tfNumber, Validator.createEmptyValidator("Número requerido"));
        validationSupport.registerValidator(tfColony, Validator.createEmptyValidator("Colonia requerida"));
        validationSupport.registerValidator(tfCity, Validator.createEmptyValidator("Ciudad Requerida"));
        validationSupport.registerValidator(tfState, Validator.createEmptyValidator("Estado Requerida"));

    }

    @FXML
    private void handleDelete(ActionEvent event) {
        boolean confirm = Utility.createAlert("Dar de Baja",
                "¿Está seguro de dar de baja la sucursal " + currentStore.getName() + "?\nEsta acción no se puede deshacer.",
                NotificationType.DELETE);

        if (confirm) {
            MessageResponse response = StoreImp.changeStatus(currentStore.getIdStore(), "Inactiva");

            if (!response.isError()) {
                this.operationSuccess = true;
                Utility.createNotification("Sucursal dada de baja correctamente", NotificationType.SUCCESS);
                closeWindow();
            } else {
                Utility.createAlert("Error", response.getMessage(), NotificationType.FAILURE);
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void handleSave(ActionEvent event) {

        if (tfCode.getText().isEmpty() || tfName.getText().isEmpty() || tfStreet.getText().isEmpty()
                || tfNumber.getText().isEmpty() || tfColony.getText().isEmpty() || tfZipCode.getText().isEmpty()
                || tfCity.getText().isEmpty() || tfState.getText().isEmpty()) {

            Utility.createAlert("Campos Vacíos", "Por favor, llene todos los campos obligatorios.", NotificationType.FAILURE);
            return;
        }

        Store store = new Store();
        store.setCode(tfCode.getText());
        store.setName(tfName.getText());
        store.setStreet(tfStreet.getText());
        store.setNumber(tfNumber.getText());
        store.setColony(tfColony.getText());
        store.setZipCode(tfZipCode.getText());
        store.setCity(tfCity.getText());
        store.setState(tfState.getText());

        if (!isEditMode) {
            store.setStatus("Activa");
        }

        MessageResponse response;

        if (isEditMode) {
            store.setIdStore(currentStore.getIdStore());
            store.setCode(currentStore.getCode());

            response = StoreImp.edit(store);
        } else {
            response = StoreImp.register(store);
        }

        if (!response.isError()) {
            this.operationSuccess = true;
            closeWindow();
        } else {
            Utility.createAlert("Error al guardar", response.getMessage(), NotificationType.FAILURE);
        }
    }

    public void setStore(Store store) {
        this.isEditMode = true;
        this.currentStore = store;

        lblTitle.setText("Editar Sucursal");
        btnSave.setText("Actualizar Datos");
        headerContainer.setStyle("-fx-background-color: #42A5F5;");

        tfCode.setText(store.getCode());
        tfName.setText(store.getName());
        tfStreet.setText(store.getStreet());
        tfNumber.setText(store.getNumber());
        tfColony.setText(store.getColony());
        tfZipCode.setText(store.getZipCode());
        tfCity.setText(store.getCity());
        tfState.setText(store.getState());

        lblStatus.setVisible(true);
        lblStatus.setText("Estatus: " + store.getStatus());

        if ("Inactiva".equalsIgnoreCase(store.getStatus())) {
            // Si ya está inactiva, estilo rojo y OCULTAMOS el botón
            lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #EF5350; -fx-font-size: 14px;");
            btnDelete.setVisible(false);
            btnDelete.setManaged(false);

        } else {

            lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-font-size: 14px;"); // Verde para activa
            btnDelete.setText("Dar de Baja");
            btnDelete.setStyle("-fx-background-color: #EF5350; -fx-text-fill: white;");
            btnDelete.setVisible(true);
            btnDelete.setManaged(true);
        }

        tfCode.setDisable(true);
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        Node root = btnCancel.getScene().getRoot();
        Utility.animateExit(root, stage);
    }

}
