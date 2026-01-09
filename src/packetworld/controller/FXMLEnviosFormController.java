package packetworld.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import packetworld.domain.ClientImp;
import packetworld.domain.EnvioImp;
import packetworld.domain.StoreImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Client;
import packetworld.pojo.Envio;
import packetworld.pojo.Paquete;
import packetworld.pojo.Store;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;
import packetworld.utility.UserSession;

public class FXMLEnviosFormController implements Initializable {

    @FXML
    private ComboBox<Client> cbCliente;
    @FXML
    private TextField tfTracking;
    @FXML
    private TextField tfFecha;
    @FXML
    private TextField tfDestino;
    @FXML
    private TextField tfPeso;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;
    @FXML
    private ComboBox<Store> cbSucursal;
    @FXML
    private TextField tfOrigen;
    @FXML
    private TextField tfCosto;
    @FXML
    private TextField tfDestinatarioNombre;
    @FXML
    private TextField tfDestinatarioTelefono;
    @FXML
    private TextField tfCiudadDestino;
    @FXML
    private TextField tfEstadoDestino;
    @FXML
    private HBox driverBox;
    @FXML
    private Label lblAssignedDriver;
    @FXML
    private Button btnUnassignDriver;

    private boolean editMode = false;
    private Envio editingEnvio = null;
    private boolean operationSuccess = false;

    private ObservableList<Client> clientes = FXCollections.observableArrayList();
    private ObservableList<Store> sucursales = FXCollections.observableArrayList();
    private Integer desiredClientId = null;
    private Integer desiredSucursalId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClientComboBox();
        setupSucursalComboBox();

        if (tfTracking != null) {
            tfTracking.setEditable(false);
        }
        if (cbEstado != null) {
            cbEstado.setItems(FXCollections.observableArrayList(
                    "recibido", "procesado", "en_transito", "entregado", "cancelado"
            ));
            cbEstado.getSelectionModel().select("recibido");
        }

        setDefaultFechaNowIfEmpty();
        loadClients();
        loadSucursales();

