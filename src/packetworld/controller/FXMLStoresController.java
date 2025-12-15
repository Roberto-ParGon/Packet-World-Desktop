/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package packetworld.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
import packetworld.domain.StoreImp;
import packetworld.pojo.Store;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

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
    private TableColumn<Store, String> colStatus;

    private ObservableList<Store> storesList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        tvStores.setItems(storesList);
        new Thread(() -> {
            HashMap<String, Object> response = StoreImp.getAllStoresMap();

            javafx.application.Platform.runLater(() -> {
                if (!(boolean) response.get("error")) {
                    List<Store> list = (List<Store>) response.get("stores");
                    storesList.addAll(list);
                } else {
                    Utility.createAlert("Error", (String) response.get("message"), NotificationType.FAILURE);
                }
            });
        }).start();
    }

    private void configureTableSelection() {
        btnEdit.setDisable(true);
        tvStores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnEdit.setDisable(newSelection == null);
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
        loadData();
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
            loadData();
        } else {
            Utility.createAlert("Selección requerida", "Selecciona una sucursal para editar.", NotificationType.INFORMATION);
        }
    }
}
