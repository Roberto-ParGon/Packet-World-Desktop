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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import packetworld.domain.UnitImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Unit;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLUnitsController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private Label lblFilter;

    @FXML
    private TableView<Unit> tvUnits;
    @FXML
    private TableColumn<Unit, String> colBrand;
    @FXML
    private TableColumn<Unit, String> colModel;
    @FXML
    private TableColumn<Unit, Integer> colYear;
    @FXML
    private TableColumn<Unit, String> colVIN;
    @FXML
    private TableColumn<Unit, String> colType;
    @FXML
    private TableColumn<Unit, String> colNII;

    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    private ObservableList<Unit> unitsList;
    private FilteredList<Unit> filteredData;
    private String filterType = "General";

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
        if (btnEdit != null) {
            btnEdit.setDisable(true);
        }
        if (btnDelete != null) {
            btnDelete.setDisable(true);
        }

        tvUnits.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isSelected = (newVal != null);
            if (btnEdit != null) {
                btnEdit.setDisable(!isSelected);
            }
            if (btnDelete != null) {
                btnDelete.setDisable(!isSelected);
            }
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
            filteredData.setPredicate(unit -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerFilter = newValue.toLowerCase().trim();

                String brand = (unit.getBrand() != null) ? unit.getBrand().toLowerCase() : "";
                String model = (unit.getModel() != null) ? unit.getModel().toLowerCase() : "";
                String vin = (unit.getVin() != null) ? unit.getVin().toLowerCase() : "";
                String nii = (unit.getNii() != null) ? unit.getNii().toLowerCase() : "";
                String type = (unit.getType() != null) ? unit.getType().toLowerCase() : "";
                String year = (unit.getYear() != null) ? unit.getYear().toString() : "";

                switch (filterType) {
                    case "Marca":
                        return brand.contains(lowerFilter);
                    case "Modelo":
                        return model.contains(lowerFilter);
                    case "Año":
                        return year.contains(lowerFilter);
                    case "VIN":
                        return vin.contains(lowerFilter);
                    case "Tipo":
                        return type.contains(lowerFilter);
                    case "NII":
                        return nii.contains(lowerFilter);
                    case "General":
                    default:

                        return brand.contains(lowerFilter)
                                || model.contains(lowerFilter)
                                || vin.contains(lowerFilter)
                                || nii.contains(lowerFilter);
                }
            });
        });
    }

    @FXML
    private void handleFilterIconClick(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        ToggleGroup group = new ToggleGroup();

        RadioMenuItem itemGeneral = createFilterOption("General (Marca, Modelo, VIN, NII)", group, true);
        RadioMenuItem itemBrand = createFilterOption("Marca", group, false);
        RadioMenuItem itemModel = createFilterOption("Modelo", group, false);
        RadioMenuItem itemYear = createFilterOption("Año", group, false);
        RadioMenuItem itemVIN = createFilterOption("VIN", group, false);
        RadioMenuItem itemType = createFilterOption("Tipo", group, false);
        RadioMenuItem itemNII = createFilterOption("NII", group, false);

        contextMenu.getItems().addAll(itemGeneral, itemBrand, itemModel, itemYear, itemVIN, itemType, itemNII);
        contextMenu.show(lblFilter, event.getScreenX(), event.getScreenY());
    }

    private RadioMenuItem createFilterOption(String text, ToggleGroup group, boolean isSelected) {
        String key = text.contains("General") ? "General" : text;

        RadioMenuItem item = new RadioMenuItem(text);
        item.setToggleGroup(group);
        item.setSelected(filterType.equals(key));

        item.setOnAction(e -> {
            filterType = key;

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
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Dar de baja unidad");
            dialog.setHeaderText("Baja de unidad: " + selected.getBrand() + " " + selected.getModel());
            dialog.setContentText("Por favor ingrese el motivo de la baja:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(reason -> {
                if (reason.trim().isEmpty()) {
                    Utility.createAlert("Aviso", "El motivo es obligatorio para continuar.", NotificationType.INFORMATION);
                    return;
                }

                MessageResponse response = UnitImp.delete(selected.getIdUnit(), reason);

                if (!response.isError()) {
                    Utility.createNotification("Unidad dada de baja exitosamente", NotificationType.DELETE);
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
