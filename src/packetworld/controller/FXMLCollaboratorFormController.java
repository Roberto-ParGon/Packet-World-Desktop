package packetworld.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import packetworld.domain.CollaboratorImp;
import packetworld.domain.StoreImp;
import packetworld.dto.MessageResponse;
import packetworld.pojo.Collaborator;
import packetworld.pojo.Store;
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
    private ComboBox<Store> cbStore;
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

    private boolean isEditMode = false;
    private boolean operationSuccess = false;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Collaborator currentCollaborator;

    private byte[] currentPhotoBytes;
    private boolean photoChanged = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeRoles();
        configureRoleListener();
        setupVisualValidation();
        loadStores();
    }

    private void initializeRoles() {
        cbRole.getItems().addAll("Administrador", "Ejecutivo de tienda", "Conductor");
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
        cbRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                boolean isDriver = newVal.equals("Conductor");
                tfVin.setDisable(!isDriver);
                if (!isDriver) {
                    tfVin.clear();
                }
            }
        });
    }

    private void loadStores() {
        new Thread(() -> {
            List<Store> stores = StoreImp.getAll();

            Platform.runLater(() -> {
                if (stores != null) {
                    List<Store> activeStores = stores.stream()
                            .filter(Store::isActiva)
                            .collect(Collectors.toList());

                    cbStore.getItems().setAll(activeStores);

                    if (isEditMode && currentCollaborator != null) {
                        selectCurrentStore();
                    }
                }
            });
        }).start();
    }

    private void selectCurrentStore() {
        Integer targetId = currentCollaborator.getIdStore();
        if (targetId != null) {
            for (Store s : cbStore.getItems()) {
                if (s.getIdStore().equals(targetId)) {
                    cbStore.setValue(s);
                    break;
                }
            }
        }
    }

    public void setCollaborator(Collaborator collaborator) {
        this.isEditMode = true;
        this.currentCollaborator = collaborator;

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
        selectStoreById(collaborator.getIdStore());

        if (!cbStore.getItems().isEmpty()) {
            selectCurrentStore();
        }

        if (collaborator.getLicense() != null) {
            tfVin.setText(collaborator.getLicense());
        }

        tfPersonalNumber.setDisable(true);
        cbRole.setDisable(true);

        downloadCollaboratorPhoto(collaborator.getIdCollaborator());
    }

    private void downloadCollaboratorPhoto(int idCollaborator) {
        new Thread(() -> {
            try {
                Collaborator photoData = CollaboratorImp.getCollaboratorPhoto(idCollaborator);
                if (photoData != null && photoData.getPhoto64() != null && !photoData.getPhoto64().isEmpty()) {

                    String cleanBase64 = photoData.getPhoto64().replaceAll("\\s", "");
                    byte[] imgBytes = Base64.getDecoder().decode(cleanBase64);

                    javafx.application.Platform.runLater(() -> {
                        ByteArrayInputStream stream = new ByteArrayInputStream(imgBytes);
                        ivCollaboratorPhoto.setImage(new Image(stream));
                    });
                }
            } catch (Exception e) {
                System.err.println("Error descargando foto: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    private void handlePhotoSelection(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png"));

        File file = fileChooser.showOpenDialog(btnSelectPhoto.getScene().getWindow());

        if (file != null) {
            try {
                ivCollaboratorPhoto.setImage(new Image(file.toURI().toString()));

                this.currentPhotoBytes = Files.readAllBytes(file.toPath());
                this.photoChanged = true;

            } catch (IOException e) {
                Utility.createAlert("Error", "No se pudo procesar la imagen.", NotificationType.FAILURE);
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        if (!isEditMode && currentPhotoBytes == null) {
            Utility.createAlert("Foto Requerida", "Debe seleccionar una fotografía.", NotificationType.FAILURE);
            return;
        }

        Collaborator formCollaborator = new Collaborator();
        formCollaborator.setPersonalNumber(tfPersonalNumber.getText());
        formCollaborator.setName(tfName.getText());
        formCollaborator.setLastname(tfLastname.getText());
        formCollaborator.setSurname(tfSurname.getText());
        formCollaborator.setCurp(tfCurp.getText());
        formCollaborator.setEmail(tfEmail.getText());
        formCollaborator.setRole(cbRole.getValue());
        if (cbStore.getValue() != null) {
            formCollaborator.setIdStore(cbStore.getValue().getIdStore());
        }

        if ("Conductor".equals(cbRole.getValue())) {
            formCollaborator.setLicense(tfVin.getText());
        }

        if (cbStore.getValue() != null) {
            formCollaborator.setIdStore(cbStore.getValue().getIdStore());
        }

        MessageResponse response;

        if (isEditMode) {
            formCollaborator.setIdCollaborator(currentCollaborator.getIdCollaborator());
            formCollaborator.setActive(currentCollaborator.isActive());
            response = CollaboratorImp.edit(formCollaborator);

            if (!response.isError() && photoChanged && currentPhotoBytes != null) {
                MessageResponse photoResponse = CollaboratorImp.uploadPhoto(
                        currentCollaborator.getIdCollaborator(),
                        this.currentPhotoBytes
                );

                if (photoResponse.isError()) {
                    Utility.createAlert("Advertencia", "Datos guardados, fallo foto: " + photoResponse.getMessage(), NotificationType.FAILURE);
                }
            }

        } else {
            formCollaborator.setPassword(pfPassword.getText());
            formCollaborator.setActive(true);
            if (this.currentPhotoBytes != null) {
                String base64 = Base64.getEncoder().encodeToString(this.currentPhotoBytes);
                formCollaborator.setPhoto64(base64);
                formCollaborator.setPhoto(this.currentPhotoBytes);
            }

            response = CollaboratorImp.register(formCollaborator);
        }

        if (!response.isError()) {
            this.operationSuccess = true;
            closeWindow();
        } else {
            Utility.createAlert("Error", response.getMessage(), NotificationType.FAILURE);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void selectStoreById(Integer targetId) {
        if (targetId != null && cbStore.getItems() != null) {
            for (Store store : cbStore.getItems()) {
                if (store.getIdStore().equals(targetId)) {
                    cbStore.setValue(store);
                    break;
                }
            }
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
