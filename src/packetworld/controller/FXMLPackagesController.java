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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Pos;

import packetworld.domain.PaqueteImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Paquete;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

/**
 * Controller para la vista Paquetes (diseñado con la misma estructura/estilos que Colaboradores).
 */
public class FXMLPackagesController implements Initializable {

    // ----- FXML nodes (deben coincidir con fx:id del FXML) -----
    @FXML private TextField searchField;      // mismo id que en Collaborators para mantener estilo
    @FXML private Label lblFilter;
    @FXML private Label lblSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    @FXML private TableView<Paquete> tvPaquetes;
    @FXML private TableColumn<Paquete, String> colId;
    @FXML private TableColumn<Paquete, String> colEnvioId;
    @FXML private TableColumn<Paquete, String> colDescripcion;
    @FXML private TableColumn<Paquete, String> colPeso;
    @FXML private TableColumn<Paquete, String> colDimensiones;
    @FXML private TableColumn<Paquete, String> colCantidad;
    @FXML private TableColumn<Paquete, String> colValor;
    @FXML private TableColumn<Paquete, String> colFecha;

    // ----- Datos y filtros -----
    private ObservableList<Paquete> paquetesList;
    private FilteredList<Paquete> filteredData;
    private String filterType = "General";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadData();                      // carga inicial (todos)
        configureTableSelection();       // habilita/deshabilita botones según selección
        configureSearchFilter();         // filtro reactivo por texto
        tvPaquetes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Ajuste dinámico de anchos por porcentaje para que descripción no bloquee todo
        tvPaquetes.widthProperty().addListener((obs, oldW, newW) -> adjustColumnWidths(newW.doubleValue()));
    }

    // Configura columnas (robusto: usa getters del POJO y evita PropertyValueFactory por tipos mixtos)
    private void configureTableColumns() {
        // ID
        colId.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(p == null || p.getId() == null ? "" : String.valueOf(p.getId()));
        });

        // Envío ID
        colEnvioId.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(p == null || p.getIdEnvio() == null ? "" : String.valueOf(p.getIdEnvio()));
        });

        // Descripción
        colDescripcion.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(p == null || p.getDescripcion() == null ? "" : p.getDescripcion());
        });

        // Peso (double) -> formatted
        colPeso.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(p == null || p.getPeso() == null ? "" : String.valueOf(p.getPeso()));
        });
        colPeso.setCellFactory(col -> new TableCell<Paquete, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) { setText(null); }
                else {
                    try { setText(String.format("%.2f", Double.parseDouble(item))); }
                    catch (Exception e) { setText(item); }
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // Dimensiones: alto×ancho×profundidad
        colDimensiones.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            if (p == null) return new javafx.beans.property.SimpleStringProperty("");
            Double alto = p.getAlto() == null ? 0.0 : p.getAlto();
            Double ancho = p.getAncho() == null ? 0.0 : p.getAncho();
            Double prof = p.getProfundidad() == null ? 0.0 : p.getProfundidad();
            String dims = String.format("%.1f×%.1f×%.1f", alto, ancho, prof);
            return new javafx.beans.property.SimpleStringProperty(dims);
        });

        // Cantidad
        colCantidad.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(p == null || p.getCantidad() == null ? "" : String.valueOf(p.getCantidad()));
        });
        colCantidad.setStyle("-fx-alignment: CENTER;");

        // Valor (double)
        colValor.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(p == null || p.getValor() == null ? "" : String.valueOf(p.getValor()));
        });
        colValor.setCellFactory(col -> new TableCell<Paquete, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) { setText(null); }
                else {
                    try { setText(String.format("%.2f", Double.parseDouble(item))); }
                    catch (Exception e) { setText(item); }
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // Fecha - maneja String o Date/Timestamp
        colFecha.setCellValueFactory(cell -> {
            Paquete p = cell.getValue();
            if (p == null) return new javafx.beans.property.SimpleStringProperty("");
            Object fecha = p.getFechaCreacion(); // ajustar si getter retorna String o Date
            if (fecha == null) return new javafx.beans.property.SimpleStringProperty("");
            if (fecha instanceof String) return new javafx.beans.property.SimpleStringProperty((String) fecha);
            if (fecha instanceof java.util.Date) {
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return new javafx.beans.property.SimpleStringProperty(df.format((java.util.Date) fecha));
            }
            return new javafx.beans.property.SimpleStringProperty(fecha.toString());
        });

        // inicial setting mínimo para evitar colapsos al inicio
        colId.setMinWidth(40);
        colEnvioId.setMinWidth(60);
        colDescripcion.setMinWidth(150);
        colPeso.setMinWidth(70);
        colDimensiones.setMinWidth(120);
        colCantidad.setMinWidth(50);
        colValor.setMinWidth(70);
        colFecha.setMinWidth(100);
    }

    /**
     * Ajusta las anchuras de las columnas usando porcentajes del ancho total de la tabla.
     * Así la columna Descripción ocupa un espacio considerable pero no "traga" las demás.
     *
     * Porcentajes (ajústalos si quieres):
     * ID         = 5%
     * Envío ID   = 7%
     * Descripción= 35%
     * Peso       = 8%
     * Dimensiones= 18%
     * Cantidad   = 5%
     * Valor      = 7%
     * Fecha      = 15%
     */
    private void adjustColumnWidths(double tableWidth) {
        // restar márgenes/padding aproximado para el cálculo
        double w = Math.max(100, tableWidth - 40);

        colId.setPrefWidth(w * 0.05);
        colEnvioId.setPrefWidth(w * 0.07);
        colDescripcion.setPrefWidth(w * 0.35);
        colPeso.setPrefWidth(w * 0.08);
        colDimensiones.setPrefWidth(w * 0.18);
        colCantidad.setPrefWidth(w * 0.05);
        colValor.setPrefWidth(w * 0.07);
        colFecha.setPrefWidth(w * 0.15);
    }

    // Carga datos (todos o por idEnvio)
    private void loadData() {
        loadData(null);
    }

    private void loadData(Integer idEnvio) {
        new Thread(() -> {
            HashMap<String, Object> resp;
            if (idEnvio == null) {
                resp = PaqueteImp.getAll();
            } else {
                resp = PaqueteImp.getByEnvio(idEnvio);
            }
            javafx.application.Platform.runLater(() -> {
                if (resp != null && !(boolean) resp.get("error")) {
                    @SuppressWarnings("unchecked")
                    List<Paquete> list = (List<Paquete>) resp.get("data");
                    paquetesList = FXCollections.observableArrayList(list);
                    filteredData = new FilteredList<>(paquetesList, p -> true);
                    SortedList<Paquete> sorted = new SortedList<>(filteredData);
                    sorted.comparatorProperty().bind(tvPaquetes.comparatorProperty());
                    tvPaquetes.setItems(sorted);

                    // Debug: imprimir primer elemento para confirmar mapeo
                    if (paquetesList != null && !paquetesList.isEmpty()) {
                        Paquete first = paquetesList.get(0);
                        System.out.println("DEBUG first paquete raw: id=" + first.getId()
                                + " idEnvio=" + first.getIdEnvio()
                                + " desc=" + first.getDescripcion()
                                + " peso=" + first.getPeso()
                                + " alto=" + first.getAlto()
                                + " ancho=" + first.getAncho()
                                + " prof=" + first.getProfundidad()
                                + " valor=" + first.getValor()
                                + " fecha=" + first.getFechaCreacion());
                    }

                    // reaplicar búsqueda si había texto
                    String currentSearch = searchField.getText();
                    if (currentSearch != null && !currentSearch.isEmpty()) {
                        searchField.setText("");
                        searchField.setText(currentSearch);
                    }

                    // forzar ajuste inmediato (por si la tabla ya tiene un ancho)
                    adjustColumnWidths(tvPaquetes.getWidth());
                } else {
                    String msg = resp == null ? "Respuesta nula del servidor" : (String) resp.get("message");
                    Utility.createAlert("Error", msg, NotificationType.FAILURE);
                }
            });
        }).start();
    }

    // Filtro de búsqueda reactivo (local) similar a Colaboradores
    private void configureSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredData == null) return;
            filteredData.setPredicate(paquete -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String txt = newValue.toLowerCase().trim();

                switch (filterType) {
                    case "Descripción":
                        return paquete.getDescripcion() != null && paquete.getDescripcion().toLowerCase().contains(txt);
                    case "ID Envío":
                        return paquete.getIdEnvio() != null && String.valueOf(paquete.getIdEnvio()).contains(txt);
                    case "ID":
                        return paquete.getId() != null && String.valueOf(paquete.getId()).contains(txt);
                    case "General":
                    default:
                        if (paquete.getDescripcion() != null && paquete.getDescripcion().toLowerCase().contains(txt)) return true;
                        if (paquete.getIdEnvio() != null && String.valueOf(paquete.getIdEnvio()).contains(txt)) return true;
                        
                        return false;
                }
            });
        });
    }

    // Menú de selección de tipo de filtro (igual que en collaborators)
    @FXML
    private void handleFilterIconClick(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        ToggleGroup group = new ToggleGroup();

        RadioMenuItem itemGeneral = createFilterOption("General (Todos los campos)", group, true);
        RadioMenuItem itemDesc = createFilterOption("Descripción", group, false);
        RadioMenuItem itemEnvio = createFilterOption("ID Envío", group, false);
        RadioMenuItem itemId = createFilterOption("ID", group, false);

        contextMenu.getItems().addAll(itemGeneral, itemDesc, itemEnvio, itemId);
        contextMenu.show(lblFilter, event.getScreenX(), event.getScreenY());
    }

    private RadioMenuItem createFilterOption(String text, ToggleGroup group, boolean isSelected) {
        RadioMenuItem item = new RadioMenuItem(text);
        item.setToggleGroup(group);
        item.setSelected(isSelected || text.startsWith(filterType));

        item.setOnAction(e -> {
            if (text.contains("General")) filterType = "General";
            else filterType = text;

            String currentSearch = searchField.getText();
            searchField.setText("");
            searchField.setText(currentSearch);
            searchField.setPromptText("Buscar por: " + filterType);
        });

        return item;
    }

    @FXML
    private void handleDeleteSearch(MouseEvent event) {
        searchField.setText("");
        searchField.requestFocus();
    }

    // Selección en la tabla -> habilita botones
    private void configureTableSelection() {
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        tvPaquetes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = (newSelection != null);
            btnEdit.setDisable(!isSelected);
            btnDelete.setDisable(!isSelected);
        });
    }

    