        if (cbCliente != null) {
            cbCliente.valueProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    try {
                        boolean shouldUpdate = !editMode;

                        if (editMode && editingEnvio != null) {
                            if (!newV.getId().equals(editingEnvio.getIdCliente())) {
                                shouldUpdate = true;
                            }
                        }

                        if (shouldUpdate) {
                            if (tfDestino != null) {
                                tfDestino.setText(newV.getFullAddress() == null ? "" : newV.getFullAddress());
                            }

                            if (tfDestinatarioTelefono != null) {
                                tfDestinatarioTelefono.setText(newV.getPhone() == null ? "" : newV.getPhone());
                            }

                            if (tfDestinatarioNombre != null) {
                                tfDestinatarioNombre.setText(newV.getFullName() == null ? "" : newV.getFullName());
                            }
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    private void setDefaultFechaNowIfEmpty() {
        if (tfFecha == null) {
            return;
        }
        if (tfFecha.getText() == null || tfFecha.getText().trim().isEmpty()) {
            tfFecha.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    public void setEditMode(boolean edit, Envio envio) {
        this.editMode = edit;
        this.editingEnvio = envio;

        if (edit && envio != null) {
            tfTracking.setText(envio.getNumGuia() != null ? envio.getNumGuia() : "");
            tfFecha.setText(envio.getFechaCreacion() != null ? envio.getFechaCreacion() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            tfDestino.setText(envio.getDireccionDestino() != null ? envio.getDireccionDestino() : "");
            tfPeso.setText(envio.getPeso() != null ? String.valueOf(envio.getPeso()) : "");

            tfCosto.setText(envio.getCosto() != null ? String.format("%.2f", envio.getCosto()) : "");

            if (cbEstado != null) {
                cbEstado.getSelectionModel().select(envio.getEstatus() != null ? envio.getEstatus() : "recibido");
            }
            if (tfDestinatarioNombre != null) {
                tfDestinatarioNombre.setText(envio.getDestinatarioNombre());
            }
            if (tfDestinatarioTelefono != null) {
                tfDestinatarioTelefono.setText(envio.getDestinatarioTelefono());
            }
            if (tfCiudadDestino != null) {
                tfCiudadDestino.setText(envio.getCiudadDestino());
            }
            if (tfEstadoDestino != null) {
                tfEstadoDestino.setText(envio.getEstadoDestino());
            }

            if (envio.getIdCliente() != null) {
                Client match = findClientById(envio.getIdCliente());
                if (match != null) {
                    cbCliente.getSelectionModel().select(match);
                } else {
                    desiredClientId = envio.getIdCliente();
                }
            }
            if (envio.getIdSucursalOrigen() != null) {
                Store match = findSucursalById(envio.getIdSucursalOrigen());
                if (match != null) {
                    cbSucursal.getSelectionModel().select(match);
                } else {
                    desiredSucursalId = envio.getIdSucursalOrigen();
                }
            }

            if (cbCliente != null) {
                cbCliente.setDisable(false);
            }

        } else {
            tfTracking.setText("Generado automáticamente...");
            tfFecha.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            tfDestino.setText("");
            tfPeso.setText("");
            tfCosto.setText("");
            if (cbEstado != null) {
                cbEstado.getSelectionModel().select("recibido");
            }
            if (cbCliente != null) {
                cbCliente.getSelectionModel().clearSelection();
                cbCliente.setDisable(false);
            }
            if (cbSucursal != null) {
                cbSucursal.getSelectionModel().clearSelection();
            }
        }

        if (tfTracking != null) {
            tfTracking.setDisable(true);
        }

        updateAssignedDriverSection();
    }

    private void updateAssignedDriverSection() {
        if (lblAssignedDriver == null || btnUnassignDriver == null) {
            return;
        }

        if (editingEnvio == null || editingEnvio.getIdColaboradorActualizo() == null) {
            lblAssignedDriver.setText("—");
            btnUnassignDriver.setVisible(false);
            btnUnassignDriver.setManaged(false);
            if (driverBox != null) {
                driverBox.setVisible(false);
                driverBox.setManaged(false);
            }
        } else {
            lblAssignedDriver.setText("ID: " + editingEnvio.getIdColaboradorActualizo());
            btnUnassignDriver.setVisible(true);
            btnUnassignDriver.setManaged(true);
            if (driverBox != null) {
                driverBox.setVisible(true);
                driverBox.setManaged(true);
            }
        }
    }

    @FXML
    public void handleUnassignDriver(ActionEvent event) {
        if (!editMode || editingEnvio == null) {
            return;
        }
        Integer envioId = editingEnvio.getId();
        if (envioId == null) {
            return;
        }

        boolean confirm = Utility.showConfirmation("Desasignar conductor", "¿Deseas desasignar el conductor del envío " + envioId + " ?");
        if (!confirm) {
            return;
        }

        new Thread(() -> {
            MessageResponse mr = EnvioImp.unassignDriver(envioId);
            Platform.runLater(() -> {
                if (mr != null && !mr.isError()) {
                    editingEnvio.setIdColaboradorActualizo(null);
                    updateAssignedDriverSection();
                    Utility.createNotification("Conductor desasignado", NotificationType.SUCCESS);
                    operationSuccess = true;
                } else {
                    String msg = mr == null ? "Respuesta nula" : mr.getMessage();
                    Utility.createAlert("Error", msg, NotificationType.FAILURE);
                }
            });
        }).start();
    }

    private Client findClientById(Integer id) {
        return clientes.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    private Store findSucursalById(Integer id) {
        return sucursales.stream().filter(s -> s.getIdStore().equals(id)).findFirst().orElse(null);
    }

    private void setupClientComboBox() {
        if (cbCliente == null) {
            return;
        }
        cbCliente.setItems(clientes);
    }

    private void setupSucursalComboBox() {
        if (cbSucursal == null) {
            return;
        }
        cbSucursal.setItems(sucursales);
    }

    private void loadClients() {
        new Thread(() -> {
            HashMap<String, Object> resp = ClientImp.getAll();
            Platform.runLater(() -> {
                if (resp != null && !(boolean) resp.get("error")) {
                    List<Client> list = (List<Client>) resp.get("data");
                    if (list != null) {
                        clientes.setAll(list);
                    }
                    if (desiredClientId != null) {
                        Client match = findClientById(desiredClientId);
                        if (match != null) {
                            cbCliente.getSelectionModel().select(match);
                            desiredClientId = null;
                        }
                    }
                }
            });
        }).start();
    }

    private void loadSucursales() {
        new Thread(() -> {
            List<Store> list = StoreImp.getAll();
            Platform.runLater(() -> {
                if (list != null) {
                    List<Store> active = new ArrayList<>();
                    for (Store s : list) {
                        if (s.isActiva()) {
                            active.add(s);
                        }
                    }
                    sucursales.setAll(active);
                    if (desiredSucursalId != null) {
                        Store match = findSucursalById(desiredSucursalId);
                        if (match != null) {
                            cbSucursal.getSelectionModel().select(match);
                            desiredSucursalId = null;
                        }
                    }
                }
            });
        }).start();
    }

    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    public void setEnvio(Envio envio) {
        setEditMode(true, envio);
    }

    private Integer getLoggedCollaboratorId() {
        UserSession session = UserSession.getInstance();
        if (session != null && session.getUser() != null) {
            return session.getUser().getIdCollaborator();
        }
        return null;
    }

    private String generateTrackingNumber(Store origin, Envio envio) {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String orgPart = (origin != null && origin.getCity() != null && origin.getCity().length() >= 3) ? origin.getCity().substring(0, 3).toUpperCase() : "ORG";
        String destPart = (envio.getCiudadDestino() != null && envio.getCiudadDestino().length() >= 3) ? envio.getCiudadDestino().substring(0, 3).toUpperCase() : "GEN";
        String uniquePart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return String.format("%s-%s-%s-%s", datePart, orgPart, destPart, uniquePart);
    }

    @FXML
    public void handleSave(ActionEvent event) {
        Client selectedClient = cbCliente.getValue();
        Store selectedStore = cbSucursal.getValue();

        if (selectedClient == null || selectedStore == null) {
            Utility.createAlert("Validación", "Cliente y Sucursal de origen son obligatorios", NotificationType.INFORMATION);
            return;
        }
        if (tfDestino.getText().trim().isEmpty()) {
            Utility.createAlert("Validación", "La dirección de destino es obligatoria", NotificationType.INFORMATION);
            return;
        }

        Envio req = new Envio();
        if (editMode && editingEnvio != null) {
            req.setId(editingEnvio.getId());
            req.setNumGuia(editingEnvio.getNumGuia());
        }

        req.setIdCliente(selectedClient.getId());
        req.setIdSucursalOrigen(selectedStore.getIdStore());
        req.setDireccionDestino(tfDestino.getText().trim());
        req.setDestinatarioNombre(tfDestinatarioNombre.getText());
        req.setDestinatarioTelefono(tfDestinatarioTelefono.getText());
        req.setCiudadDestino(tfCiudadDestino.getText());
        req.setEstadoDestino(tfEstadoDestino.getText());
        req.setEstatus(cbEstado.getValue() != null ? cbEstado.getValue() : "recibido");
        req.setFechaCreacion(tfFecha.getText());

        Integer collaboratorId = getLoggedCollaboratorId();
        if (collaboratorId != null) {
            req.setIdColaboradorActualizo(collaboratorId);
        } else {
            System.err.println("ADVERTENCIA: No se encontró usuario en sesión. El campo colaborador quedará nulo.");
        }

        Double pesoVal = 1.0;
        try {
            if (tfPeso.getText() != null && !tfPeso.getText().isEmpty()) {
                pesoVal = Double.parseDouble(tfPeso.getText().trim());
                req.setPeso(pesoVal);
            } else {
                req.setPeso(1.0);
            }
        } catch (NumberFormatException e) {
            Utility.createAlert("Validación", "Peso inválido. Use punto decimal.", NotificationType.INFORMATION);
            return;
        }

        MessageResponse resp = null;

        if (!editMode) {
            String autoGuia = generateTrackingNumber(selectedStore, req);
            req.setNumGuia(autoGuia);

            List<Paquete> paquetes = new ArrayList<>();
            Paquete p = new Paquete();
            p.setDescripcion("Paquete Inicial");
            p.setPeso(pesoVal);
            p.setCantidad(1);
            p.setAlto(10.0);
            p.setAncho(10.0);
            p.setProfundidad(10.0);
            p.setValor(0.0);
            paquetes.add(p);

            resp = EnvioImp.register(req, paquetes);

            if (resp != null && !resp.isError()) {
                this.operationSuccess = true;
                Utility.createNotification("Envío registrado.\nGuía: " + autoGuia + "\n", NotificationType.SUCCESS);
                Utility.closeModal(btnSave);
            } else {
                Utility.createAlert("Error al registrar", resp != null ? resp.getMessage() : "Sin respuesta", NotificationType.FAILURE);
            }
        } else {
            resp = EnvioImp.edit(req);
            if (resp != null && !resp.isError()) {
                this.operationSuccess = true;
                Utility.createNotification("Envío actualizado correctamente", NotificationType.SUCCESS);
                Utility.closeModal(btnSave);
            } else {
                Utility.createAlert("Error al actualizar", resp != null ? resp.getMessage() : "Sin respuesta", NotificationType.FAILURE);
            }
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        Utility.closeModal(btnCancel);
    }
}
