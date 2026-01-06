package packetworld.controller;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import packetworld.domain.ClientImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Client;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

/**
 * Controlador de Clientes corregido y robusto.
 * - evita NullPointer al poblar listas cuando la respuesta del servicio es null
 * - inicializa y reusa una ObservableList global (clientsList)
 * - prepara FilteredList / SortedList y los bindea a la tabla
 * - usa la clave "data" devuelta por ClientImp.getAll()
 */
public class FXMLClientsController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Label lblFilter;

    @FXML private TableColumn<Client, String> colFirstName;
    @FXML private TableColumn<Client, String> colLastName;
    @FXML private TableColumn<Client, String> colStreet;
    @FXML private TableColumn<Client, String> colNumber;
    @FXML private TableColumn<Client, String> colColony;
    @FXML private TableColumn<Client, String> colZip;
    @FXML private TableColumn<Client, String> colPhone;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableView<Client> tvClients;

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    // Lista observable global reutilizada
    private ObservableList<Client> clientsList = FXCollections.observableArrayList();
    private FilteredList<Client> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();

        // Preparar filtered + sorted y bindear a la tabla
        filteredData = new FilteredList<>(clientsList, p -> true);
        SortedList<Client> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(tvClients.comparatorProperty());
        tvClients.setItems(sorted);

        loadData();
        configureTableSelection();
        configureSearch();
        tvClients.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTableColumns() {
        // PropertyValueFactory usa los nombres de propiedad: "firstName" -> getFirstName()
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        colColony.setCellValueFactory(new PropertyValueFactory<>("colony"));
        colZip.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    /**
     * Carga los clientes desde el backend de forma segura.
     * Usa la clave "data" que devuelve ClientImp.getAll() y garantiza nunca pasar null a FXCollections.
     */
    private void loadData() {
        new Thread(() -> {
            HashMap<String, Object> response = null;
            try {
                response = ClientImp.getAll();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            final HashMap<String, Object> finalResp = response;
            Platform.runLater(() -> {
                if (finalResp == null) {
                    clientsList.clear();
                    Utility.createAlert("Error", "Respuesta nula del servidor al cargar clientes", NotificationType.FAILURE);
                    return;
                }

                Object err = finalResp.get("error");
                boolean isError = (err instanceof Boolean) ? (Boolean) err : true;
                if (isError) {
                    String msg = finalResp.get("message") == null ? "Error al cargar clientes" : String.valueOf(finalResp.get("message"));
                    clientsList.clear();
                    Utility.createAlert("Error", msg, NotificationType.FAILURE);
                    return;
                }

                // El servicio devuelve la lista bajo la clave "data"
                @SuppressWarnings("unchecked")
                List<Client> list = (List<Client>) finalResp.get("data");
                if (list == null) list = Collections.emptyList();

                // Actualizamos la ObservableList existente (no reemplazarla) para mantener bindings
                clientsList.setAll(list);
            });
        }).start();
    }

    private void configureSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (filteredData == null) return;
            String q = newVal == null ? "" : newVal.trim().toLowerCase();
            filteredData.setPredicate(c -> {
                if (c == null) return false;
                // Usar getters de compatibilidad (getFirstName/getLastName/getPhone/getEmail etc.)
                boolean match = false;
                String v;
                v = c.getFirstName(); if (v != null && v.toLowerCase().contains(q)) match = true;
                v = c.getLastName(); if (v != null && v.toLowerCase().contains(q)) match = true;
                v = c.getStreet(); if (v != null && v.toLowerCase().contains(q)) match = true;
                v = c.getNumber(); if (v != null && v.toLowerCase().contains(q)) match = true;
                v = c.getColony(); if (v != null && v.toLowerCase().contains(q)) match = true;
                v = c.getZipCode(); if (v != null && v.toLowerCase().contains(q)) match = true;
                v = c.getPhone(); if (v != null && v.toLowerCase().contains(q)) match = true;
                v = c.getEmail(); if (v != null && v.toLowerCase().contains(q)) match = true;
                return match;
            });
        });
    }

    private void configureTableSelection() {
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        tvClients.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean isSelected = newSel != null;
            btnEdit.setDisable(!isSelected);
            btnDelete.setDisable(!isSelected);
        });
    }

    @FXML
    private void handleAddClient(ActionEvent event) {
        Utility.<FXMLClientFormController>openAnimatedModal(
                "/packetworld/view/FXMLClientForm.fxml",
                null,
                controller -> controller.isOperationSuccess(),
                controller -> "Cliente registrado exitosamente"
        );
        // recargar datos después de cerrar modal
        loadData();
    }

    @FXML
    private void handleEditClient(ActionEvent event) {
        Client selected = tvClients.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("EDIT -> Selected client (debug): id=" + selected.getId()
                + " nombre=" + selected.getFirstName() + " apellido=" + selected.getLastName()
                + " calle=" + selected.getStreet() + " num_ext=" + selected.getNumber()
                + " colonia=" + selected.getColony() + " cp=" + selected.getZipCode()
                + " telefono=" + selected.getPhone() + " email=" + selected.getEmail());
            Utility.<FXMLClientFormController>openAnimatedModal(
                    "/packetworld/view/FXMLClientForm.fxml",
                    controller -> controller.setClient(selected),
                    controller -> controller.isOperationSuccess(),
                    controller -> "Cliente editado exitosamente"
            );
            loadData();
        } else {
            Utility.createAlert("Selección requerida", "Selecciona un cliente.", NotificationType.INFORMATION);
        }
    }

    @FXML
    private void handleDeleteClient(ActionEvent event) {
        Client selected = tvClients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Utility.createAlert("Selección requerida", "Selecciona un cliente.", NotificationType.INFORMATION);
            return;
        }
        boolean confirm = Utility.createAlert("Eliminar cliente", "¿Desea eliminar este cliente?", NotificationType.DELETE);
        if (!confirm) return;

        MessageResponse resp = ClientImp.delete(selected.getId());
        if (resp != null && !resp.isError()) {
            Utility.createNotification(resp.getMessage() == null ? "Cliente eliminado" : resp.getMessage(), NotificationType.DELETE);
            loadData();
        } else {
            String msg = resp == null ? "No hay respuesta del servidor" : resp.getMessage();
            Utility.createAlert("Error", msg, NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleClearSearch(MouseEvent event) {
        searchField.setText("");
        searchField.requestFocus();
    }
}