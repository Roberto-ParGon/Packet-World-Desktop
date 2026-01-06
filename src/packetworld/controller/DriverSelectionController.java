package packetworld.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import packetworld.domain.CollaboratorImp;
import packetworld.pojo.Collaborator;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

/**
 * Modal que muestra la lista de conductores disponibles y permite seleccionar uno.
 * Adaptado al POJO packetworld.pojo.Collaborator (getIdCollaborator(), getName(), getLastname(), getSurname()).
 */
public class DriverSelectionController implements Initializable {

    @FXML private TableView<Collaborator> tvDrivers;
    @FXML private TableColumn<Collaborator, String> colDriverId;
    @FXML private TableColumn<Collaborator, String> colDriverName;
    @FXML private Button btnOk;
    @FXML private Button btnCancel;
    @FXML private Label lblStatus;

    private final ObservableList<Collaborator> drivers = FXCollections.observableArrayList();
    private Integer selectedDriverId = null;
    private boolean confirmed = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (tvDrivers != null) {
            tvDrivers.setItems(drivers);
        }

        // Columna ID
        if (colDriverId != null) {
            colDriverId.setCellValueFactory(cell -> {
                Collaborator c = cell.getValue();
                String s = (c == null || c.getIdCollaborator() == null) ? "" : String.valueOf(c.getIdCollaborator());
                return new SimpleStringProperty(s);
            });
        }

        // Columna Nombre completo (name + lastname + surname) - adaptada al POJO
        if (colDriverName != null) {
            colDriverName.setCellValueFactory(cell -> {
                Collaborator c = cell.getValue();
                StringBuilder sb = new StringBuilder();
                if (c != null) {
                    if (c.getName() != null && !c.getName().trim().isEmpty()) sb.append(c.getName().trim());
                    if (c.getLastname() != null && !c.getLastname().trim().isEmpty()) {
                        if (sb.length() > 0) sb.append(" ");
                        sb.append(c.getLastname().trim());
                    }
                    if (c.getSurname() != null && !c.getSurname().trim().isEmpty()) {
                        if (sb.length() > 0) sb.append(" ");
                        sb.append(c.getSurname().trim());
                    }
                }
                return new SimpleStringProperty(sb.toString());
            });
        }

        // Inicialmente deshabilitar Aceptar hasta seleccionar
        if (btnOk != null) btnOk.setDisable(true);

        // Listener de selección
        if (tvDrivers != null) {
            tvDrivers.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && newV.getIdCollaborator() != null) {
                    selectedDriverId = newV.getIdCollaborator();
                } else {
                    selectedDriverId = null;
                }
                if (btnOk != null) btnOk.setDisable(selectedDriverId == null);
            });
        }

        loadDrivers();
    }

    private void loadDrivers() {
        if (lblStatus != null) lblStatus.setText("Cargando conductores...");
        new Thread(() -> {
            List<Collaborator> list = null;
            try {
                list = CollaboratorImp.getAvailableDrivers();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            final List<Collaborator> finalList = (list == null ? java.util.Collections.emptyList() : list);
            Platform.runLater(() -> {
                drivers.setAll(finalList);
                if (lblStatus != null) lblStatus.setText(finalList.isEmpty() ? "No hay conductores disponibles" : "Seleccione un conductor");
                if (btnOk != null) btnOk.setDisable(true);
            });
        }).start();
    }

    @FXML
    private void onOk() {
        if (selectedDriverId == null) {
            Utility.createAlert("Validación", "Selecciona un conductor", NotificationType.INFORMATION);
            return;
        }
        confirmed = true;
        // cerrar el modal: el Stage lo cierra el controlador que abrió el modal
        if (btnOk != null && btnOk.getScene() != null) {
            btnOk.getScene().getWindow().hide();
        }
    }

    @FXML
    private void onCancel() {
        confirmed = false;
        if (btnCancel != null && btnCancel.getScene() != null) {
            btnCancel.getScene().getWindow().hide();
        }
    }

    public Integer getSelectedDriverId() {
        return selectedDriverId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}