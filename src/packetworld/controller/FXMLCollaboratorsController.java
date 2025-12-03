/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package packetworld.controller;

import animatefx.animation.ZoomIn;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import packetworld.pojo.Collaborator;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FXMLCollaboratorsController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<Collaborator, String> colCurp;
    @FXML
    private TableView<Collaborator> tvCollaborators;
    @FXML
    private TableColumn<Collaborator, String> colName;
    @FXML
    private TableColumn<Collaborator, String> colLastname;
    @FXML
    private TableColumn<Collaborator, String> colSurname;
    @FXML
    private TableColumn<Collaborator, String> colEmail;
    @FXML
    private TableColumn<Collaborator, String> colpersonalNumber;
    @FXML
    private TableColumn<Collaborator, String> colRole;
    @FXML
    private TableColumn<Collaborator, String> colStore;
    @FXML
    private TableColumn<Collaborator, String> colLicense;

    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAssign;
    private ObservableList<Collaborator> collaboratorsList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadData();
        configureTableSelection();
        tvCollaborators.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colCurp.setCellValueFactory(new PropertyValueFactory<>("curp"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colpersonalNumber.setCellValueFactory(new PropertyValueFactory<>("personalNumber"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStore.setCellValueFactory(new PropertyValueFactory<>("store"));
        colLicense.setCellValueFactory(new PropertyValueFactory<>("license"));

        tvCollaborators.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadData() {
        collaboratorsList = FXCollections.observableArrayList();

        collaboratorsList.add(new Collaborator("2023001", "Marco", "Antonio", "Solis", "SAM900101HDFRRN010", "buki@gmail.com", "Administrador", "Sucursal Central", ""));
        collaboratorsList.add(new Collaborator("2023002", "Juan", "Gabriel", "Tejano", "TGJ950202MSDFRRN02", "juanga@hotmail.com.com", "Conductor", "Sucursal Norte", "20180102"));

        tvCollaborators.setItems(collaboratorsList);
    }

    private void configureTableSelection() {
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        btnAssign.setDisable(true);
        tvCollaborators.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = (newSelection != null);

            btnEdit.setDisable(!isSelected);
            btnDelete.setDisable(!isSelected);
            btnAssign.setDisable(!isSelected);
        });

    }

    @FXML
    private void handleAddCollaborator(ActionEvent event) {
        openModal(null);
    }

    @FXML
    private void handleEditCollaborator(ActionEvent event) {
        Collaborator selected = tvCollaborators.getSelectionModel().getSelectedItem();

        if (selected != null) {
            openModal(selected);
        } else {
            Utility.createAlert("No hay Colaborador Seleccionado", "Por favor, Seleccionar un Colaborador de la lista", NotificationType.INFORMATION);
        }
    }

    @FXML
    private void handleAssignCollaborator(ActionEvent event) {
        Collaborator selected = tvCollaborators.getSelectionModel().getSelectedItem();

        if (selected != null) {
            if ("Conductor".equalsIgnoreCase(selected.getRole())) {
                openAssignModal(selected);
            } else {
                Utility.createAlert("Rol Incorrecto", "Solo se pueden asignar vehículos a colaboradores con el rol de 'Conductor'.", NotificationType.FAILURE);
            }
        } else {
            Utility.createAlert("Selección Requerida", "Selecciona un conductor de la lista.", NotificationType.FAILURE);
        }
    }

@FXML
    private void handleDeleteCollaborator(ActionEvent event) {
        Collaborator selected = tvCollaborators.getSelectionModel().getSelectedItem();
        
        if (selected != null) {
            String mensaje = "¿Está seguro que desea eliminar al colaborador " 
                    + selected.getPersonalNumber() + ": " 
                    + selected.getName() + " " + selected.getLastname() + " " + selected.getSurname() + "?\n"
                    + "Recuerde que esta acción es irreversible.";

            boolean confirmarEliminacion = Utility.createAlert("Eliminar Colaborador", mensaje, NotificationType.DELETE);

            if (confirmarEliminacion) {
                collaboratorsList.remove(selected);
                
                Utility.createNotification("Colaborador eliminado exitosamente", NotificationType.DELETE);
            }
        }
    }

    private void openModal(Collaborator collaborator) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLCollaboratorForm.fxml"));
            Parent root = loader.load();

            FXMLCollaboratorFormController controller = loader.getController();

            boolean isEditing = (collaborator != null);

            if (isEditing) {
                controller.setCollaborator(collaborator);
            }

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);

            ZoomIn zoomInAnimation = new ZoomIn(root);
            zoomInAnimation.setSpeed(1.4);
            zoomInAnimation.play();

            stage.showAndWait();
            if (controller.isOperationSuccess()) {
                String message;

                if (isEditing) {
                    message = "Colaborador editado exitosamente";
                } else {
                    message = "Colaborador guardado exitosamente";
                }

                Utility.createNotification(message, NotificationType.SUCCESS);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al abrir el formulario: " + ex.getMessage());
        }
    }

    private void openAssignModal(Collaborator driver) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLAssignVehicle.fxml"));
            Parent root = loader.load();

            FXMLAssignVehicleController controller = loader.getController();
            controller.initData(driver);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);

            ZoomIn zoomIn = new ZoomIn(root);
            zoomIn.setSpeed(1.5);
            zoomIn.play();

            stage.showAndWait();

            if (controller.isOperationSuccess()) {
                Utility.createNotification("Vehículo asignado exitosamente", NotificationType.SUCCESS);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al abrir asignación: " + ex.getMessage());
        }
    }

}
