package packetworld.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import packetworld.domain.ClientImp;
import packetworld.domain.EnvioImp;
import packetworld.domain.StoreImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Client;
import packetworld.pojo.Envio;
import packetworld.pojo.Store;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;
import packetworld.pojo.DriverAssignmentSession;

/**
 * Controller for Envío form (create / edit).
 * - populates clients/sucursales
 * - shows assigned driver and allows unassigning from the modal
 * - validates and sends create/edit requests to backend (EnvioImp)
 */
public class FXMLEnviosFormController implements Initializable {

    // -----------------------
    // FXML injected controls
    // -----------------------
    @FXML private ComboBox<Client> cbCliente;
    @FXML private TextField tfTracking;
    @FXML private TextField tfFecha;
    @FXML private TextField tfDestino;
    @FXML private TextField tfPeso;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;

    // Sucursal (origen)
    @FXML private ComboBox<Store> cbSucursal;
    @FXML private TextField tfOrigen;

    // Additional fields added to the form
    @FXML private TextField tfCosto;
    @FXML private TextField tfDestinatarioNombre;
    @FXML private TextField tfDestinatarioTelefono;
    @FXML private TextField tfCiudadDestino;
    @FXML private TextField tfEstadoDestino;
    @FXML private Label lblAssignedDriver;
    @FXML private Button btnUnassignDriver;
    @FXML private Label lblAssignedInfo;

    // Optional "Nuevo cliente" button (may be absent in FXML)
    @FXML private Button btnNewClient;

    // -----------------------
    // Controller state
    // -----------------------
    private boolean editMode = false;
    private Envio editingEnvio = null;
    private boolean operationSuccess = false;

    private ObservableList<Client> clientes = FXCollections.observableArrayList();
    private ObservableList<Store> sucursales = FXCollections.observableArrayList();

    // selección diferida si setEditMode se invoca antes de que se hayan cargado las listas
    private Integer desiredClientId = null;
    private Integer desiredSucursalId = null;

    // Valores exactos del ENUM en la BD (mostrar solo estos)
    private static final ObservableList<String> ESTADOS_BD = FXCollections.observableArrayList(
            "recibido", "procesado", "en_transito", "entregado", "cancelado"
    );

    // ------------------ initialize() ------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupClientComboBox();
        setupSucursalComboBox();

        // tracking no editable
        if (tfTracking != null) tfTracking.setEditable(false);

        // poblar cbEstado con valores exactos del ENUM
        if (cbEstado != null) {
            cbEstado.setItems(FXCollections.observableArrayList(
                    "recibido", "procesado", "en_transito", "entregado", "cancelado"
            ));
            cbEstado.getSelectionModel().select("recibido");
        }

        // si es nuevo por defecto la fecha será ahora
        setDefaultFechaNowIfEmpty();

        // listeners / cargas iniciales
        loadClients();
        loadSucursales();

