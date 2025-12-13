/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package packetworld.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import packetworld.domain.CollaboratorImp;
import packetworld.dto.MessageResponse;
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
        new Thread(() -> {
            HashMap<String, Object> response = CollaboratorImp.getAll();
            javafx.application.Platform.runLater(() -> {
                if (!(boolean) response.get("error")) {
                    List<Collaborator> list = (List<Collaborator>) response.get("collaborators");
                    collaboratorsList = FXCollections.observableArrayList(list);

                    filteredData = new FilteredList<>(collaboratorsList, p -> true);
                    SortedList<Collaborator> sortedData = new SortedList<>(filteredData);
                    sortedData.comparatorProperty().bind(tvCollaborators.comparatorProperty());
                    tvCollaborators.setItems(sortedData);

                    String currentSearch = searchField.getText();
                    if (!currentSearch.isEmpty()) {
                        searchField.setText("");
                        searchField.setText(currentSearch);
                    }
                } else {
                    Utility.createAlert("Error", (String) response.get("message"), NotificationType.FAILURE);
                }
            });
        }).start();
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

                    case "Número de Personal":
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
        RadioMenuItem itemName = createFilterOption("Nombre(s)", group, false);
        RadioMenuItem itemLastname = createFilterOption("Apellido Paterno", group, false);
        RadioMenuItem itemSurname = createFilterOption("Apellido Materno", group, false);
        RadioMenuItem itemPersonalNumber = createFilterOption("Número de Personal", group, false);
        RadioMenuItem itemRole = createFilterOption("Rol", group, false);

        contextMenu.getItems().addAll(
                itemGeneral,
                itemName,
                itemLastname,
                itemSurname,
                itemPersonalNumber,
                itemRole
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
        Utility.<FXMLCollaboratorFormController>openAnimatedModal(
                "/packetworld/view/FXMLCollaboratorForm.fxml",
                null,
                controller -> controller.isOperationSuccess(),
                controller -> "Colaborador guardado exitosamente"
        );
        loadData();
    }

    @FXML
    private void handleEditCollaborator(ActionEvent event) {
        Collaborator selected = tvCollaborators.getSelectionModel().getSelectedItem();

        if (selected != null) {
            System.out.println("Editando colaborador: " + selected.getName() + " ID: " + selected.getIdCollaborator());
            Utility.<FXMLCollaboratorFormController>openAnimatedModal(
                    "/packetworld/view/FXMLCollaboratorForm.fxml",
                    controller -> controller.setCollaborator(selected),
                    controller -> controller.isOperationSuccess(),
                    controller -> "Colaborador editado exitosamente"
            );
            loadData();
        } else {
            Utility.createAlert("Selección requerida", "Por favor, selecciona un colaborador.", NotificationType.INFORMATION);
        }
    }

    @FXML
    private void handleAssignCollaborator(ActionEvent event) {
        Collaborator selected = tvCollaborators.getSelectionModel().getSelectedItem();

        if (selected != null) {
            if ("Conductor".equalsIgnoreCase(selected.getRole())) {
                Utility.<FXMLAssignVehicleController>openAnimatedModal(
                        "/packetworld/view/FXMLAssignVehicle.fxml",
                        controller -> controller.initData(selected),
                        controller -> controller.isOperationSuccess(),
                        controller -> "Vehículo asignado exitosamente"
                );
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
            String message = "¿Está seguro que desea eliminar al colaborador "
                    + selected.getName() + " " + selected.getLastname() + "?\n"
                    + "Esta acción es irreversible.";

            boolean confirmDelete = Utility.createAlert("Eliminar Colaborador", message, NotificationType.DELETE);

            if (confirmDelete) {
                MessageResponse response = CollaboratorImp.delete(selected.getIdCollaborator());

                if (!response.isError()) {
                    String msg = (response.getMessage() != null && !response.getMessage().isEmpty())
                            ? response.getMessage()
                            : "Colaborador eliminado exitosamente";

                    Utility.createNotification(msg, NotificationType.DELETE);

                    loadData();
                } else {
                    Utility.createAlert("Error", response.getMessage(), NotificationType.FAILURE);
                }
            }
        }
    }

}
