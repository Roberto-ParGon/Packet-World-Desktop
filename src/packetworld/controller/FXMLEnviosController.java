package packetworld.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import packetworld.utility.Utility;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador de la vista "Envíos" (tabla).
 * - Carga envíos desde la API (EnvioImp.getAll)
 * - Configura columnas para mostrar valores null como "" y envolver texto largo
 * - Abre el modal de creación/edición y refresca la tabla si hubo cambios
 */
public class FXMLEnviosController implements Initializable {

    @FXML private TableView<Envio> tvEnvios;

    @FXML private TableColumn<Envio, String> colId;
    @FXML private TableColumn<Envio, String> colIdCliente;
    @FXML private TableColumn<Envio, String> colGuia;
    @FXML private TableColumn<Envio, String> colDestinatario;
    @FXML private TableColumn<Envio, String> colTelefono;
    @FXML private TableColumn<Envio, String> colDireccion;
    @FXML private TableColumn<Envio, String> colCiudad;
    @FXML private TableColumn<Envio, String> colEstadoDestino;
    @FXML private TableColumn<Envio, String> colSucursalOrigen;
    @FXML private TableColumn<Envio, String> colCosto;
    @FXML private TableColumn<Envio, String> colPeso;
    @FXML private TableColumn<Envio, String> colEstatus;
    @FXML private TableColumn<Envio, String> colFechaCreacion;
    @FXML private TableColumn<Envio, String> colFechaActualizacion;
    @FXML private TableColumn<Envio, String> colColaborador; // muestra id del conductor si existe

    @FXML private TextField tfSearch;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnAsignar;
    @FXML private Button btnEliminar;

