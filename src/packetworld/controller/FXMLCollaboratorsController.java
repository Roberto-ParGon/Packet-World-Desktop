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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

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
    private Label lblFilter;
    @FXML
    private Label lblSearch;
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
    private FilteredList<Collaborator> filteredData;

    private String filterType = "General";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadData();
        configureTableSelection();
        configureSearchFilter();
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
        collaboratorsList.add(new Collaborator("2023003", "Pepe", "Pica", "Papas", "PPP900101HDFRRN010", "ppp@gmail.com", "Ejecutivo de tienda", "Sucursal Sur", ""));
        collaboratorsList.add(new Collaborator("2023004", "Valentin", "Elizalde", "Fermín", "VLE950202MSDFRRN02", "elizalde@hotmail.com.com", "Conductor", "Sucursal Central", "30180201"));
        collaboratorsList.add(new Collaborator("2023005", "Elizabeth Angela", "Ferro", "Valentín", "SAM900101HDFRRN010", "eli@gmail.com", "Administrador", "Sucursal Sur", ""));
        collaboratorsList.add(new Collaborator("2023006", "Gabriel", "Montiel", "Ferro", "WRV950202MSDFRRN02", "wero@hotmail.com.com", "Conductor", "Surcusal Central", "6080100"));

        filteredData = new FilteredList<>(collaboratorsList, p -> true);

        SortedList<Collaborator> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tvCollaborators.comparatorProperty());

        tvCollaborators.setItems(sortedData);
    }

    private void configureSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(collaborator -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (lowerCaseFilter.isEmpty()) {
                    return true;
                }

                String fullName = (collaborator.getName() + " " + collaborator.getLastname() + " " + collaborator.getSurname()).toLowerCase();
                String personalNumber = collaborator.getPersonalNumber().toLowerCase();
                String role = collaborator.getRole().toLowerCase();

                switch (filterType) {
                    case "Nombre(s)":
                        return collaborator.getName().toLowerCase().contains(lowerCaseFilter);

                    case "Apellido Paterno":
                        return collaborator.getLastname().toLowerCase().contains(lowerCaseFilter);

                    case "Apellido Materno":
                        return collaborator.getSurname().toLowerCase().contains(lowerCaseFilter);

                    case "No. Personal":
                        return personalNumber.contains(lowerCaseFilter);

                    case "Rol":
                        return role.contains(lowerCaseFilter);

                    case "General":
                    default:
                        if (fullName.contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (personalNumber.contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (role.contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false;
                }
            });
        });
    }

    @FXML
    private void handleFilterIconClick(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        ToggleGroup group = new ToggleGroup();

        RadioMenuItem itemGeneral = createFilterOption("General (Todos los campos)", group, true);
        RadioMenuItem itemNombre = createFilterOption("Nombre(s)", group, false);
        RadioMenuItem itemPaterno = createFilterOption("Apellido Paterno", group, false);
        RadioMenuItem itemMaterno = createFilterOption("Apellido Materno", group, false);
        RadioMenuItem itemPersonal = createFilterOption("Número de Personal", group, false);
        RadioMenuItem itemRol = createFilterOption("Rol", group, false);

        contextMenu.getItems().addAll(
                itemGeneral,
                itemNombre,
                itemPaterno,
                itemMaterno,
                itemPersonal,
                itemRol
        );

        contextMenu.show(lblFilter, event.getScreenX(), event.getScreenY());
    }

    private RadioMenuItem createFilterOption(String text, ToggleGroup group, boolean isSelected) {
        RadioMenuItem item = new RadioMenuItem(text);
        item.setToggleGroup(group);
        item.setSelected(isSelected || text.startsWith(filterType));

        item.setOnAction(e -> {
            if (text.contains("General")) {
                filterType = "General";
            } else {
                filterType = text;
            }

            String currentSearch = searchField.getText();
            searchField.setText("");
            searchField.setText(currentSearch);

            searchField.setPromptText("Buscar por: " + filterType);
        });

        return item;
    }

    @FXML
    private void handleDeleteSearch(MouseEvent event) {
        searchField.setText("");
        searchField.requestFocus();
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
