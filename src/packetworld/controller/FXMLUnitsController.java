package packetworld.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import packetworld.domain.UnitImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Unit;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLUnitsController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<Unit> tvUnits;
    @FXML private TableColumn<Unit, String> colBrand;
    @FXML private TableColumn<Unit, String> colModel;
    @FXML private TableColumn<Unit, Integer> colYear;
    @FXML private TableColumn<Unit, String> colVIN;
    @FXML private TableColumn<Unit, String> colType;
    @FXML private TableColumn<Unit, String> colNII;

    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private ObservableList<Unit> unitsList;
    private FilteredList<Unit> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        configureTableSelection();
        loadData();
        configureSearchFilter();
        tvUnits.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }    

    private void configureTableColumns() {
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colVIN.setCellValueFactory(new PropertyValueFactory<>("vin"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colNII.setCellValueFactory(new PropertyValueFactory<>("nii"));
    }
    
    private void configureTableSelection() {
        if(tvUnits.getScene() != null) {
             // Lógica de seguridad si es nulo
        }
        
        tvUnits.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isSelected = (newVal != null);
            try {
               Button edit = (Button) tvUnits.getScene().lookup(".btn-blue");
               Button delete = (Button) tvUnits.getScene().lookup(".btn-red");
               if(edit != null) edit.setDisable(!isSelected);
               if(delete != null) delete.setDisable(!isSelected);
            } catch(Exception e) { /* Ignorar si aún no carga la escena */ }
        });
    }

    private void loadData() {
        new Thread(() -> {
            HashMap<String, Object> response = UnitImp.getAll();
            javafx.application.Platform.runLater(() -> {
                if (!(boolean) response.get("error")) {
                    List<Unit> list = (List<Unit>) response.get("data");
                    unitsList = FXCollections.observableArrayList(list);
                    
                    filteredData = new FilteredList<>(unitsList, p -> true);
                    SortedList<Unit> sortedData = new SortedList<>(filteredData);
                    sortedData.comparatorProperty().bind(tvUnits.comparatorProperty());
                    tvUnits.setItems(sortedData);
                } else {
                    Utility.createAlert("Error", (String) response.get("message"), NotificationType.FAILURE);
                }
            });
        }).start();
    }

    private void configureSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(unit -> {
                if (newValue == null || newValue.isEmpty()) return true;
                
                String lowerFilter = newValue.toLowerCase().trim();
                
                // Búsqueda por VIN, Marca o NII
                if (unit.getVin() != null && unit.getVin().toLowerCase().contains(lowerFilter)) return true;
                if (unit.getBrand() != null && unit.getBrand().toLowerCase().contains(lowerFilter)) return true;
                if (unit.getNii() != null && unit.getNii().toLowerCase().contains(lowerFilter)) return true;
                
                return false;
            });
        });
    }

    @FXML
    private void handleAddUnit(ActionEvent event) {
        Utility.<FXMLUnitFormController>openAnimatedModal(
            "/packetworld/view/FXMLUnitForm.fxml",
            null,
            controller -> controller.isOperationSuccess(),
            controller -> "Unidad registrada correctamente"
        );
        loadData();
    }

    @FXML
    private void handleEditUnit(ActionEvent event) {
        Unit selected = tvUnits.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Utility.<FXMLUnitFormController>openAnimatedModal(
                "/packetworld/view/FXMLUnitForm.fxml",
                controller -> controller.setUnit(selected),
                controller -> controller.isOperationSuccess(),
                controller -> "Unidad actualizada correctamente"
            );
            loadData();
        } else {
            Utility.createAlert("Aviso", "Selecciona una unidad para editar", NotificationType.INFORMATION);
        }
    }

    @FXML
    private void handleDeleteUnit(ActionEvent event) {
        Unit selected = tvUnits.getSelectionModel().getSelectedItem();

        if (selected != null) {
            // 1. Pedir el motivo al usuario
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Dar de baja unidad");
            dialog.setHeaderText("Baja de unidad: " + selected.getBrand() + " " + selected.getModel());
            dialog.setContentText("Por favor ingrese el motivo de la baja:");

            Optional<String> result = dialog.showAndWait();
            
            result.ifPresent(reason -> {
                // 2. Validar que escribió algo
                if (reason.trim().isEmpty()) {
                    Utility.createAlert("Aviso", "El motivo es obligatorio para continuar.", NotificationType.INFORMATION);
                    return;
                }

                // 3. Enviar la petición de baja
                MessageResponse response = UnitImp.delete(selected.getIdUnit(), reason);

                if (!response.isError()) {
                    Utility.createNotification("Unidad dada de baja exitosamente", NotificationType.SUCCESS);
                    loadData();
                } else {
                    Utility.createAlert("Error", response.getMessage(), NotificationType.FAILURE);
                }
            });
        } else {
            Utility.createAlert("Selección requerida", "Por favor, selecciona una unidad.", NotificationType.INFORMATION);
        }
    }
}