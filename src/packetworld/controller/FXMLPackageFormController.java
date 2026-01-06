package packetworld.controller;

import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import packetworld.domain.EnvioImp;
import packetworld.domain.PaqueteImp; // asume que existe
import packetworld.dto.MessageResponse;
import packetworld.pojo.Envio;
import packetworld.pojo.Paquete;
import packetworld.pojo.ResponseHTTP;
import packetworld.utility.Utility;
import packetworld.utility.NotificationType;

import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller del formulario de Paquete.
 */
public class FXMLPackageFormController implements Initializable {

    @FXML private ComboBox<Envio> cbEnvio;
    @FXML private TextField tfDescripcion;
    @FXML private TextField tfPeso;
    @FXML private TextField tfAlto;
    @FXML private TextField tfAncho;
    @FXML private TextField tfProfundidad;
    @FXML private TextField tfCantidad;
    @FXML private TextField tfValor;

    private boolean editMode = false;
    private Paquete editingPaquete = null;
    private ObservableList<Envio> enviosList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar ComboBox para mostrar label y manejar selección
        cbEnvio.setItems(enviosList);

cbEnvio.setCellFactory(listView -> new javafx.scene.control.ListCell<packetworld.pojo.Envio>() {
    @Override
    protected void updateItem(packetworld.pojo.Envio item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            String id = item.getId() == null ? "?" : item.getId().toString();
            String dir = item.getDireccionDestino() == null ? "" : item.getDireccionDestino();
            setText(id + (dir.isEmpty() ? "" : " - " + dir));
        }
    }
});

// la celda que se muestra cuando el ComboBox está cerrado
cbEnvio.setButtonCell(new javafx.scene.control.ListCell<packetworld.pojo.Envio>() {
    @Override
    protected void updateItem(packetworld.pojo.Envio item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            String id = item.getId() == null ? "?" : item.getId().toString();
            String dir = item.getDireccionDestino() == null ? "" : item.getDireccionDestino();
            setText(id + (dir.isEmpty() ? "" : " - " + dir));
        }
    }
});

        loadEnvios();
    }
    private boolean operationSuccess = false;