    private ObservableList<Envio> envios = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTable();
        loadData();
        wireUi();
    }

    private void wireUi() {
        // Habilitar/deshabilitar botones según selección
        tvEnvios.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            boolean has = newS != null;
            btnEditar.setDisable(!has);
            btnAsignar.setDisable(!has);
            btnEliminar.setDisable(!has);
        });

        // Búsqueda simple por guía / destinatario / dirección
        if (tfSearch != null) {
            tfSearch.textProperty().addListener((obs, oldText, newText) -> {
                // Simple filtering: recarga completo y filtra in-memory
                filterTable(newText == null ? "" : newText.trim().toLowerCase());
            });
        }
    }

    private void filterTable(String q) {
        if (q.isEmpty()) {
            // recargar original (llamar loadData ya pobró envios list)
            // Already envios contains current list - no separate fullList kept; reload from API for simplicity
            loadData();
            return;
        }
        ObservableList<Envio> filtered = FXCollections.observableArrayList();
        for (Envio e : envios) {
            if (e == null) continue;
            String guia = e.getNumGuia() == null ? "" : e.getNumGuia().toLowerCase();
            String dest = e.getDestinatarioNombre() == null ? "" : e.getDestinatarioNombre().toLowerCase();
            String direccion = e.getDireccionDestino() == null ? "" : e.getDireccionDestino().toLowerCase();
            if (guia.contains(q) || dest.contains(q) || direccion.contains(q)) filtered.add(e);
        }
        tvEnvios.setItems(filtered);
    }

    // Configura las columnas para que muestren "" en lugar de null y apliquen wrapping donde convenga
    private void configureTable() {
        tvEnvios.setItems(envios);

        colId.setCellValueFactory(nullSafeValue(e -> e.getId() == null ? "" : String.valueOf(e.getId())));
        colIdCliente.setCellValueFactory(nullSafeValue(e -> e.getIdCliente() == null ? "" : String.valueOf(e.getIdCliente())));
        colGuia.setCellValueFactory(nullSafeValue(Envio::getNumGuia));

        colDestinatario.setCellValueFactory(nullSafeValue(Envio::getDestinatarioNombre));
        applyWrappingCellFactory(colDestinatario);

        colTelefono.setCellValueFactory(nullSafeValue(Envio::getDestinatarioTelefono));

        colDireccion.setCellValueFactory(nullSafeValue(Envio::getDireccionDestino));
        applyWrappingCellFactory(colDireccion);

        colCiudad.setCellValueFactory(nullSafeValue(Envio::getCiudadDestino));
        colEstadoDestino.setCellValueFactory(nullSafeValue(Envio::getEstadoDestino));

        colSucursalOrigen.setCellValueFactory(nullSafeValue(e -> e.getIdSucursalOrigen() == null ? "" : String.valueOf(e.getIdSucursalOrigen())));

        colCosto.setCellValueFactory(nullSafeValue(e -> {
            Double c = e.getCosto();
            return c == null ? "" : String.format("%.2f", c);
        }));

        colPeso.setCellValueFactory(nullSafeValue(e -> {
            Double p = e.getPeso();
            return p == null ? "" : String.format("%.2f", p);
        }));

        colEstatus.setCellValueFactory(nullSafeValue(Envio::getEstatus));

        colFechaCreacion.setCellValueFactory(nullSafeValue(Envio::getFechaCreacion));
        colFechaActualizacion.setCellValueFactory(nullSafeValue(Envio::getFechaActualizacion));

        // Colaborador: mostrar id del colaborador/conductor asignado (si existe) o cadena vacía
        colColaborador.setCellValueFactory(nullSafeValue(e -> {
            Integer id = e.getIdColaboradorActualizo();
            return id == null ? "" : String.valueOf(id);
        }));

        tvEnvios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Helper: crea un Callback<CellDataFeatures, ObservableValue<String>> que evita nulls
    private <T> Callback<TableColumn.CellDataFeatures<Envio, String>, javafx.beans.value.ObservableValue<String>> nullSafeValue(java.util.function.Function<Envio, T> getter) {
        return cell -> {
            Envio e = cell.getValue();
            T v = e == null ? null : getter.apply(e);
            String s = (v == null) ? "" : String.valueOf(v);
            return new SimpleStringProperty(s);
        };
    }

    // CellFactory para columnas de texto largo que permiten wrap
    private void applyWrappingCellFactory(TableColumn<Envio, String> col) {
        col.setCellFactory(new Callback<TableColumn<Envio,String>, TableCell<Envio,String>>() {
            @Override
            public TableCell<Envio, String> call(TableColumn<Envio, String> tc) {
                return new TableCell<Envio, String>() {
                    private final Text text = new Text();
                    {
                        // binding para que el texto haga wrap dentro de la columna
                        text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
                        setGraphic(text);
                    }
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null || item.trim().isEmpty()) {
                            text.setText("");
                        } else {
                            text.setText(item);
                        }
                    }
                };
            }
        });
    }

    // Carga envíos en background y actualiza la lista observable en UI thread
    public void loadData() {
        new Thread(() -> {
            System.out.println("loadData() -> requesting envios...");
            HashMap<String, Object> resp = EnvioImp.getAll();
            Platform.runLater(() -> {
                if (resp == null) {
                    Utility.createAlert("Error", "Respuesta nula del servidor al cargar envíos", NotificationType.FAILURE);
                    envios.clear();
                    return;
                }
                Object errObj = resp.get("error");
                boolean isError = (errObj instanceof Boolean) ? (Boolean) errObj : true;
                if (isError) {
                    Object msgObj = resp.get("message");
                    String msg = msgObj == null ? "Error al cargar envíos" : String.valueOf(msgObj);
                    Utility.createAlert("Error", msg, NotificationType.FAILURE);
                    envios.clear();
                    return;
                }
                @SuppressWarnings("unchecked")
                List<Envio> list = (List<Envio>) resp.get("data");
                if (list == null) {
                    envios.clear();
                } else {
                    envios.setAll(list);
                    // Ensure table view shows full list (if search field is empty)
                    if (tfSearch == null || tfSearch.getText() == null || tfSearch.getText().trim().isEmpty()) {
                        tvEnvios.setItems(envios);
                    } else {
                        filterTable(tfSearch.getText().trim().toLowerCase());
                    }
                }
            });
        }).start();
    }

    // Abre modal para crear nuevo envio
    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLEnvioForm.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Envío");
            stage.setScene(new Scene(root));
            // set create mode
            if (controller instanceof packetworld.controller.FXMLEnviosFormController) {
                ((packetworld.controller.FXMLEnviosFormController) controller).setEditMode(false, null);
            }
            stage.showAndWait();
            // after close, check result
            if (controller instanceof packetworld.controller.FXMLEnviosFormController) {
                boolean ok = ((packetworld.controller.FXMLEnviosFormController) controller).isOperationSuccess();
                if (ok) loadData();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Utility.createAlert("Error", "No se pudo abrir el formulario de envío", NotificationType.FAILURE);
        }
    }

    // Abre modal para editar envio seleccionado
    @FXML
    private void handleEdit() {
        Envio sel = tvEnvios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createAlert("Validación", "Selecciona un envío para editar", NotificationType.INFORMATION);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLEnvioForm.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Envío");
            stage.setScene(new Scene(root));
            if (controller instanceof packetworld.controller.FXMLEnviosFormController) {
                // use setEnvio which delegates to setEditMode(true, envio)
                ((packetworld.controller.FXMLEnviosFormController) controller).setEnvio(sel);
            }
            stage.showAndWait();
            if (controller instanceof packetworld.controller.FXMLEnviosFormController) {
                boolean ok = ((packetworld.controller.FXMLEnviosFormController) controller).isOperationSuccess();
                if (ok) loadData();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Utility.createAlert("Error", "No se pudo abrir el formulario de edición", NotificationType.FAILURE);
        }
    }

    // Eliminar envío seleccionado (confirmación y llamada a API)
    @FXML
    private void handleDelete() {
        Envio sel = tvEnvios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createAlert("Validación", "Selecciona un envío para eliminar", NotificationType.INFORMATION);
            return;
        }
        boolean confirm = Utility.showConfirmation("Eliminar envío", "¿Eliminar envío id " + sel.getId() + " ?");
        if (!confirm) return;

        new Thread(() -> {
            MessageResponse mr = EnvioImp.delete(sel.getId());
            Platform.runLater(() -> {
                if (mr != null && !mr.isError()) {
                    Utility.createNotification("Envío eliminado", NotificationType.SUCCESS);
                    loadData();
                } else {
                    String msg = mr == null ? "Respuesta nula del servidor" : mr.getMessage();
                    Utility.createAlert("Error al eliminar", msg, NotificationType.FAILURE);
                }
            });
        }).start();
    }

    // Método público para que otros controladores puedan forzar recarga
    public void refresh() {
        loadData();
    }

    @FXML
    private void handleClearSearch(MouseEvent event) {
        try {
            if (tfSearch != null) {
                tfSearch.clear();
            }
            // restaurar la lista completa en la tabla
            tvEnvios.setItems(envios); // 'envios' es la ObservableList con todos los envíos
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    // Abre modal para seleccionar conductor y asignarlo (uso de FXMLDriverSelection)
     // Abre modal para seleccionar conductor y asignarlo (uso de FXMLDriverSelection)
    @FXML
    private void handleAssign() {
        Envio sel = tvEnvios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createAlert("Validación", "Selecciona un envío para asignar conductor", NotificationType.INFORMATION);
            return;
        }

        // Si el envío ya tiene conductor no intentamos asignar: mostrar mensaje claro
        Integer alreadyAssigned = sel.getIdColaboradorActualizo();
        if (alreadyAssigned != null) {
            Utility.createAlert("Validación", "El envío " + sel.getId() + " ya está asignado al conductor con ID " + alreadyAssigned + ". Desasigna al conductor antes de reasignar.", NotificationType.INFORMATION);
            return;
        }

        String fxmlPath = "/packetworld/view/FXMLDriverSelection.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        System.out.println("DEBUG handleAssign -> loading FXML: " + fxmlPath + " -> URL=" + fxmlUrl);

        try {
            FXMLLoader loader;
            Parent root;
            if (fxmlUrl != null) {
                loader = new FXMLLoader(fxmlUrl);
                root = loader.load();
            } else {
                // fallback: try to load by stream and set location (avoids Location is not set)
                java.io.InputStream is = getClass().getResourceAsStream(fxmlPath);
                if (is == null) {
                    String msg = "No se encontró " + fxmlPath + " en el classpath. Verifica que el archivo exista en resources/packetworld/view/";
                    System.err.println("handleAssign: " + msg);
                    Utility.createAlert("Error", "No se pudo abrir selector de conductores:\n" + msg, NotificationType.FAILURE);
                    return;
                }
                loader = new FXMLLoader();
                try { loader.setLocation(fxmlUrl); } catch (Throwable ex) { /* ignore */ }
                root = loader.load(is);
                is.close();
            }

            DriverSelectionController ctrl = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Asignar conductor");
            Scene scene = new Scene(root);
            try {
                scene.getStylesheets().add(Utility.class.getResource("/packetworld/resources/styles/collaborators.css").toExternalForm());
            } catch (Throwable ex) { /* ignore if not found */ }
            stage.setScene(scene);
            stage.showAndWait();

            if (ctrl != null && ctrl.isConfirmed()) {
                Integer driverId = ctrl.getSelectedDriverId();
                if (driverId == null) {
                    Utility.createAlert("Validación", "No se seleccionó conductor", NotificationType.INFORMATION);
                    return;
                }

                // Validación adicional: no permitir asignar un conductor que ya tenga otro envío asignado
                for (Envio e : envios) {
                    if (e == null) continue;
                    Integer assigned = e.getIdColaboradorActualizo();
                    if (assigned != null && assigned.equals(driverId) && !e.getId().equals(sel.getId())) {
                        Utility.createAlert("Validación", "El conductor con ID " + driverId + " ya tiene asignado el envío ID " + e.getId() + ". No puede asignarse a otro envío.", NotificationType.INFORMATION);
                        return;
                    }
                }

                boolean confirm = Utility.showConfirmation("Confirmar asignación", "Asignar conductor " + driverId + " al envío " + sel.getId() + " ?");
                if (!confirm) return;

                // llamar al backend (en background)
                new Thread(() -> {
                    System.out.println("DEBUG handleAssign -> calling EnvioImp.assignDriver envioId=" + sel.getId() + " driverId=" + driverId);
                    packetworld.dto.MessageResponse mr = EnvioImp.assignDriver(sel.getId(), driverId);
                    Platform.runLater(() -> {
                        if (mr != null && !mr.isError()) {
                            Utility.createNotification(mr.getMessage() == null || mr.getMessage().isEmpty() ? "Conductor asignado" : mr.getMessage(), NotificationType.SUCCESS);

                            // Optimistic UI update: poner el id del colaborador en el objeto local y refrescar la tabla
                            try {
                                sel.setIdColaboradorActualizo(driverId);
                                tvEnvios.refresh();
                            } catch (Throwable ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            // Mapear mensajes técnicos a algo entendible
                            String msg = mr == null ? "Respuesta nula del servidor" : mr.getMessage();
                            if (msg != null && (msg.toLowerCase().contains("duplicate") || msg.toLowerCase().contains("duplicate entry") || msg.toLowerCase().contains("unique constraint"))) {
                                msg = "El envío ya está asignado a un conductor.";
                            }
                            Utility.createAlert("Error al asignar envío", msg, NotificationType.FAILURE);
                        }
                    });
                }).start();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.createAlert("Error", "No se pudo abrir selector de conductores: " + ex.getMessage(), NotificationType.FAILURE);
        }
    }
}