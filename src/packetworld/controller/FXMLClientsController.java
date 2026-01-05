package packetworld.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import packetworld.domain.ClientImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Client;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

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

    private ObservableList<Client> clientsList;
    private FilteredList<Client> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadData();
        configureTableSelection();
        configureSearch();
        tvClients.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTableColumns() {
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        colColony.setCellValueFactory(new PropertyValueFactory<>("colony"));
        colZip.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadData() {
        new Thread(() -> {
            HashMap<String, Object> response = ClientImp.getAll();
            javafx.application.Platform.runLater(() -> {
                if (!(boolean) response.get("error")) {
                    List<Client> list = (List<Client>) response.get("clients");
                    clientsList = FXCollections.observableArrayList(list);
                    filteredData = new FilteredList<>(clientsList, p -> true);
                    SortedList<Client> sorted = new SortedList<>(filteredData);
                    sorted.comparatorProperty().bind(tvClients.comparatorProperty());
                    tvClients.setItems(sorted);

                    String currentSearch = searchField.getText();
                    if (currentSearch != null && !currentSearch.isEmpty()) {
                        searchField.setText("");
                        searchField.setText(currentSearch);
                    }
                } else {
                    Utility.createAlert("Error", (String) response.get("message"), NotificationType.FAILURE);
                }
            });
        }).start();
    }

    private void configureSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (filteredData == null) return;
            String q = newVal == null ? "" : newVal.trim().toLowerCase();
            filteredData.setPredicate(c ->
                (c.getFirstName() != null && c.getFirstName().toLowerCase().contains(q)) ||
                (c.getLastName() != null && c.getLastName().toLowerCase().contains(q)) ||
                (c.getStreet() != null && c.getStreet().toLowerCase().contains(q)) ||
                (c.getNumber() != null && c.getNumber().toLowerCase().contains(q)) ||
                (c.getColony() != null && c.getColony().toLowerCase().contains(q)) ||
                (c.getZipCode() != null && c.getZipCode().toLowerCase().contains(q)) ||
                (c.getPhone() != null && c.getPhone().toLowerCase().contains(q)) ||
                (c.getEmail() != null && c.getEmail().toLowerCase().contains(q))
            );
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
        if (!resp.isError()) {
            Utility.createNotification(resp.getMessage() == null ? "Cliente eliminado" : resp.getMessage(), NotificationType.DELETE);
            loadData();
        } else {
            Utility.createAlert("Error", resp.getMessage(), NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleClearSearch(MouseEvent event) {
        searchField.setText("");
        searchField.requestFocus();
    }
}