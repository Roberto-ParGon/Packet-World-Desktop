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
import packetworld.pojo.Delivery;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLDeliveriesController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<Delivery> tvDeliveries;
    @FXML private TableColumn<Delivery, String> colTracking;
    @FXML private TableColumn<Delivery, String> colRecipient;
    @FXML private TableColumn<Delivery, String> colOrigin;
    @FXML private TableColumn<Delivery, String> colDestination;
    @FXML private TableColumn<Delivery, String> colWeight;
    @FXML private TableColumn<Delivery, String> colDate;
    @FXML private TableColumn<Delivery, String> colStatus;

    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private ObservableList<Delivery> deliveriesList;
    private FilteredList<Delivery> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureColumns();
        loadData();
        configureSelection();
        configureSearch();
        tvDeliveries.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureColumns() {
        colTracking.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getTrackingNumber()));
        colRecipient.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getRecipientName()));
        colOrigin.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getOrigin()));
        colDestination.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getDestination()));
        colWeight.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getWeight()));
        colDate.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getDate()));
        colStatus.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getStatus()));
    }

    private void loadData() {
        deliveriesList = FXCollections.observableArrayList();

        // Datos de ejemplo
        Delivery d1 = new Delivery();
        d1.setId(1);
        d1.setTrackingNumber("PW-0001");
        d1.setSenderName("Sucursal Centro");
        d1.setRecipientName("Juan Pérez");
        d1.setOrigin("Xalapa");
        d1.setDestination("Veracruz");
        d1.setWeight("2.5 kg");
        d1.setDate("2025-12-01");
        d1.setStatus("Pendiente");

        Delivery d2 = new Delivery();
        d2.setId(2);
        d2.setTrackingNumber("PW-0002");
        d2.setSenderName("Bodega");
        d2.setRecipientName("María López");
        d2.setOrigin("Veracruz");
        d2.setDestination("Coatzacoalcos");
        d2.setWeight("1.2 kg");
        d2.setDate("2025-12-02");
        d2.setStatus("Enviado");

        deliveriesList.addAll(d1, d2);

        filteredData = new FilteredList<>(deliveriesList, p -> true);
        SortedList<Delivery> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(tvDeliveries.comparatorProperty());
        tvDeliveries.setItems(sorted);
    }

    private void configureSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String q = newVal == null ? "" : newVal.trim().toLowerCase();
            filteredData.setPredicate(d ->
                    (d.getTrackingNumber() != null && d.getTrackingNumber().toLowerCase().contains(q)) ||
                    (d.getRecipientName() != null && d.getRecipientName().toLowerCase().contains(q)) ||
                    (d.getStatus() != null && d.getStatus().toLowerCase().contains(q))
            );
        });
    }

    private void configureSelection() {
        tvDeliveries.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            boolean has = nv != null;
            btnEdit.setDisable(!has);
            btnDelete.setDisable(!has);
        });
    }

    @FXML
    private void handleAddDelivery() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLDeliveryForm.fxml"));
            Parent root = loader.load();
            packetworld.controller.FXMLDeliveryFormController ctrl = (packetworld.controller.FXMLDeliveryFormController) loader.getController();
            ctrl.setTargetList(deliveriesList);
            Stage s = new Stage();
            s.initModality(Modality.APPLICATION_MODAL);
            s.setTitle("Nuevo Envío");
            s.setScene(new Scene(root));
            s.showAndWait();
            tvDeliveries.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            Utility.createNotification("Error al abrir formulario", NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleEditDelivery() {
        Delivery sel = tvDeliveries.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createNotification("Selecciona un envío", NotificationType.INFORMATION);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLDeliveryForm.fxml"));
            Parent root = loader.load();
            packetworld.controller.FXMLDeliveryFormController ctrl = (packetworld.controller.FXMLDeliveryFormController) loader.getController();
            ctrl.setTargetList(deliveriesList);
            ctrl.setDelivery(sel);
            Stage s = new Stage();
            s.initModality(Modality.APPLICATION_MODAL);
            s.setTitle("Editar Envío");
            s.setScene(new Scene(root));
            s.showAndWait();
            tvDeliveries.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            Utility.createNotification("Error al abrir formulario", NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleDeleteDelivery() {
        Delivery sel = tvDeliveries.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utility.createNotification("Selecciona un envío", NotificationType.INFORMATION);
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este envío?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                deliveriesList.remove(sel);
                Utility.createNotification("Envío eliminado", NotificationType.SUCCESS);
            }
        });
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
    }
}