@FXML
private void handleSearch(ActionEvent event) {
    String txt = searchField.getText().trim();
    if (txt.isEmpty()) {
        loadData(null);
        return;
    }

    // Si el texto son solo dígitos, interpretarlo como ID de ENVÍO y pedir al servidor
    if (txt.matches("\\d+")) {
        try {
            int id = Integer.parseInt(txt);
            // Llamada al servidor para obtener paquetes por idEnvio
            loadData(id);
        } catch (NumberFormatException ex) {
            Utility.createAlert("Dato inválido", "ID inválido", NotificationType.INFORMATION);
        }
        return;
    }

    // Si no es numérico, dejamos que el filtro local lo procese.
    if (filteredData != null) {
        // Forzamos re-evaluación del predicate
        String current = searchField.getText();
        searchField.setText("");
        searchField.setText(current);
    } else {
        loadData(null);
    }
}
    @FXML
    private void handleAdd(ActionEvent event) {
        Utility.<FXMLPackageFormController>openAnimatedModal(
                "/packetworld/view/FXMLPackageForm.fxml",
                controller -> controller.setEditMode(false),
                controller -> controller.isOperationSuccess(),
                controller -> "Paquete agregado"
        );
        loadData(null);
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Paquete sel = tvPaquetes.getSelectionModel().getSelectedItem();
        if (sel == null) { Utility.createAlert("Aviso", "Selecciona un paquete", NotificationType.INFORMATION); return; }

        Utility.<FXMLPackageFormController>openAnimatedModal(
                "/packetworld/view/FXMLPackageForm.fxml",
                controller -> controller.setPaquete(sel),
                controller -> controller.isOperationSuccess(),
                controller -> "Paquete actualizado"
        );
        loadData(null);
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Paquete sel = tvPaquetes.getSelectionModel().getSelectedItem();
        if (sel == null) { Utility.createAlert("Aviso", "Selecciona un paquete", NotificationType.INFORMATION); return; }

        boolean confirmed = Utility.createAlert("Eliminar paquete", "¿Eliminar paquete seleccionado?", NotificationType.DELETE);
        if (!confirmed) return;

        MessageResponse resp = PaqueteImp.deletePackage(sel.getId());
        if (!resp.isError()) {
            Utility.createNotification("Paquete eliminado", NotificationType.SUCCESS);
            loadData(null);
        } else {
            Utility.createAlert("Error", resp.getMessage(), NotificationType.FAILURE);
        }
    }
}