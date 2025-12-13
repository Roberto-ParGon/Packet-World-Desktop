package packetworld.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import packetworld.pojo.Collaborator;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

public class FXMLCollaboratorFormController implements Initializable {

    @FXML
    private ImageView ivCollaboratorPhoto;
    @FXML
    private Button btnSelectPhoto;
    @FXML
    private TextField tfPersonalNumber;
    @FXML
    private TextField tfCurp;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfLastname;
    @FXML
    private TextField tfSurname;
    @FXML
    private TextField tfEmail;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfConfirmPassword;
    @FXML
    private ComboBox<String> cbRole;
    @FXML
    private ComboBox<String> cbStore;
    @FXML
    private TextField tfVin;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;
    @FXML
    private HBox headerContainer;
    @FXML
    private Label lblTitle;

    private File selectedImageFile;
    private boolean isEditMode = false;
    private boolean operationSuccess = false;
    private final ValidationSupport validationSupport = new ValidationSupport();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeRoles();
        configureRoleListener();
        setupVisualValidation();

        cbStore.getItems().addAll("Sucursal Central", "Sucursal Norte", "Sucursal Sur");
    }

    private void initializeRoles() {
        cbRole.getItems().addAll(
                "Administrador",
                "Ejecutivo de Tienda",
                "Conductor"
        );
    }

    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    private void setupVisualValidation() {
        validationSupport.registerValidator(tfPersonalNumber, Validator.createEmptyValidator("Número de personal requerido"));
        validationSupport.registerValidator(tfName, Validator.createEmptyValidator("Nombre requerido"));
        validationSupport.registerValidator(tfLastname, Validator.createEmptyValidator("Apellido paterno requerido"));
        validationSupport.registerValidator(tfSurname, Validator.createEmptyValidator("Apellido materno requerido"));
        validationSupport.registerValidator(tfCurp, Validator.createRegexValidator("El CURP debe tener 18 caracteres", "^.{18}$", null));
        validationSupport.registerValidator(tfEmail, Validator.createRegexValidator("Formato de correo inválido", "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", null));
        validationSupport.registerValidator(pfPassword, Validator.createEmptyValidator("Contraseña requerida"));
        validationSupport.registerValidator(pfConfirmPassword, Validator.createEmptyValidator("Confirmación requerida"));
        validationSupport.registerValidator(cbRole, Validator.createEmptyValidator("Rol requerido"));
        validationSupport.registerValidator(cbStore, Validator.createEmptyValidator("Sucursal requerida"));

    }

    private void configureRoleListener() {
        cbRole.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Conductor")) {
                    tfVin.setDisable(false);
                } else {
                    tfVin.setDisable(true);
                    tfVin.clear();
                }
            }
        });
    }

    public void setCollaborator(Collaborator collaborator) {
        this.isEditMode = true;

        lblTitle.setText("Editar Colaborador");
        btnSave.setText("Actualizar Datos");

        headerContainer.setStyle("-fx-background-color: #42A5F5;");

        tfPersonalNumber.setText(collaborator.getPersonalNumber());
        tfCurp.setText(collaborator.getCurp());
        tfName.setText(collaborator.getName());
        tfLastname.setText(collaborator.getLastname());
        tfSurname.setText(collaborator.getSurname());
        tfEmail.setText(collaborator.getEmail());
        cbRole.setValue(collaborator.getRole());
        cbStore.setValue(collaborator.getIdStore());

        if (collaborator.getLicense() != null) {
            tfVin.setText(collaborator.getLicense());
        }

        tfPersonalNumber.setDisable(true);
        cbRole.setDisable(true);
        tfVin.setEditable(cbRole.getValue().equals("Conductor"));

    }

    @FXML
    private void handlePhotoSelection(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Fotografía de Perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png")
        );

        Stage stage = (Stage) btnSelectPhoto.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedImageFile = file;
            try {

            } catch (Exception e) {
                Utility.createAlert("Error", "No se pudo cargar la imagen seleccionada.", NotificationType.FAILURE);
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInputs()) {

            if (isEditMode) {

            } else {

            }
            this.operationSuccess = true;
            closeWindow();
        }
    }

    private boolean validateInputs() {
        if (tfPersonalNumber.getText().trim().isEmpty() || tfName.getText().trim().isEmpty()
                || tfLastname.getText().trim().isEmpty() || tfSurname.getText().trim().isEmpty()
                || tfEmail.getText().trim().isEmpty() || pfPassword.getText().isEmpty()
                || cbRole.getValue() == null || cbStore.getValue() == null) {

            Utility.createAlert("Campos Vacíos", "Por favor, llene todos los campos obligatorios.", NotificationType.FAILURE);
            return false;
        }

        if (tfCurp.getText().trim().length() != 18) {
            Utility.createAlert("CURP Inválido", "El CURP debe contener exactamente 18 caracteres.", NotificationType.FAILURE);
            return false;
        }

        if (!isValidEmail(tfEmail.getText().trim())) {
            Utility.createAlert("Correo Inválido", "Ingrese un correo electrónico válido (ejemplo@dominio.com).", NotificationType.FAILURE);
            return false;
        }

        if (!pfPassword.getText().equals(pfConfirmPassword.getText())) {
            Utility.createAlert("Error de Contraseña", "Las contraseñas no coinciden.", NotificationType.FAILURE);
            return false;
        }

        String selectedRole = cbRole.getValue();
        if ("Conductor".equals(selectedRole)) {
            if (tfVin.getText().trim().isEmpty()) {
                Utility.createAlert("Licencia requerida Requerida", "Para el rol de Conductor, el campo de Licencia es obligatorio.", NotificationType.FAILURE);
                return false;
            }
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        Node root = btnCancel.getScene().getRoot();
        Utility.animateExit(root, stage);
    }
}
