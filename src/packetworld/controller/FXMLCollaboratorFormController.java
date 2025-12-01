package packetworld.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

    private File selectedImageFile;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeRoles();
        configureRoleListener();
   
        cbStore.getItems().addAll("Sucursal Central", "Sucursal Norte", "Sucursal Sur");
    }    

    private void initializeRoles() {
        cbRole.getItems().addAll(
            "Administrador",
            "Colaborador",
            "Conductor"
        );
    }

    private void configureRoleListener() {
        cbRole.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Conductor")){
                    tfVin.setDisable(false);
                } else {
                    tfVin.setDisable(true);
                    tfVin.clear();
                }
            }
        });
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
           
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSave(ActionEvent event) {


        if (!pfPassword.getText().equals(pfConfirmPassword.getText())) {
            System.out.println("Error: Las contraseñas no coinciden");
            Utility.createAlert("Error en los campos de contraseña", "La contraseña no coincide en ambos campos.", NotificationType.FAILURE);
        }
    }
}