/** Devuelto al caller para saber si hay que mostrar la notificación de éxito. */
public boolean isOperationSuccess() {
    return operationSuccess;
}

    private void loadEnvios() {
        // Reusar EnvioImp.getAll() — devuelve HashMap con clave "data" lista de Envios
        try {
            java.util.HashMap<String,Object> resp = EnvioImp.getAll();
            if (resp != null && resp.containsKey("error") && Boolean.FALSE.equals(resp.get("error"))) {
                @SuppressWarnings("unchecked")
                List<Envio> list = (List<Envio>) resp.get("data");
                enviosList.setAll(list);
            } else {
                System.err.println("No se pudo cargar envios: " + resp);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void setEditMode(boolean editMode, Paquete paquete) {
        this.editMode = editMode;
        this.editingPaquete = paquete;
        if (editMode && paquete != null) {
            // seleccionar envío en el combo por id si está presente
            if (paquete.getEnvioId() != null) {
                for (Envio e : enviosList) {
                    if (e != null && e.getId() != null && e.getId().equals(paquete.getEnvioId())) {
                        cbEnvio.getSelectionModel().select(e);
                        break;
                    }
                }
            }
            tfDescripcion.setText(paquete.getDescripcion());
            tfPeso.setText(paquete.getPeso() == null ? "" : paquete.getPeso().toString());
            tfAlto.setText(paquete.getAlto() == null ? "" : paquete.getAlto().toString());
            tfAncho.setText(paquete.getAncho() == null ? "" : paquete.getAncho().toString());
            tfProfundidad.setText(paquete.getProfundidad() == null ? "" : paquete.getProfundidad().toString());
            tfCantidad.setText(paquete.getCantidad() == null ? "" : paquete.getCantidad().toString());
            tfValor.setText(paquete.getValor() == null ? "" : paquete.getValor().toString());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tfDescripcion.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSave() {
        // Validaciones básicas
        Envio selectedEnvio = cbEnvio.getSelectionModel().getSelectedItem();
        if (selectedEnvio == null) {
            Utility.createAlert("Validación", "Selecciona un envío del listado.", NotificationType.INFORMATION);
            return;
        }

        Paquete p = editMode && editingPaquete != null ? editingPaquete : new Paquete();
        p.setEnvioId(selectedEnvio.getId());
        p.setDescripcion(tfDescripcion.getText());
        p.setPeso(parseDoubleOrNull(tfPeso.getText()));
        p.setAlto(parseDoubleOrNull(tfAlto.getText()));
        p.setAncho(parseDoubleOrNull(tfAncho.getText()));
        p.setProfundidad(parseDoubleOrNull(tfProfundidad.getText()));
        p.setCantidad(parseIntegerOrNull(tfCantidad.getText()));
        p.setValor(parseDoubleOrNull(tfValor.getText()));

        // Llamada al backend (PaqueteImp.registrar o editar)
        MessageResponse mr;
        if (editMode) {
            mr = PaqueteImp.editPackage(p);
        } else {
            mr = PaqueteImp.addPackage(p);
        }

        if (mr != null && !mr.isError()) {
            Utility.createNotification(mr.getMessage() == null ? "Guardado" : mr.getMessage(), NotificationType.SUCCESS);
            // cerrar modal y forzar recarga en la vista padre (si corresponde)
            Stage stage = (Stage) tfDescripcion.getScene().getWindow();
            stage.close();
        } else {
            String msg = mr == null ? "Respuesta nula del servidor" : mr.getMessage();
            Utility.createAlert("Error al guardar paquete", msg, NotificationType.FAILURE);
        }
    }

    private Double parseDoubleOrNull(String s) {
        try { return s == null || s.trim().isEmpty() ? null : Double.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }

    private Integer parseIntegerOrNull(String s) {
        try { return s == null || s.trim().isEmpty() ? null : Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
    // añade estos métodos a FXMLPackageFormController (dentro de la clase)


/**
 * Compatibilidad: método que espera el caller (sin paquete).
 * Llama a tu setEditMode existente pasando null para el paquete.
 */
public void setEditMode(boolean editMode) {
    // si ya tienes setEditMode(boolean, Paquete), llámalo:
    try {
        // si existe el overload de dos parámetros:
        this.setEditMode(editMode, null);
    } catch (NoSuchMethodError e) {
        // si no existe, aplica la lógica mínima:
        this.editMode = editMode;
        this.editingPaquete = null;
        // opcional: actualizar campos de UI
    }
}

/**
 * Compatibilidad: el caller quiere pasar el paquete por separado.
 * Marcaremos el formulario en modo edición y seleccionamos el paquete.
 */public void setPaquete(packetworld.pojo.Paquete paquete) {
    // Si ya existe setEditMode(boolean, Paquete), reutilízalo
    try {
        this.setEditMode(true, paquete);
        return; // ya hizo todo
    } catch (NoSuchMethodError e) {
        // si no existe, continúa y aplica manualmente
    }

    // fallback manual
    this.editMode = true;
    this.editingPaquete = paquete;

    // si envios ya cargó (o recargar envios luego), seleccionar en combo si corresponde:
    if (paquete != null && enviosList != null && !enviosList.isEmpty()) {
        for (Envio envio : enviosList) {                       // <-- variable renombrada a 'envio'
            if (envio != null && envio.getId() != null
                    && envio.getId().equals(paquete.getIdEnvio())) {
                cbEnvio.getSelectionModel().select(envio);
                break;
            }
        }
    }

    // y setear campos del formulario (descripcion, peso, etc.)
    if (paquete != null) {
        tfDescripcion.setText(paquete.getDescripcion());
        tfPeso.setText(paquete.getPeso() == null ? "" : paquete.getPeso().toString());
        tfAlto.setText(paquete.getAlto() == null ? "" : paquete.getAlto().toString());
        tfAncho.setText(paquete.getAncho() == null ? "" : paquete.getAncho().toString());
        tfProfundidad.setText(paquete.getProfundidad() == null ? "" : paquete.getProfundidad().toString());
        tfCantidad.setText(paquete.getCantidad() == null ? "" : paquete.getCantidad().toString());
        tfValor.setText(paquete.getValor() == null ? "" : paquete.getValor().toString());
    }
}
}