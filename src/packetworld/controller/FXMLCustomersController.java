package packetworld.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import packetworld.pojo.Client;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLCustomersController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<Client> tvCustomers;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colAddress;
    @FXML private TableColumn<Client, String> colCity;
    @FXML private TableColumn<Client, String> colZip;
    @FXML private TableColumn<Client, String> colPhone;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colStatus;

    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private ObservableList<Client> customersList;
    private FilteredList<Client> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        loadData();
        configureSelection();
        configureSearch();
        tvCustomers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTableColumns() {
        colName.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                (cell.getValue().getFullName().trim())));
        colAddress.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                (cell.getValue().getStreet() == null ? "" : cell.getValue().getStreet())
                        + (cell.getValue().getNumber() == null ? "" : " " + cell.getValue().getNumber())
                        + (cell.getValue().getColony() == null ? "" : ", " + cell.getValue().getColony())
        ));
        colCity.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getCity()));
        colZip.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getZipCode()));
        colPhone.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getPhone()));
        colEmail.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getEmail()));
        colStatus.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getStatus()));
    }

    private void loadData() {
        customersList = FXCollections.observableArrayList();

        // Datos iniciales en memoria (ajusta/añade los que necesites)
        Client c1 = new Client();
        c1.setId(1);
        c1.setFirstName("Sucursal Centro"); // si quieres mostrar nombre cliente real, cámbialo por nombre real
        c1.setLastName(""); // puedes poner apellido u otro campo
        c1.setStreet("Av. Enríquez #123");
        c1.setNumber("");
        c1.setColony("Col. Centro");
        c1.setZipCode("91000");
        c1.setCity("Xalapa");
        c1.setState("Veracruz");
        c1.setPhone("2281234567");
        c1.setEmail("cliente1@mail.com");
        c1.setStatus("Activo");

        Client c2 = new Client();
        c2.setId(2);
        c2.setFirstName("Sucursal Plaza Américas");
        c2.setStreet("Carr. Xalapa-Veracruz #km 2");
        c2.setNumber("");
        c2.setColony("Col. ...");
        c2.setZipCode("91190");
        c2.setCity("Xalapa");
        c2.setPhone("2289876543");
        c2.setEmail("cliente2@mail.com");
        c2.setStatus("Activo");

        Client c3 = new Client();
        c3.setId(3);
        c3.setFirstName("Sucursal Puerto");
        c3.setStreet("Blvd. Ávila Camacho #500");
        c3.setZipCode("94294");
        c3.setCity("Veracruz");
        c3.setPhone("2291112222");
        c3.setEmail("cliente3@mail.com");
        c3.setStatus("Inactivo");

        Client c4 = new Client();
        c4.setId(4);
        c4.setFirstName("Bodega Industrial");
        c4.setStreet("Calle de la Industria #55");
        c4.setZipCode("91697");
        c4.setCity("Veracruz");
        c4.setPhone("2293334444");
        c4.setEmail("cliente4@mail.com");
        c4.setStatus("Activo");

        customersList.addAll(c1, c2, c3, c4);

        filteredData = new FilteredList<>(customersList, p -> true);
        SortedList<Client> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(tvCustomers.comparatorProperty());
        tvCustomers.setItems(sorted);
    }

    private void configureSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String q = newVal == null ? "" : newVal.trim().toLowerCase();
            filteredData.setPredicate(c ->
                    (c.getFullName() != null && c.getFullName().toLowerCase().contains(q)) ||
                    (c.getPhone() != null && c.getPhone().toLowerCase().contains(q)) ||
                    (c.getEmail() != null && c.getEmail().toLowerCase().contains(q))
            );
        });
    }

    private void configureSelection() {
        tvCustomers.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            boolean has = nv != null;
            btnEdit.setDisable(!has);
            btnDelete.setDisable(!has);
        });
    }

    @FXML
private void handleAddCustomer() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLClientForm.fxml"));
        Parent root = loader.load();
        packetworld.controller.FXMLClientFormController ctrl = (packetworld.controller.FXMLClientFormController) loader.getController();
        ctrl.setTargetList(customersList); // pasar la lista para que el formulario pueda añadir el cliente directamente
        Stage s = new Stage();
        s.initModality(Modality.APPLICATION_MODAL);
        s.setTitle("Nuevo Cliente");
        s.setScene(new Scene(root));
        s.showAndWait();
        tvCustomers.refresh();
    } catch (IOException e) {
        e.printStackTrace();
        Utility.createNotification("Error al abrir formulario", NotificationType.FAILURE);
    }
}
    @FXML
private void handleEditCustomer() {
    Client sel = tvCustomers.getSelectionModel().getSelectedItem();
    if (sel == null) {
        Utility.createNotification("Selecciona un cliente", NotificationType.INFORMATION);
        return;
    }
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLClientForm.fxml"));
        Parent root = loader.load();
        packetworld.controller.FXMLClientFormController ctrl = (packetworld.controller.FXMLClientFormController) loader.getController();
        ctrl.setTargetList(customersList); // pasar la lista para que el formulario pueda actualizarla
        ctrl.setClient(sel); // llenar formulario con los datos
        Stage s = new Stage();
        s.initModality(Modality.APPLICATION_MODAL);
        s.setTitle("Editar Cliente");
        s.setScene(new Scene(root));
        s.showAndWait();
        tvCustomers.refresh();
    } catch (IOException e) {
        e.printStackTrace();
        Utility.createNotification("Error al abrir formulario", NotificationType.FAILURE);
    }
}

    @FXML
    private void handleDeleteCustomer() {
        Client sel = tvCustomers.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createNotification("Selecciona un cliente", NotificationType.INFORMATION);
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este cliente?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                customersList.remove(sel);
                Utility.createNotification("Cliente eliminado", NotificationType.SUCCESS);
            }
        });
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
    }
}