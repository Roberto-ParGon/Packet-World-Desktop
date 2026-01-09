package packetworld.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import packetworld.domain.EnvioImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Envio;
import packetworld.utility.NotificationType;
import packetworld.utility.UserSession;
import packetworld.utility.Utility;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLEnviosController implements Initializable {

    @FXML
    private TableView<Envio> tvEnvios;

    @FXML
    private TableColumn<Envio, String> colId;
    @FXML
    private TableColumn<Envio, String> colIdCliente;
    @FXML
    private TableColumn<Envio, String> colGuia;
    @FXML
    private TableColumn<Envio, String> colDestinatario;
    @FXML
    private TableColumn<Envio, String> colTelefono;
    @FXML
    private TableColumn<Envio, String> colDireccion;
    @FXML
    private TableColumn<Envio, String> colCiudad;
    @FXML
    private TableColumn<Envio, String> colEstadoDestino;
    @FXML
    private TableColumn<Envio, String> colSucursalOrigen;
    @FXML
    private TableColumn<Envio, String> colCosto;
    @FXML
    private TableColumn<Envio, String> colPeso;
    @FXML
    private TableColumn<Envio, String> colEstatus;
    @FXML
    private TableColumn<Envio, String> colFechaCreacion;
    @FXML
    private TableColumn<Envio, String> colFechaActualizacion;
    @FXML
    private TableColumn<Envio, String> colColaborador;

    @FXML
    private TextField tfSearch;
    @FXML
    private Label lblFilter;
    @FXML
    private Label lblSearchClear;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnAsignar;
    @FXML
    private Button btnEliminar;

    private ObservableList<Envio> enviosList = FXCollections.observableArrayList();
    private FilteredList<Envio> filteredData;
    private String filterType = "General";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lblFilter != null) {
            lblFilter.setCursor(Cursor.HAND);
        }
        if (lblSearchClear != null) {
            lblSearchClear.setCursor(Cursor.HAND);
        }

        configureTable();

        filteredData = new FilteredList<>(enviosList, p -> true);
        SortedList<Envio> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvEnvios.comparatorProperty());
        tvEnvios.setItems(sortedData);

        loadData();
        configureSearchFilter();
        wireUi();
    }

    private void wireUi() {
        tvEnvios.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            boolean has = newS != null;
            btnEditar.setDisable(!has);
            btnAsignar.setDisable(!has);
            btnEliminar.setDisable(!has);
        });
    }

    private void configureSearchFilter() {
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(envio -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerFilter = newValue.toLowerCase().trim();
                String id = (envio.getId() != null) ? String.valueOf(envio.getId()) : "";
                String idCliente = (envio.getIdCliente() != null) ? String.valueOf(envio.getIdCliente()) : "";
                String guia = (envio.getNumGuia() != null) ? envio.getNumGuia().toLowerCase() : "";

                switch (filterType) {
                    case "ID":
                        return id.contains(lowerFilter);
                    case "ID Cliente":
                        return idCliente.contains(lowerFilter);
                    case "Guía":
                        return guia.contains(lowerFilter);
                    case "General":
                    default:
                        return id.contains(lowerFilter) || idCliente.contains(lowerFilter) || guia.contains(lowerFilter);
                }
            });
        });
    }

    @FXML
    private void handleFilterIconClick(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        ToggleGroup group = new ToggleGroup();

        contextMenu.getItems().addAll(
                createFilterOption("General", group, true),
                createFilterOption("ID", group, false),
                createFilterOption("ID Cliente", group, false),
                createFilterOption("Guía", group, false)
        );
        contextMenu.show(lblFilter, event.getScreenX(), event.getScreenY());
    }

    private RadioMenuItem createFilterOption(String text, ToggleGroup group, boolean isSelected) {
        RadioMenuItem item = new RadioMenuItem(text);
        item.setToggleGroup(group);
        item.setSelected(filterType.equals(text));
        item.setOnAction(e -> {
            filterType = text;
            String currentSearch = tfSearch.getText();
            tfSearch.setText("");
            tfSearch.setText(currentSearch);
            if ("General".equals(text)) {
                tfSearch.setPromptText("Buscar envío (ID, ID Cliente o Guía)");
            } else {
                tfSearch.setPromptText("Buscar por: " + filterType);
            }
        });
        return item;
    }

    @FXML
    private void handleClearSearch(MouseEvent event) {
        tfSearch.setText("");
        tfSearch.requestFocus();
    }

    public void loadData() {
        new Thread(() -> {
            HashMap<String, Object> resp = EnvioImp.getAll();
            Platform.runLater(() -> {
                if (resp == null) {
                    Utility.createAlert("Error", "Respuesta nula del servidor", NotificationType.FAILURE);
                    enviosList.clear();
                    return;
                }
                boolean isError = (Boolean) resp.getOrDefault("error", true);
                if (isError) {
                    String msg = (String) resp.getOrDefault("message", "Error desconocido");
                    Utility.createAlert("Error", msg, NotificationType.FAILURE);
                    enviosList.clear();
                    return;
                }

                List<Envio> list = (List<Envio>) resp.get("data");
                if (list != null) {
                    enviosList.setAll(list);
                    if (!tfSearch.getText().isEmpty()) {
                        String txt = tfSearch.getText();
                        tfSearch.setText("");
                        tfSearch.setText(txt);
                    }
                } else {
                    enviosList.clear();
                }
            });
        }).start();
    }

    private void configureTable() {
        colId.setCellValueFactory(nullSafeValue(e -> e.getId()));
        colIdCliente.setCellValueFactory(nullSafeValue(e -> e.getIdCliente()));
        colGuia.setCellValueFactory(nullSafeValue(Envio::getNumGuia));
        colDestinatario.setCellValueFactory(nullSafeValue(Envio::getDestinatarioNombre));
        applyWrappingCellFactory(colDestinatario);
        colTelefono.setCellValueFactory(nullSafeValue(Envio::getDestinatarioTelefono));
        colDireccion.setCellValueFactory(nullSafeValue(Envio::getDireccionDestino));
        applyWrappingCellFactory(colDireccion);
        colCiudad.setCellValueFactory(nullSafeValue(Envio::getCiudadDestino));
        colEstadoDestino.setCellValueFactory(nullSafeValue(Envio::getEstadoDestino));

        colSucursalOrigen.setCellValueFactory(nullSafeValue(e -> {
            if (e.getSucursalOrigen() != null && !e.getSucursalOrigen().isEmpty()) {
                return e.getSucursalOrigen();
            }
            return e.getIdSucursalOrigen() == null ? "—" : "ID: " + e.getIdSucursalOrigen();
        }));

        colCosto.setCellValueFactory(nullSafeValue(e -> e.getCosto() == null ? "" : String.format("%.2f", e.getCosto())));
        colPeso.setCellValueFactory(nullSafeValue(e -> e.getPeso() == null ? "" : String.format("%.2f", e.getPeso())));
        colEstatus.setCellValueFactory(nullSafeValue(Envio::getEstatus));
        colFechaCreacion.setCellValueFactory(nullSafeValue(Envio::getFechaCreacion));
        colFechaActualizacion.setCellValueFactory(nullSafeValue(Envio::getFechaActualizacion));

        colColaborador.setCellValueFactory(nullSafeValue(e -> {
            Integer id = e.getIdColaboradorActualizo();
            return id == null ? "—" : String.valueOf(id);
        }));

        tvEnvios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private <T> Callback<TableColumn.CellDataFeatures<Envio, String>, javafx.beans.value.ObservableValue<String>> nullSafeValue(java.util.function.Function<Envio, T> getter) {
        return cell -> {
            T v = cell.getValue() == null ? null : getter.apply(cell.getValue());
            return new SimpleStringProperty(v == null ? "" : String.valueOf(v));
        };
    }

    private void applyWrappingCellFactory(TableColumn<Envio, String> col) {
        col.setCellFactory(tc -> new TableCell<Envio, String>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
                setGraphic(text);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                text.setText(empty || item == null ? "" : item);
            }
        });
    }

    @FXML
    private void handleAdd() {
        openForm(false, null);
    }

    @FXML
    private void handleEdit() {
        Envio sel = tvEnvios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createAlert("Validación", "Selecciona un envío", NotificationType.INFORMATION);
            return;
        }
        openForm(true, sel);
    }

    private void openForm(boolean isEdit, Envio envio) {
        Utility.<FXMLEnviosFormController>openAnimatedModal(
                "/packetworld/view/FXMLEnvioForm.fxml",
                controller -> controller.setEditMode(isEdit, envio),
                controller -> controller.isOperationSuccess(),
                controller -> null
        );
        loadData();
    }

    @FXML
    private void handleDelete() {
        Envio sel = tvEnvios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createAlert("Validación", "Selecciona un envío", NotificationType.INFORMATION);
            return;
        }

        if (Utility.showConfirmation("Eliminar envío", "¿Eliminar envío id " + sel.getId() + " ?")) {
            new Thread(() -> {
                MessageResponse mr = EnvioImp.delete(sel.getId());
                Platform.runLater(() -> {
                    if (mr != null && !mr.isError()) {
                        Utility.createNotification("Envío eliminado", NotificationType.DELETE);
                        loadData();
                    } else {
                        Utility.createAlert("Error", mr == null ? "Sin respuesta" : mr.getMessage(), NotificationType.FAILURE);
                    }
                });
            }).start();
        }
    }

    @FXML
    private void handleAssign() {
        Envio sel = tvEnvios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createAlert("Validación", "Selecciona un envío", NotificationType.INFORMATION);
            return;
        }

        Integer alreadyAssigned = sel.getIdConductor();
        if (alreadyAssigned != null && alreadyAssigned > 0) {
            Utility.createAlert("Validación", "El envío " + sel.getId() + " ya tiene conductor (ID: " + alreadyAssigned + "). Desasigna primero.", NotificationType.INFORMATION);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLDriverSelection.fxml"));
            Parent root = loader.load();
            DriverSelectionController ctrl = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Asignar conductor");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (ctrl != null && ctrl.isConfirmed()) {
                Integer driverId = ctrl.getSelectedDriverId();
                if (driverId != null) {
                    new Thread(() -> {
                        Integer myId = null;
                        if (UserSession.getInstance().getUser() != null) {
                            myId = UserSession.getInstance().getUser().getIdCollaborator();
                        }

                        MessageResponse mr = EnvioImp.assignDriver(sel.getId(), driverId, myId);

                        Platform.runLater(() -> {
                            if (mr != null && !mr.isError()) {
                                Utility.createNotification("Conductor asignado", NotificationType.SUCCESS);
                                loadData();
                            } else {
                                Utility.createAlert("Error", mr.getMessage(), NotificationType.FAILURE);
                            }
                        });
                    }).start();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.createAlert("Error", "No se pudo abrir selector", NotificationType.FAILURE);
        }
    }
}
