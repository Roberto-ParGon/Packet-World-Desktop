package packetworld.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import packetworld.domain.EnvioImp;
import packetworld.domain.CollaboratorImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Collaborator;
import packetworld.pojo.Envio;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLAssignDriverToEnvioController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private Label lblGuia;
    @FXML private Label lblDestinatario;
    @FXML private ComboBox<Collaborator> cbDrivers;
    @FXML private Button btnCancel;
    @FXML private Button btnAssign;

    private Envio envio;
    private boolean operationSuccess = false;
    private String successMessage = "Conductor asignado";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // no-op
    }

    public boolean isOperationSuccess() { return operationSuccess; }
    public String getSuccessMessage() { return successMessage; }

    public void initForEnvio(Envio e) {
        this.envio = e;
        if (e == null) return;
        lblGuia.setText(e.getNumGuia() == null ? "" : e.getNumGuia());
        lblDestinatario.setText(e.getDestinatarioNombre() == null ? "" : e.getDestinatarioNombre());
        loadDrivers();
    }

    private void loadDrivers() {
    new Thread(() -> {
        HashMap<String, Object> resp = CollaboratorImp.getAll();
        final HashMap<String,Object> finalResp = resp;
        Platform.runLater(() -> {
            List<Collaborator> drivers = new ArrayList<>();
            if (finalResp != null && !(boolean) finalResp.get("error")) {
                @SuppressWarnings("unchecked")
                List<Collaborator> list = (List<Collaborator>) finalResp.get("collaborators");
                if (list != null) {
                    for (Collaborator c : list) {
                        if (c != null && "Conductor".equalsIgnoreCase(c.getRole())) {
                            drivers.add(c);
                        }
                    }
                }
            } else {
                String msg = finalResp == null ? "Error cargando colaboradores" : String.valueOf(finalResp.get("message"));
                Utility.createAlert("Error", msg, NotificationType.FAILURE);
            }

            // Configurar cómo se muestran los items en el ComboBox
            cbDrivers.setCellFactory(listView -> new ListCell<Collaborator>() {
                @Override
                protected void updateItem(Collaborator item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(collaboratorLabel(item));
                    }
                }
            });

            // Texto que aparece en el botón del ComboBox (valor seleccionado)
            cbDrivers.setButtonCell(new ListCell<Collaborator>() {
                @Override
                protected void updateItem(Collaborator item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : collaboratorLabel(item));
                }
            });

            cbDrivers.setItems(FXCollections.observableArrayList(drivers));
            if (!drivers.isEmpty()) cbDrivers.getSelectionModel().selectFirst();
        });
    }).start();
}

// Helper que devuelve etiqueta legible del colaborador
private String collaboratorLabel(Collaborator c) {
    if (c == null) return "";

    String namePart = "";
    if (c.getName() != null && !c.getName().trim().isEmpty()) namePart += c.getName().trim();
    if (c.getLastname() != null && !c.getLastname().trim().isEmpty()) {
        namePart = (namePart + " " + c.getLastname().trim()).trim();
    }
    if (c.getSurname() != null && !c.getSurname().trim().isEmpty()) {
        namePart = (namePart + " " + c.getSurname().trim()).trim();
    }

    if (namePart.isEmpty()) namePart = "Conductor";

    String personal = (c.getPersonalNumber() == null || c.getPersonalNumber().trim().isEmpty())
            ? ""
            : " (" + c.getPersonalNumber().trim() + ")";

    return namePart + personal;
}
    @FXML
    private void handleAssign(javafx.event.ActionEvent event) {
        Collaborator sel = cbDrivers.getValue();
        if (sel == null || envio == null) {
            Utility.createAlert("Validación", "Selecciona un conductor", NotificationType.INFORMATION);
            return;
        }
        // Llamar a EnvioImp.assignDriver
        MessageResponse resp = EnvioImp.assignDriver(envio.getId(), sel.getIdCollaborator());
        if (resp != null && !resp.isError()) {
            this.operationSuccess = true;
            this.successMessage = resp.getMessage() == null ? "Conductor asignado correctamente" : resp.getMessage();
            Utility.createNotification(this.successMessage, NotificationType.SUCCESS);
            closeWindow();
        } else {
            String msg = resp == null ? "Respuesta nula del servidor" : resp.getMessage();
            Utility.createAlert("Error", msg, NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        Utility.animateExit(stage.getScene().getRoot(), stage);
    }
}
