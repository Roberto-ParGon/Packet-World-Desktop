package packetworld.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import packetworld.domain.EnvioImp;
import packetworld.domain.PaqueteImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Envio;
import packetworld.pojo.Paquete;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLPackageFormController implements Initializable {

    @FXML
    private ComboBox<Envio> cbEnvio;
    @FXML
    private TextArea tfDescripcion;
    @FXML
    private TextField tfPeso;
    @FXML
    private TextField tfAlto;
    @FXML
    private TextField tfAncho;
    @FXML
    private TextField tfProfundidad;
    @FXML
    private Spinner<Integer> spCantidad;
    @FXML
    private TextField tfValor;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

    private boolean editMode = false;
    private Paquete editingPaquete = null;
    private boolean operationSuccess = false;
    private ObservableList<Envio> enviosList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEnvioComboBox();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        if (spCantidad != null) {
            spCantidad.setValueFactory(valueFactory);
        }

        loadEnvios();
    }

    private void setupEnvioComboBox() {
        if (cbEnvio == null) {
            return;
        }

        cbEnvio.setItems(enviosList);
        Callback<ListView<Envio>, ListCell<Envio>> factory = lv -> new ListCell<Envio>() {
            @Override
            protected void updateItem(Envio item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String guia = item.getNumGuia() != null ? item.getNumGuia() : "Sin Guía";
                    String dest = item.getDireccionDestino() != null ? item.getDireccionDestino() : "";
                    setText(guia + " - " + dest);
                }
            }
        };
        cbEnvio.setCellFactory(factory);
        cbEnvio.setButtonCell(factory.call(null));
    }

    private void loadEnvios() {
        new Thread(() -> {
            HashMap<String, Object> resp = EnvioImp.getAll();
            Platform.runLater(() -> {
                if (resp != null && !(boolean) resp.get("error")) {
                    List<Envio> list = (List<Envio>) resp.get("data");
                    if (list != null) {
                        enviosList.setAll(list);
                        if (editMode && editingPaquete != null && cbEnvio != null) {
                            for (Envio e : enviosList) {
                                if (e.getId().equals(editingPaquete.getEnvioId())) {
                                    cbEnvio.setValue(e);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    System.err.println("No se pudieron cargar los envíos.");
                }
            });
        }).start();
    }

    public void setEditMode(boolean editMode, Paquete paquete) {
        this.editMode = editMode;
        this.editingPaquete = paquete;

        if (cbEnvio != null) {
            cbEnvio.setDisable(editMode);
        }

        if (editMode && paquete != null) {
            if (tfDescripcion != null) {
                tfDescripcion.setText(paquete.getDescripcion());
            }
            if (tfPeso != null) {
                tfPeso.setText(String.valueOf(paquete.getPeso()));
            }
            if (tfAlto != null) {
                tfAlto.setText(String.valueOf(paquete.getAlto()));
            }
            if (tfAncho != null) {
                tfAncho.setText(String.valueOf(paquete.getAncho()));
            }
            if (tfProfundidad != null) {
                tfProfundidad.setText(String.valueOf(paquete.getProfundidad()));
            }
            if (tfValor != null) {
                tfValor.setText(String.valueOf(paquete.getValor()));
            }

            if (spCantidad != null && paquete.getCantidad() != null) {
                spCantidad.getValueFactory().setValue(paquete.getCantidad());
            }
        }
    }

    public void setPaquete(Paquete p) {
        setEditMode(true, p);
    }

    @FXML
    private void handleSave() {
        if (cbEnvio != null && cbEnvio.getValue() == null) {
            Utility.createAlert("Validación", "Seleccione un envío.", NotificationType.FAILURE);
            return;
        }
        if (tfDescripcion != null && tfDescripcion.getText().trim().isEmpty()) {
            Utility.createAlert("Validación", "Ingrese una descripción.", NotificationType.FAILURE);
            return;
        }

        try {
            Paquete p = (editMode && editingPaquete != null) ? editingPaquete : new Paquete();

            if (cbEnvio != null) {
                p.setEnvioId(cbEnvio.getValue().getId());
            }
            if (tfDescripcion != null) {
                p.setDescripcion(tfDescripcion.getText());
            }
            if (tfPeso != null) {
                p.setPeso(parseDouble(tfPeso.getText()));
            }
            if (tfAlto != null) {
                p.setAlto(parseDouble(tfAlto.getText()));
            }
            if (tfAncho != null) {
                p.setAncho(parseDouble(tfAncho.getText()));
            }
            if (tfProfundidad != null) {
                p.setProfundidad(parseDouble(tfProfundidad.getText()));
            }
            if (tfValor != null) {
                p.setValor(parseDouble(tfValor.getText()));
            }

            if (spCantidad != null) {
                p.setCantidad(spCantidad.getValue());
            } else {
                p.setCantidad(1);
            }

            MessageResponse mr;
            if (editMode) {
                mr = PaqueteImp.editPackage(p);
            } else {
                mr = PaqueteImp.addPackage(p);
            }

            if (mr != null && !mr.isError()) {
                operationSuccess = true;
                Utility.createNotification("Paquete guardado", NotificationType.SUCCESS);
                Utility.closeModal(btnSave);
            } else {
                Utility.createAlert("Error", mr != null ? mr.getMessage() : "Error de red", NotificationType.FAILURE);
            }

        } catch (NumberFormatException e) {
            Utility.createAlert("Error", "Verifique que los campos numéricos sean correctos.", NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleCancel() {
        Utility.closeModal(btnCancel);
    }

    private Double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(s.trim());
    }

    public boolean isOperationSuccess() {
        return operationSuccess;
    }
}
