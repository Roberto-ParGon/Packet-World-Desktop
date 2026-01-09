package packetworld.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import packetworld.domain.PaqueteImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Paquete;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLPackagesController implements Initializable {

    @FXML
    private TableView<Paquete> tvPaquetes;
    @FXML
    private TableColumn<Paquete, String> colId;
    @FXML
    private TableColumn<Paquete, String> colEnvioId;
    @FXML
    private TableColumn<Paquete, String> colDescripcion;
    @FXML
    private TableColumn<Paquete, String> colPeso;
    @FXML
    private TableColumn<Paquete, String> colDimensiones;
    @FXML
    private TableColumn<Paquete, String> colCantidad;
    @FXML
    private TableColumn<Paquete, String> colValor;
    // @FXML private TableColumn<Paquete, String> colFecha;

    @FXML
    private TextField searchField;
    @FXML
    private Label lblFilter;
    @FXML
    private Label lblSearch;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    private ObservableList<Paquete> paquetesList = FXCollections.observableArrayList();
    private FilteredList<Paquete> filteredData;
    private String filterType = "General";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lblFilter != null) {
            lblFilter.setCursor(Cursor.HAND);
        }
        if (lblSearch != null) {
            lblSearch.setCursor(Cursor.HAND);
        }

        configureTable();

        filteredData = new FilteredList<>(paquetesList, p -> true);
        SortedList<Paquete> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvPaquetes.comparatorProperty());
        tvPaquetes.setItems(sortedData);

        configureSearchFilter();
        loadData();
        configureSelection();
    }

    private void configureTable() {
        colId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));
        colEnvioId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getEnvioId())));
        colDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescripcion()));
        colPeso.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getPeso()) + " kg"));

        colDimensiones.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            return new SimpleStringProperty(String.format("%.0fx%.0fx%.0f", p.getAlto(), p.getAncho(), p.getProfundidad()));
        });

        colCantidad.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getCantidad())));
        colValor.setCellValueFactory(cell -> new SimpleStringProperty("$" + cell.getValue().getValor()));
        tvPaquetes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadData() {
        new Thread(() -> {
            HashMap<String, Object> response = PaqueteImp.getAll();
            Platform.runLater(() -> {
                if (response != null && !(boolean) response.get("error")) {
                    List<Paquete> list = (List<Paquete>) response.get("data");
                    if (list != null) {
                        paquetesList.setAll(list);
                        String currentSearch = searchField.getText();
                        if (!currentSearch.isEmpty()) {
                            searchField.setText("");
                            searchField.setText(currentSearch);
                        }
                    }
                } else {
                    System.err.println("Error cargando paquetes");
                }
            });
        }).start();
    }

    private void configureSelection() {
        tvPaquetes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = (newVal != null);
            btnEdit.setDisable(!selected);
            btnDelete.setDisable(!selected);
        });
    }

    private void configureSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(paquete -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerFilter = newValue.toLowerCase().trim();

                String id = String.valueOf(paquete.getId());
                String idEnvio = String.valueOf(paquete.getEnvioId());
                String descripcion = (paquete.getDescripcion() != null) ? paquete.getDescripcion().toLowerCase() : "";

                switch (filterType) {
                    case "ID Paquete":
                        return id.contains(lowerFilter);
                    case "ID Envío":
                        return idEnvio.contains(lowerFilter);
                    case "Descripción":
                        return descripcion.contains(lowerFilter);
                    case "General":
                    default:
                        return id.contains(lowerFilter)
                                || idEnvio.contains(lowerFilter)
                                || descripcion.contains(lowerFilter);
                }
            });
        });
    }

    @FXML
    private void handleFilterIconClick(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        ToggleGroup group = new ToggleGroup();

        RadioMenuItem itemGeneral = createFilterOption("General", group, true);
        RadioMenuItem itemId = createFilterOption("ID Paquete", group, false);
        RadioMenuItem itemIdEnvio = createFilterOption("ID Envío", group, false);
        RadioMenuItem itemDesc = createFilterOption("Descripción", group, false);

        contextMenu.getItems().addAll(itemGeneral, itemId, itemIdEnvio, itemDesc);
        contextMenu.show(lblFilter, event.getScreenX(), event.getScreenY());
    }

    private RadioMenuItem createFilterOption(String text, ToggleGroup group, boolean isSelected) {
        RadioMenuItem item = new RadioMenuItem(text);
        item.setToggleGroup(group);
        item.setSelected(filterType.equals(text));

        item.setOnAction(e -> {
            filterType = text;

            String currentSearch = searchField.getText();
            searchField.setText("");
            searchField.setText(currentSearch);

            if ("General".equals(text)) {
                searchField.setPromptText("Buscar paquete...");
            } else {
                searchField.setPromptText("Buscar por: " + filterType);
            }
        });
        return item;
    }

    @FXML
    private void handleDeleteSearch(MouseEvent event) {
        searchField.setText("");
        searchField.requestFocus();
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        Utility.<FXMLPackageFormController>openAnimatedModal(
                "/packetworld/view/FXMLPackageForm.fxml",
                controller -> controller.setEditMode(false, null),
                controller -> controller.isOperationSuccess(),
                controller -> null
        );
        loadData();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Paquete selected = tvPaquetes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Utility.<FXMLPackageFormController>openAnimatedModal(
                "/packetworld/view/FXMLPackageForm.fxml",
                controller -> controller.setEditMode(true, selected),
                controller -> controller.isOperationSuccess(),
                controller -> null
        );
        loadData();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Paquete selected = tvPaquetes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        boolean confirm = Utility.showConfirmation("Eliminar Paquete", "¿Estás seguro de eliminar este paquete?");
        if (confirm) {
            new Thread(() -> {
                MessageResponse mr = PaqueteImp.deletePackage(selected.getId());
                Platform.runLater(() -> {
                    if (mr != null && !mr.isError()) {
                        Utility.createNotification("Paquete eliminado", NotificationType.SUCCESS);
                        loadData();
                    } else {
                        Utility.createAlert("Error", mr != null ? mr.getMessage() : "Error desconocido", NotificationType.FAILURE);
                    }
                });
            }).start();
        }
    }
}