        // cuando se seleccione un cliente, completar info de destino/destinatario
        if (cbCliente != null) {
            cbCliente.valueProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    try {
                        if (tfDestino != null)
                            tfDestino.setText(newV.getFullAddress() == null ? "" : newV.getFullAddress());
                        if (newV.getTelefono() != null && tfDestinatarioTelefono != null &&
                                (tfDestinatarioTelefono.getText() == null || tfDestinatarioTelefono.getText().isEmpty())) {
                            tfDestinatarioTelefono.setText(newV.getTelefono());
                        }
                        if (tfDestinatarioNombre != null &&
                                (tfDestinatarioNombre.getText() == null || tfDestinatarioNombre.getText().isEmpty())) {
                            String name = null;
                            try { name = newV.getFullName(); } catch (Throwable ex) { name = null; }
                            if (name == null || name.trim().isEmpty()) {
                                try { name = newV.getNombreCompleto(); } catch (Throwable ex) { name = null; }
                            }
                            if (name != null) tfDestinatarioNombre.setText(name);
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        System.out.println("FXMLEnviosFormController initialized");
    }

    private void setDefaultFechaNowIfEmpty() {
        if (tfFecha == null) return;
        if (tfFecha.getText() == null || tfFecha.getText().trim().isEmpty()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now = LocalDateTime.now().format(fmt);
            tfFecha.setText(now);
        }
    }

    // ------------------ setEditMode() ------------------
    public void setEditMode(boolean edit, Envio envio) {
        this.editMode = edit;
        this.editingEnvio = envio;
        System.out.println("setEditMode edit=" + edit + " envioId=" + (envio == null ? "null" : envio.getId()));

        if (edit && envio != null) {
            // Rellenar campos básicos desde el envío
            tfTracking.setText(envio.getNumGuia() != null ? envio.getNumGuia() : "");
            // Usar la fecha de creación del envío en modo edición (si existe), si no usar la actual
            tfFecha.setText(envio.getFechaCreacion() != null ? envio.getFechaCreacion() :
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            tfDestino.setText(envio.getDireccionDestino() != null ? envio.getDireccionDestino() : "");
            tfPeso.setText(envio.getPeso() != null ? String.valueOf(envio.getPeso()) : "");
            tfCosto.setText(envio.getCosto() != null ? String.valueOf(envio.getCosto()) : "");
            if (cbEstado != null)
                cbEstado.getSelectionModel().select(envio.getEstatus() != null ? envio.getEstatus() : "recibido");

            // Destinatario
            if (tfDestinatarioNombre != null)
                tfDestinatarioNombre.setText(envio.getDestinatarioNombre() != null ? envio.getDestinatarioNombre() : "");
            if (tfDestinatarioTelefono != null)
                tfDestinatarioTelefono.setText(envio.getDestinatarioTelefono() != null ? envio.getDestinatarioTelefono() : "");
            if (tfCiudadDestino != null)
                tfCiudadDestino.setText(envio.getCiudadDestino() != null ? envio.getCiudadDestino() : "");
            if (tfEstadoDestino != null)
                tfEstadoDestino.setText(envio.getEstadoDestino() != null ? envio.getEstadoDestino() : "");

            // Cliente: seleccionar si está en la lista o marcar selección diferida
            if (envio.getIdCliente() != null) {
                Client match = findClientById(envio.getIdCliente());
                if (match != null) {
                    cbCliente.getSelectionModel().select(match);
                } else {
                    desiredClientId = envio.getIdCliente();
                }
            } else {
                if (cbCliente != null) cbCliente.getSelectionModel().clearSelection();
            }

            // Sucursal origen: seleccionar si está en la lista o marcar selección diferida
            if (envio.getIdSucursalOrigen() != null) {
                Store match = findSucursalById(envio.getIdSucursalOrigen());
                if (match != null) {
                    cbSucursal.getSelectionModel().select(match);
                } else {
                    desiredSucursalId = envio.getIdSucursalOrigen();
                }
            } else {
                if (cbSucursal != null) cbSucursal.getSelectionModel().clearSelection();
            }

            // DESHABILITAR edición del cliente en modo edición
            if (cbCliente != null) cbCliente.setDisable(true);
            if (btnNewClient != null) {
                btnNewClient.setVisible(false);
                btnNewClient.setManaged(false);
            }

            // Si el envío ya está entregado, bloquear la edición completa
            boolean isDelivered = envio.getEstatus() != null && "entregado".equalsIgnoreCase(envio.getEstatus());
            if (isDelivered) {
                if (cbSucursal != null) cbSucursal.setDisable(true);
                if (tfOrigen != null) tfOrigen.setDisable(true);
                if (tfDestino != null) tfDestino.setDisable(true);
                if (tfPeso != null) tfPeso.setDisable(true);
                if (cbEstado != null) cbEstado.setDisable(true);
                if (btnSave != null) btnSave.setDisable(true);
                // opcional: aviso
                Utility.createAlert("Envío entregado", "Este envío tiene estatus 'entregado' y no puede ser modificado.", NotificationType.INFORMATION);
            } else {
                // modo edición normal (no entregado): permitir editar excepto cliente
                if (cbSucursal != null) cbSucursal.setDisable(false);
                if (tfOrigen != null) tfOrigen.setDisable(false);
                if (tfDestino != null) tfDestino.setDisable(false);
                if (tfPeso != null) tfPeso.setDisable(false);
                if (cbEstado != null) cbEstado.setDisable(false);
                if (btnSave != null) btnSave.setDisable(false);
            }

        } else {
            // modo crear: limpiar campos y permitir seleccionar cliente y mostrar botón nuevo cliente
            tfTracking.setText("");
            // en creación la fecha por defecto es ahora
            tfFecha.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            tfDestino.setText("");
            tfPeso.setText("");
            tfCosto.setText("");
            if (tfDestinatarioNombre != null) tfDestinatarioNombre.setText("");
            if (tfDestinatarioTelefono != null) tfDestinatarioTelefono.setText("");
            if (tfCiudadDestino != null) tfCiudadDestino.setText("");
            if (tfEstadoDestino != null) tfEstadoDestino.setText("");
            if (cbEstado != null) cbEstado.getSelectionModel().select("recibido");
            if (cbCliente != null) cbCliente.getSelectionModel().clearSelection();
            if (cbSucursal != null) cbSucursal.getSelectionModel().clearSelection();
            editingEnvio = null;
            desiredClientId = null;
            desiredSucursalId = null;

            if (cbCliente != null) cbCliente.setDisable(false);
            if (cbSucursal != null) cbSucursal.setDisable(false);
            if (tfOrigen != null) tfOrigen.setDisable(false);
            if (tfDestino != null) tfDestino.setDisable(false);
            if (tfPeso != null) tfPeso.setDisable(false);
            if (cbEstado != null) cbEstado.setDisable(false);
            if (btnSave != null) btnSave.setDisable(false);

            if (btnNewClient != null) {
                btnNewClient.setVisible(true);
                btnNewClient.setManaged(true);
            }
        }

        // Siempre deshabilitar edición del tracking (nunca se edita)
        if (tfTracking != null) {
            tfTracking.setDisable(true);
        }

        // Update assigned driver display after mode change / data populated
        updateAssignedDriverSection();
    }
    public void setEnvio(packetworld.pojo.Envio envio) {
    setEditMode(true, envio);
}

    private void populateFieldsFromClient(Client c) {
        if (c == null) return;
        if (tfDestino != null) tfDestino.setText(c.getFullAddress());
    }

    private void loadClients() {
        new Thread(() -> {
            System.out.println("loadClients() -> requesting clients...");
            HashMap<String, Object> resp = null;
            try {
                resp = ClientImp.getAll();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            final HashMap<String, Object> finalResp = resp;
            Platform.runLater(() -> {
                System.out.println("DEBUG loadClients -> resp = " + finalResp);
                if (finalResp != null) {
                    Object errObj = finalResp.get("error");
                    boolean isError = (errObj instanceof Boolean) ? (Boolean) errObj : true;
                    if (!isError) {
                        @SuppressWarnings("unchecked")
                        List<Client> list = (List<Client>) finalResp.get("data");
                        if (list != null) {
                            clientes.setAll(list);
                            System.out.println("loadClients() -> loaded " + list.size() + " clients");
                        } else {
                            clientes.clear();
                            System.out.println("loadClients() -> data was null, cleared clients list");
                        }

                        // selección diferida si existe desiredClientId
                        if (desiredClientId != null) {
                            Client match = findClientById(desiredClientId);
                            if (match != null) {
                                cbCliente.getSelectionModel().select(match);
                                desiredClientId = null;
                            }
                        }
                    } else {
                        Object msgObj = finalResp.get("message");
                        String msg = msgObj == null ? "No se pudieron cargar clientes" : String.valueOf(msgObj);
                        Utility.createAlert("Error", msg, NotificationType.FAILURE);
                        clientes.clear();
                    }
                } else {
                    Utility.createAlert("Error", "Respuesta nula del servidor al cargar clientes", NotificationType.FAILURE);
                    clientes.clear();
                }
            });
        }).start();
    }

    private Client findClientById(Integer clientId) {
        if (clientId == null) return null;
        for (Client c : clientes) {
            if (c.getId() != null && c.getId().equals(clientId)) return c;
        }
        return null;
    }

    // --------------------------
    // Store (Sucursal) ComboBox
    // --------------------------
    private void setupSucursalComboBox() {
        if (cbSucursal == null) {
            System.err.println("FXMLEnviosFormController: cbSucursal is null — revisa FXMLEnvioForm.fxml fx:id y fx:controller");
            return;
        }
        cbSucursal.setItems(sucursales);

        cbSucursal.setCellFactory(list -> new ListCell<Store>() {
            @Override protected void updateItem(Store item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        cbSucursal.setButtonCell(new ListCell<Store>() {
            @Override protected void updateItem(Store item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        // sincronizar selección con tfOrigen: al seleccionar sucursal, llenar tfOrigen con dirección
        cbSucursal.valueProperty().addListener((obs, oldS, newS) -> {
            if (newS != null && tfOrigen != null) {
                tfOrigen.setText(newS.getFullAddress());
            }
        });
    }

    // Reemplaza solo el método loadSucursales() por este
    private void loadSucursales() {
        new Thread(() -> {
            System.out.println("loadSucursales() -> requesting sucursales...");
            List<Store> list = java.util.Collections.emptyList();
            try {
                list = StoreImp.getAll(); // tu StoreImp.getAll() devuelve List<Store>
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            final List<Store> finalList = (list == null ? java.util.Collections.emptyList() : list);
            Platform.runLater(() -> {
                // FILTRAR sólo SUCURSALES ACTIVAS
                List<Store> onlyActive = new java.util.ArrayList<>();
                for (Store s : finalList) {
                    if (s != null && s.isActiva()) {
                        onlyActive.add(s);
                    }
                }
                sucursales.setAll(onlyActive);

                System.out.println("loadSucursales() -> loaded (active only) " + sucursales.size() + " sucursales");
                if (desiredSucursalId != null) {
                    Store match = findSucursalById(desiredSucursalId);
                    if (match != null) {
                        cbSucursal.getSelectionModel().select(match);
                        desiredSucursalId = null;
                    }
                }
            });
        }).start();
    }

    private Store findSucursalById(Integer idSucursalOrigen) {
        if (idSucursalOrigen == null) return null;
        for (Store s : sucursales) {
            if (s != null && s.getIdStore() != null && s.getIdStore().equals(idSucursalOrigen)) {
                return s;
            }
        }
        return null;
    }

    // --------------------------
    // Guardar / Cancelar
    // --------------------------
    @FXML
    public void handleCancel(ActionEvent event) {
        Utility.closeModal(btnCancel);
    }

    @FXML
    public void handleSave(ActionEvent event) {
        System.out.println("handleSave() called - editMode=" + editMode);

        // Validaciones básicas
        Client selectedClient = cbCliente.getValue();
        if (selectedClient == null) {
            Utility.createAlert("Validación", "Selecciona un cliente registrado antes de continuar", NotificationType.INFORMATION);
            return;
        }

        Store selectedStore = cbSucursal.getValue();
        if (selectedStore == null) {
            Utility.createAlert("Validación", "Selecciona una sucursal de origen", NotificationType.INFORMATION);
            return;
        }

        String destino = tfDestino.getText() != null ? tfDestino.getText().trim() : "";
        if (destino.isEmpty()) {
            Utility.createAlert("Validación", "La dirección de destino es obligatoria", NotificationType.INFORMATION);
            return;
        }

        // Construir objeto Envio con TODOS los campos del formulario
        Envio req = new Envio();

        // Si estamos editando, conservar el id (y opcionalmente numGuia)
        if (editMode && editingEnvio != null) {
            req.setId(editingEnvio.getId());
            // conservar numGuia si el backend lo requiere para la edición
            req.setNumGuia(editingEnvio.getNumGuia());
        }

        // Campos básicos
        req.setIdCliente(selectedClient.getId());
        req.setIdSucursalOrigen(selectedStore.getIdStore());
        req.setDireccionDestino(destino);

        // Costo
        try {
            String sCosto = (tfCosto != null && tfCosto.getText() != null) ? tfCosto.getText().trim() : "";
            if (!sCosto.isEmpty()) {
                req.setCosto(Double.parseDouble(sCosto));
            } else {
                req.setCosto(0.0);
            }
        } catch (NumberFormatException ex) {
            Utility.createAlert("Validación", "Costo inválido", NotificationType.INFORMATION);
            return;
        }

        // Peso
        try {
            String sPeso = (tfPeso != null && tfPeso.getText() != null) ? tfPeso.getText().trim() : "";
            if (!sPeso.isEmpty()) {
                req.setPeso(Double.parseDouble(sPeso));
            } else {
                req.setPeso(0.0);
            }
        } catch (NumberFormatException ex) {
            Utility.createAlert("Validación", "Peso inválido", NotificationType.INFORMATION);
            return;
        }

        // Destinatario y teléfono (si el formulario los tiene, usarlo; si no, tomar del cliente)
        String nombreDest = (tfDestinatarioNombre != null && tfDestinatarioNombre.getText() != null && !tfDestinatarioNombre.getText().trim().isEmpty())
                ? tfDestinatarioNombre.getText().trim()
                : selectedClient.getFullName();
        String telDest = (tfDestinatarioTelefono != null && tfDestinatarioTelefono.getText() != null && !tfDestinatarioTelefono.getText().trim().isEmpty())
                ? tfDestinatarioTelefono.getText().trim()
                : selectedClient.getTelefono();

        req.setDestinatarioNombre(nombreDest);
        req.setDestinatarioTelefono(telDest);

        // Ciudad / Estado destino
        if (tfCiudadDestino != null) req.setCiudadDestino(tfCiudadDestino.getText() == null ? null : tfCiudadDestino.getText().trim());
        if (tfEstadoDestino != null) req.setEstadoDestino(tfEstadoDestino.getText() == null ? null : tfEstadoDestino.getText().trim());

        // Estatus (obligatorio y exactamente el valor del ENUM)
        String estadoBD = (cbEstado != null && cbEstado.getSelectionModel().getSelectedItem() != null)
                ? cbEstado.getSelectionModel().getSelectedItem()
                : null;
        if (estadoBD == null || estadoBD.trim().isEmpty()) {
            Utility.createAlert("Validación", "Selecciona un estatus válido", NotificationType.INFORMATION);
            return;
        }
        req.setEstatus(estadoBD);

        // Fecha creación: si está en el formulario, asignarla, en creación podemos dejarla para el backend
        if (tfFecha != null && tfFecha.getText() != null && !tfFecha.getText().trim().isEmpty()) {
            req.setFechaCreacion(tfFecha.getText().trim());
        }

        // idColaboradorActualizo: si tienes el id del conductor en la asignación, usarlo
        Integer loggedUserId = getLoggedCollaboratorId();
        System.out.println("DEBUG: getLoggedCollaboratorId -> " + loggedUserId);
        if (loggedUserId != null) req.setIdColaboradorActualizo(loggedUserId);

        MessageResponse resp = null;

        if (!editMode) {
            // Crear nuevo envío
            resp = EnvioImp.register(req);
            if (resp != null && !resp.isError()) {
                this.operationSuccess = true;
                Utility.createNotification("Envío creado", NotificationType.SUCCESS);
                Utility.closeModal(btnSave);
            } else {
                String msg = resp == null ? "Respuesta nula del servidor" : resp.getMessage();
                Utility.createAlert("Error en base de datos", msg, NotificationType.FAILURE);
            }
        } else {
            // Edición: si cambió sólo el estatus conviene llamar al endpoint changeStatus (si tu backend lo requiere)
            boolean statusChanged = editingEnvio != null && editingEnvio.getEstatus() != null
                    ? !editingEnvio.getEstatus().equalsIgnoreCase(req.getEstatus())
                    : (editingEnvio != null && editingEnvio.getEstatus() == null && req.getEstatus() != null);

            if (statusChanged && req.getId() != null) {
                // Llamada específica para cambiar estatus (si tu backend lo maneja así)
                MessageResponse statusResp = EnvioImp.changeStatus(req.getId(), req.getEstatus(), loggedUserId);
                if (statusResp != null && !statusResp.isError()) {
                    // Intentar también actualizar el resto
                    resp = EnvioImp.edit(req);
                } else {
                    String msg = statusResp == null ? "Respuesta nula del servidor" : statusResp.getMessage();
                    Utility.createAlert("Error al cambiar estatus", msg, NotificationType.FAILURE);
                    return;
                }
            } else {
                // No cambió estatus o no se requiere endpoint separado: enviar la edición completa
                resp = EnvioImp.edit(req);
            }

            if (resp != null && !resp.isError()) {
                this.operationSuccess = true;
                Utility.createNotification("Envío actualizado", NotificationType.SUCCESS);
                Utility.closeModal(btnSave);
            } else {
                String msg = resp == null ? "Respuesta nula del servidor" : resp.getMessage();
                Utility.createAlert("Error al actualizar envío", msg, NotificationType.FAILURE);
            }
        }
    }

    // handler para posibles clicks (no usado aquí)
    @FXML
    private void handleClearSearch(MouseEvent event) {
        // no implementado en este modal
    }

    // --------------------------
    // Cliente ComboBox (implementación segura)
    // --------------------------
    private void setupClientComboBox() {
        try {
            if (cbCliente == null) {
                System.err.println("FXMLEnviosFormController.setupClientComboBox: cbCliente es null (FXML no inyectado)");
                return;
            }

            // Lista ya creada en el controlador: 'clientes' (ObservableList<Client>)
            cbCliente.setItems(clientes);

            // Cómo se muestran las filas en el desplegable
            cbCliente.setCellFactory(listView -> new ListCell<Client>() {
                @Override
                protected void updateItem(Client item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String label;
                        try {
                            label = item.getFullName();
                            if (label == null || label.trim().isEmpty()) {
                                label = (item.getNombre() == null ? "" : item.getNombre()) + " " + (item.getApellido() == null ? "" : item.getApellido());
                            }
                        } catch (Throwable ex) {
                            label = (item.getNombre() == null ? "" : item.getNombre()) + " " + (item.getApellido() == null ? "" : item.getApellido());
                        }
                        setText(label.trim());
                    }
                }
            });

            // Texto que se muestra en el botón (valor seleccionado)
            cbCliente.setButtonCell(new ListCell<Client>() {
                @Override
                protected void updateItem(Client item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String label;
                        try {
                            label = item.getFullName();
                            if (label == null || label.trim().isEmpty()) {
                                label = (item.getNombre() == null ? "" : item.getNombre()) + " " + (item.getApellido() == null ? "" : item.getApellido());
                            }
                        } catch (Throwable ex) {
                            label = (item.getNombre() == null ? "" : item.getNombre()) + " " + (item.getApellido() == null ? "" : item.getApellido());
                        }
                        setText(label.trim());
                    }
                }
            });

            // cuando cambie la selección, rellenar destino/destinatario si están vacíos
            cbCliente.valueProperty().addListener((obs, oldV, newV) -> {
                try {
                    if (newV == null) return;

                    // Rellenar dirección destino si el campo está vacío
                    if (tfDestino != null && (tfDestino.getText() == null || tfDestino.getText().trim().isEmpty())) {
                        String addr = "";
                        try { addr = newV.getFullAddress(); } catch (Throwable ex) { addr = null; }
                        if (addr == null || addr.trim().isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            if (newV.getCalle() != null && !newV.getCalle().trim().isEmpty()) sb.append(newV.getCalle().trim());
                            if (newV.getNumExt() != null && !newV.getNumExt().trim().isEmpty()) sb.append(" ").append(newV.getNumExt().trim());
                            if (newV.getColonia() != null && !newV.getColonia().trim().isEmpty()) sb.append(", ").append(newV.getColonia().trim());
                            if (newV.getCiudad() != null && !newV.getCiudad().trim().isEmpty()) sb.append(", ").append(newV.getCiudad().trim());
                            addr = sb.toString().trim();
                        }
                        if (addr != null && !addr.trim().isEmpty()) tfDestino.setText(addr);
                    }

                    // Rellenar teléfono destinatario si está vacío
                    if (tfDestinatarioTelefono != null && (tfDestinatarioTelefono.getText() == null || tfDestinatarioTelefono.getText().trim().isEmpty())) {
                        try {
                            String telefono = newV.getTelefono();
                            if (telefono != null && !telefono.trim().isEmpty()) tfDestinatarioTelefono.setText(telefono);
                        } catch (Throwable ex) { /* ignore */ }
                    }

                    // Rellenar nombre destinatario si está vacío
                    if (tfDestinatarioNombre != null && (tfDestinatarioNombre.getText() == null || tfDestinatarioNombre.getText().trim().isEmpty())) {
                        String fullname = null;
                        try { fullname = newV.getFullName(); } catch (Throwable ex) { fullname = null; }
                        if (fullname == null || fullname.trim().isEmpty()) {
                            try { fullname = newV.getNombreCompleto(); } catch (Throwable ex) { fullname = null; }
                        }
                        if (fullname != null && !fullname.trim().isEmpty()) tfDestinatarioNombre.setText(fullname);
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Throwable ex) {
            ex.printStackTrace();
            System.err.println("Error en setupClientComboBox: " + ex.getMessage());
        }
    }

    // getter que espera Utility.openAnimatedModal
    public boolean isOperationSuccess() {
        return this.operationSuccess;
    }

    /**
     * Obtiene el id del colaborador que realiza la acción.
     * Lee directamente desde DriverAssignmentSession (POJO) donde guardas la asignación.
     */
    private Integer getLoggedCollaboratorId() {
        try {
            return DriverAssignmentSession.getCurrentDriverId();
        } catch (Throwable ex) {
            return null;
        }
    }

    private void updateAssignedDriverSection() {
        if (lblAssignedDriver == null || lblAssignedInfo == null || btnUnassignDriver == null) return;

        if (editingEnvio == null) {
            lblAssignedDriver.setText("—");
            lblAssignedInfo.setText("");
            btnUnassignDriver.setVisible(false);
            btnUnassignDriver.setManaged(false);
            return;
        }
        Integer driverId = editingEnvio.getIdColaboradorActualizo();
        if (driverId == null) {
            lblAssignedDriver.setText("No hay conductor asignado");
            lblAssignedInfo.setText("");
            btnUnassignDriver.setVisible(false);
            btnUnassignDriver.setManaged(false);
        } else {
            lblAssignedDriver.setText("ID: " + driverId);
            lblAssignedInfo.setText("Conductor asignado (id " + driverId + ").");
            btnUnassignDriver.setVisible(true);
            btnUnassignDriver.setManaged(true);
        }
    }

    @FXML
    public void handleUnassignDriver(ActionEvent event) {
        if (!editMode || editingEnvio == null) return;
        Integer envioId = editingEnvio.getId();
        if (envioId == null) return;

        boolean confirm = Utility.showConfirmation("Desasignar conductor", "¿Deseas desasignar el conductor del envío " + envioId + " ?");
        if (!confirm) return;

        // Ejecutar en background
        new Thread(() -> {
            MessageResponse mr = EnvioImp.unassignDriver(envioId);
            Platform.runLater(() -> {
                if (mr != null && !mr.isError()) {
                    // Actualización optimista local
                    editingEnvio.setIdColaboradorActualizo(null);
                    updateAssignedDriverSection();
                    Utility.createNotification(mr.getMessage() == null || mr.getMessage().isEmpty() ? "Conductor desasignado" : mr.getMessage(), NotificationType.SUCCESS);
                    operationSuccess = true;
                } else {
                    String msg = mr == null ? "Respuesta nula del servidor" : mr.getMessage();
                    Utility.createAlert("Error", msg, NotificationType.FAILURE);
                }
            });
        }).start();
    }
}