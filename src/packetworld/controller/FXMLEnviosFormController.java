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
import javafx.scene.layout.VBox;
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
    @FXML
    private VBox boxDetallesPaquete;
    @FXML
    private TextField tfAlto;
    @FXML
    private TextField tfAncho;
    @FXML
    private TextField tfProfundidad;
    @FXML
    private TextField tfDescripcionPaquete;

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
        if (tfFecha != null && (tfFecha.getText() == null || tfFecha.getText().trim().isEmpty())) {
            tfFecha.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    public void setEditMode(boolean edit, Envio envio) {
        this.editMode = edit;
        this.editingEnvio = envio;

        System.out.println("DEBUG FORM: setEditMode invocado. Edit=" + edit);

        if (edit && envio != null) {

            tfTracking.setText(envio.getNumGuia());
            tfFecha.setText(envio.getFechaCreacion());
            tfDestino.setText(envio.getDireccionDestino());
            tfPeso.setText(envio.getPeso() != null ? String.valueOf(envio.getPeso()) : "");
            tfCosto.setText(envio.getCosto() != null ? String.format("%.2f", envio.getCosto()) : "");

            if (cbEstado != null) {
                cbEstado.getSelectionModel().select(envio.getEstatus());
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

            System.out.println("DEBUG FORM: ------------------------------------------------");
            if (envio.getIdSucursalOrigen() != null) {
                Store match = findSucursalById(envio.getIdSucursalOrigen());
                if (match != null) {
                    cbSucursal.getSelectionModel().select(match);
                } else {
                    desiredSucursalId = envio.getIdSucursalOrigen();
                }
            } else {
                System.err.println("DEBUG FORM: ERROR FATAL -> envio.getIdSucursalOrigen() es NULL.");
            }
            System.out.println("DEBUG FORM: ------------------------------------------------");

            if (cbCliente != null) {
                cbCliente.setDisable(false);
            }

            // --- NUEVO: OCULTAR SECCIÓN DE PAQUETE EN EDICIÓN ---
            if (boxDetallesPaquete != null) {
                boxDetallesPaquete.setVisible(false);
                boxDetallesPaquete.setManaged(false);
            }

        } else {

            tfTracking.setText("Generado automáticamente...");
            tfFecha.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            if (cbCliente != null) {
                cbCliente.getSelectionModel().clearSelection();
                cbCliente.setDisable(false);
            }
            if (cbSucursal != null) {
                cbSucursal.getSelectionModel().clearSelection();
            }

            if (boxDetallesPaquete != null) {
                boxDetallesPaquete.setVisible(true);
                boxDetallesPaquete.setManaged(true);
            }
            if (tfAlto != null) {
                tfAlto.clear();
            }
            if (tfAncho != null) {
                tfAncho.clear();
            }
            if (tfProfundidad != null) {
                tfProfundidad.clear();
            }
            if (tfDescripcionPaquete != null) {
                tfDescripcionPaquete.clear();
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

        if (editingEnvio == null || editingEnvio.getIdConductor() == null) {
            lblAssignedDriver.setText("—");
            btnUnassignDriver.setVisible(false);
            btnUnassignDriver.setManaged(false);
            if (driverBox != null) {
                driverBox.setVisible(false);
                driverBox.setManaged(false);
            }
        } else {
            lblAssignedDriver.setText("ID: " + editingEnvio.getIdConductor());
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

        boolean confirm = Utility.showConfirmation("Desasignar conductor", "¿Deseas desasignar el conductor?");
        if (!confirm) {
            return;
        }

        new Thread(() -> {
            final Integer myId = (UserSession.getInstance().getUser() != null)
                    ? UserSession.getInstance().getUser().getIdCollaborator()
                    : null;

            MessageResponse mr = EnvioImp.unassignDriver(envioId, myId);

            Platform.runLater(() -> {
                if (mr != null && !mr.isError()) {
                    editingEnvio.setIdConductor(null);

                    editingEnvio.setIdColaboradorActualizo(myId);

                    updateAssignedDriverSection();
                    Utility.createNotification("Conductor desasignado", NotificationType.SUCCESS);
                } else {
                    String msg = mr == null ? "Sin respuesta" : mr.getMessage();
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
        if (cbCliente != null) {
            cbCliente.setItems(clientes);
        }
    }

    private void setupSucursalComboBox() {
        if (cbSucursal != null) {
            cbSucursal.setItems(sucursales);
        }
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
        System.out.println("DEBUG UI: Iniciando hilo de carga de sucursales...");

        new Thread(() -> {
            List<Store> list = StoreImp.getAll();

            if (list == null) {
                System.out.println("DEBUG API: StoreImp.getAll() devolvió NULL.");
            } else {
                System.out.println("DEBUG API: StoreImp.getAll() devolvió " + list.size() + " registros.");
                for (Store s : list) {
                    System.out.println("   -> API Trajo: ID=" + s.getIdStore() + " | Nombre=" + s.getName() + " | Estatus=" + s.getStatus());
                }
            }

            Platform.runLater(() -> {
                if (list != null) {
                    List<Store> active = new ArrayList<>();
                    for (Store s : list) {
                        if (s.isActiva()) {
                            active.add(s);
                        } else {
                            System.out.println("DEBUG UI: Filtrando (ocultando) sucursal inactiva ID=" + s.getIdStore());
                        }
                    }
                    sucursales.setAll(active);
                    System.out.println("DEBUG UI: ComboBox poblado con " + sucursales.size() + " sucursales activas.");

                    if (desiredSucursalId != null) {
                        System.out.println("DEBUG UI: Intentando selección diferida para ID: " + desiredSucursalId);
                        Store match = findSucursalById(desiredSucursalId);

                        if (match != null) {
                            cbSucursal.getSelectionModel().select(match);
                            System.out.println("DEBUG UI: ¡ÉXITO! Sucursal seleccionada diferidamente: " + match.getName());
                            desiredSucursalId = null;
                        } else {
                            System.err.println("DEBUG UI: FALLO CRÍTICO. El ID " + desiredSucursalId + " no se encontró en la lista de sucursales activas del ComboBox.");
                            System.err.println("          Posible causa: La sucursal origen está 'Inactiva' o el ID no coincide.");
                        }
                    }
                } else {
                    System.err.println("DEBUG UI: No se pudieron cargar sucursales (Lista nula).");
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
        if (selectedClient.getId() == null || selectedClient.getId() == 0) {
            Utility.createAlert("Error de Datos", "Cliente inválido. Recargue.", NotificationType.FAILURE);
            return;
        }
        if (tfDestino.getText().trim().isEmpty()) {
            Utility.createAlert("Validación", "Destino obligatorio", NotificationType.INFORMATION);
            return;
        }

        Envio req = new Envio();

        if (editMode && editingEnvio != null) {
            req.setId(editingEnvio.getId());
            req.setNumGuia(editingEnvio.getNumGuia());
            req.setIdConductor(editingEnvio.getIdConductor());
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

        Integer loggedUser = getLoggedCollaboratorId();
        if (loggedUser != null) {
            req.setIdColaboradorActualizo(loggedUser);
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
            Utility.createAlert("Validación", "Peso inválido", NotificationType.INFORMATION);
            return;
        }

        MessageResponse resp = null;

        if (!editMode) {
            String autoGuia = generateTrackingNumber(selectedStore, req);
            req.setNumGuia(autoGuia);

            List<Paquete> paquetes = new ArrayList<>();
            Paquete p = new Paquete();

            try {
                double alto = (tfAlto.getText().isEmpty()) ? 10.0 : Double.parseDouble(tfAlto.getText().trim());
                double ancho = (tfAncho.getText().isEmpty()) ? 10.0 : Double.parseDouble(tfAncho.getText().trim());
                double prof = (tfProfundidad.getText().isEmpty()) ? 10.0 : Double.parseDouble(tfProfundidad.getText().trim());

                if (alto <= 0 || ancho <= 0 || prof <= 0) {
                    throw new NumberFormatException();
                }

                p.setAlto(alto);
                p.setAncho(ancho);
                p.setProfundidad(prof);
            } catch (NumberFormatException e) {
                Utility.createAlert("Validación", "Las dimensiones deben ser números válidos mayores a 0", NotificationType.INFORMATION);
                return;
            }

            String desc = tfDescripcionPaquete.getText().trim();
            p.setDescripcion(desc.isEmpty() ? "Paquete Inicial" : desc);
            p.setPeso(pesoVal);
            p.setCantidad(1);
            p.setValor(0.0);

            paquetes.add(p);

            resp = EnvioImp.register(req, paquetes);
        } else {
            resp = EnvioImp.edit(req);
        }

        if (resp != null && !resp.isError()) {
            this.operationSuccess = true;
            Utility.createNotification(!editMode ? "Envío registrado." : "Envío actualizado.", NotificationType.SUCCESS);
            Utility.closeModal(btnSave);
        } else {
            Utility.createAlert("Error", resp != null ? resp.getMessage() : "Sin respuesta", NotificationType.FAILURE);
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        Utility.closeModal(btnCancel);
    }
}
