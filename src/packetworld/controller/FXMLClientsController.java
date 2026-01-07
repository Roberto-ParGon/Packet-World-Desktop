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
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import packetworld.domain.ClientImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Client;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLClientsController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Label lblFilter;
    @FXML private Label lblSearch;

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

    private ObservableList<Client> clientsList = FXCollections.observableArrayList();
    private FilteredList<Client> filteredData;
    
    private String filterType = "General";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (lblFilter != null) lblFilter.setCursor(Cursor.HAND);
        if (lblSearch != null) lblSearch.setCursor(Cursor.HAND);

        configureTableColumns();

        filteredData = new FilteredList<>(clientsList, p -> true);
        SortedList<Client> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(tvClients.comparatorProperty());
        tvClients.setItems(sorted);

        loadData();
        configureTableSelection();
        configureSearchFilter();
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
                    Utility.createAlert("Error", "Respuesta nula del servidor", NotificationType.FAILURE);
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

                @SuppressWarnings("unchecked")
                List<Client> list = (List<Client>) finalResp.get("data");
                if (list == null) list = Collections.emptyList();

                clientsList.setAll(list);
                
                if (!searchField.getText().isEmpty()) {
                    String current = searchField.getText();
                    searchField.setText("");
                    searchField.setText(current);
                }
            });
        }).start();
    }

    private void configureSearchFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (filteredData == null) return;
            
            filteredData.setPredicate(client -> {
                if (newVal == null || newVal.isEmpty()) return true;
                
                String lowerVal = newVal.trim().toLowerCase();
                
                String firstName = (client.getFirstName() != null) ? client.getFirstName().toLowerCase() : "";
                String lastName = (client.getLastName() != null) ? client.getLastName().toLowerCase() : "";
                String phone = (client.getPhone() != null) ? client.getPhone().toLowerCase() : "";
                String email = (client.getEmail() != null) ? client.getEmail().toLowerCase() : "";
                String street = (client.getStreet() != null) ? client.getStreet().toLowerCase() : "";

                switch (filterType) {
                    case "Nombre":
                        return firstName.contains(lowerVal);
                    case "Apellidos":
                        return lastName.contains(lowerVal);
                    case "Teléfono":
                        return phone.contains(lowerVal);
                    case "Correo":
                        return email.contains(lowerVal);
                    case "General":
                    default:
                        return firstName.contains(lowerVal) || 
                               lastName.contains(lowerVal) || 
                               phone.contains(lowerVal) || 
                               email.contains(lowerVal) ||
                               street.contains(lowerVal);
                }
            });
        });
    }

    @FXML
    private void handleFilterIconClick(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        ToggleGroup group = new ToggleGroup();

        RadioMenuItem itemGeneral = createFilterOption("General (Todos los campos)", group, true);
        RadioMenuItem itemNombre = createFilterOption("Nombre", group, false);
        RadioMenuItem itemApellidos = createFilterOption("Apellidos", group, false);
        RadioMenuItem itemTelefono = createFilterOption("Teléfono", group, false);
        RadioMenuItem itemCorreo = createFilterOption("Correo", group, false);

        contextMenu.getItems().addAll(itemGeneral, itemNombre, itemApellidos, itemTelefono, itemCorreo);
        contextMenu.show(lblFilter, event.getScreenX(), event.getScreenY());
    }

    private RadioMenuItem createFilterOption(String text, ToggleGroup group, boolean isSelected) {
        String key = text.contains("General") ? "General" : text;
        
        RadioMenuItem item = new RadioMenuItem(text);
        item.setToggleGroup(group);
        item.setSelected(filterType.equals(key));

        item.setOnAction(e -> {
            filterType = key;
            
            String currentSearch = searchField.getText();
            searchField.setText("");
            searchField.setText(currentSearch);
            
            searchField.setPromptText("Buscar por: " + filterType);
        });

        return item;
    }

    @FXML
    private void handleClearSearch(MouseEvent event) {
        searchField.setText("");
        searchField.requestFocus();
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
        boolean confirm = Utility.createAlert("Eliminar cliente", "¿Desea eliminar a " + selected.getFirstName() + " " + selected.getLastName() + "?", NotificationType.DELETE);
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
}