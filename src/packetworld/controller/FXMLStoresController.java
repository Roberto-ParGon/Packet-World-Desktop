/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package packetworld.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import packetworld.pojo.Store;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FXMLStoresController implements Initializable {

    @FXML
    private Button btnEdit;
    @FXML
    private TableView<Store> tvStores;
    @FXML
    private TableColumn<Store, String> colName;
    @FXML
    private TableColumn<Store, String> colAddress;
    @FXML
    private TableColumn<Store, String> colCity;
    @FXML
    private TableColumn<Store, String> colZipCode;
    @FXML
    private TableColumn<Store, String> colPhone;
    @FXML
    private TableColumn<Store, String> colManager;
    @FXML
    private TableColumn<Store, String> colStatus;

    private ObservableList<Store> storesList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        configureTableColumns();
        loadData();
        configureTableSelection();
        tvStores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("fullAddress"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colZipCode.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colManager.setCellValueFactory(new PropertyValueFactory<>("manager"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<Store, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Activa".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #9E9E9E; -fx-font-style: italic;");
                    }
                }
            }
        });
    }

    private void loadData() {
        storesList = FXCollections.observableArrayList();

        storesList.add(new Store("SUC-001", "Sucursal Centro", "Activa", "Av. Enríquez", "123", "Centro", "91000", "Xalapa", "Veracruz", "2281234567", "Roberto Gómez"));
        storesList.add(new Store("SUC-002", "Sucursal Plaza Américas", "Activa", "Carr. Xalapa-Veracruz", "km 2", "Pastoresa", "91190", "Xalapa", "Veracruz", "2289876543", "María Antonieta"));
        storesList.add(new Store("SUC-003", "Sucursal Puerto", "Inactiva", "Blvd. Ávila Camacho", "500", "Costa Verde", "94294", "Veracruz", "Veracruz", "2291112222", "Ramón Valdés"));
        storesList.add(new Store("SUC-004", "Bodega Industrial", "Activa", "Calle de la Industria", "55", "Bruno Pagliai", "91697", "Veracruz", "Veracruz", "2293334444", "Carlos Villagrán"));

        tvStores.setItems(storesList);
    }

    private void configureTableSelection() {
        btnEdit.setDisable(true);

        tvStores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = (newSelection != null);
            btnEdit.setDisable(!isSelected);

        });
    }

    @FXML
    private void handleAddStore(ActionEvent event) {
        Utility.<FXMLStoreFormController>openAnimatedModal(
                "/packetworld/view/FXMLStoreForm.fxml",
                null,
                controller -> controller.isOperationSuccess(),
                controller -> "Sucursal registrada exitosamente"
        );
    }

    @FXML
    private void handleEditStore(ActionEvent event) {
        Store selected = tvStores.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Utility.<FXMLStoreFormController>openAnimatedModal(
                    "/packetworld/view/FXMLStoreForm.fxml",
                    controller -> controller.setStore(selected),
                    controller -> controller.isOperationSuccess(),
                    controller -> "Información de sucursal actualizada"
            );
            tvStores.refresh();
        } else {
            Utility.createAlert("Selección requerida", "Selecciona una sucursal para editar.", NotificationType.INFORMATION);
        }
    }

}
