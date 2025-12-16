package packetworld.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import packetworld.domain.UnitImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Unit;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLUnitFormController implements Initializable {

    @FXML private TextField tfBrand;
    @FXML private TextField tfModel;
    @FXML private TextField tfYear;
    @FXML private TextField tfVin;
    @FXML private TextField tfNii;
    @FXML private ComboBox<String> cbType;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    @FXML private HBox headerContainer;
    @FXML private Label lblTitle;

    private boolean isEditMode = false;
    private boolean operationSuccess = false;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Unit currentUnit;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTypes();
        
        setupVisualValidation();
        
        // Listener para autocalcular NII (Año + 4 VIN)
        tfYear.textProperty().addListener((obs, old, nev) -> calculateNii());
        tfVin.textProperty().addListener((obs, old, nev) -> calculateNii());
    }
    
    private void initializeTypes() {
        cbType.getItems().clear();
        cbType.getItems().addAll("Gasolina", "Diesel", "Eléctrica", "Híbrida");
    }

    private void setupVisualValidation() {
        validationSupport.registerValidator(tfBrand, Validator.createEmptyValidator("Marca requerida"));
        validationSupport.registerValidator(tfModel, Validator.createEmptyValidator("Modelo requerido"));
        validationSupport.registerValidator(tfYear, Validator.createRegexValidator("Año inválido", "^\\d{4}$", null));
        validationSupport.registerValidator(tfVin, Validator.createEmptyValidator("VIN requerido"));
        validationSupport.registerValidator(cbType, Validator.createEmptyValidator("Tipo requerido"));
    }

    private void calculateNii() {
        String year = tfYear.getText().trim();
        String vin = tfVin.getText().trim();
        
        if (year.length() >= 4 && vin.length() >= 4) {
            String generatedNii = year + vin.substring(0, 4);
            tfNii.setText(generatedNii.toUpperCase());
        } else {
            tfNii.setText("");
        }
    }

    public void setUnit(Unit unit) {
        this.isEditMode = true;
        this.currentUnit = unit;

        lblTitle.setText("Editar Unidad");
        btnSave.setText("Actualizar");
        headerContainer.setStyle("-fx-background-color: #42A5F5;");

        tfBrand.setText(unit.getBrand());
        tfModel.setText(unit.getModel());
        tfYear.setText(String.valueOf(unit.getYear()));
        tfVin.setText(unit.getVin());
        cbType.setValue(unit.getType());
        tfNii.setText(unit.getNii());

        // REQUERIMIENTO: Bloquear edición de VIN
        tfVin.setDisable(true);
    }

    public boolean isOperationSuccess() { return operationSuccess; }

@FXML
    private void handleSave(ActionEvent event) {
        // 1. Validar campos vacíos
        if (tfBrand.getText().isEmpty() || tfVin.getText().isEmpty()) {
            Utility.createAlert("Campos requeridos", "Por favor llena marca y VIN.", NotificationType.FAILURE);
            return;
        }

        // 2. Validar longitud del VIN (Alerta personalizada de formato)
        if (tfVin.getText().trim().length() != 17) {
            Utility.createAlert("Formato Incorrecto", "El VIN debe tener exactamente 17 caracteres.", NotificationType.FAILURE);
            return; 
        }

        Unit unit = new Unit();
        unit.setBrand(tfBrand.getText());
        unit.setModel(tfModel.getText());
        
        try {
            unit.setYear(Integer.parseInt(tfYear.getText()));
        } catch(NumberFormatException e) {
             Utility.createAlert("Dato Inválido", "El año debe ser un número válido.", NotificationType.FAILURE);
             return;
        }
        
        unit.setVin(tfVin.getText());
        unit.setType(cbType.getValue());
        unit.setNii(tfNii.getText());

        MessageResponse response;
        if (isEditMode) {
            unit.setIdUnit(currentUnit.getIdUnit());
            unit.setVin(currentUnit.getVin());
            response = UnitImp.edit(unit);
        } else {
            response = UnitImp.register(unit);
        }

        if (!response.isError()) {
            this.operationSuccess = true;
            closeWindow();
        } else {
            Utility.createAlert("Error", response.getMessage(), NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) { closeWindow(); }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        Node root = btnCancel.getScene().getRoot();
        Utility.animateExit(root, stage);
    }
}