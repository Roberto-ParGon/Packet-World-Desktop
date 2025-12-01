/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package packetworld.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FXMLCollaboratorsController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<?, ?> colCurp;
    @FXML
    private TableView<?> tvCollaborators;
    @FXML
    private TableColumn<?, ?> colName;
    @FXML
    private TableColumn<?, ?> colLastname;
    @FXML
    private TableColumn<?, ?> colSurname;
    @FXML
    private TableColumn<?, ?> colEmail;
    @FXML
    private TableColumn<?, ?> colpersonalNumber;
    @FXML
    private TableColumn<?, ?> colRole;
    @FXML
    private TableColumn<?, ?> colStore;
    @FXML
    private TableColumn<?, ?> colLicense;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        tvCollaborators.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }    

    @FXML
    private void handleAddCollaborator(ActionEvent event) throws IOException {
        
         Parent root = FXMLLoader.load(getClass().getResource("/packetworld/view/FXMLCollaboratorForm.fxml"));
                Scene collaboratorScene = new Scene(root);
                Stage collaboratorStage = new Stage();
                collaboratorStage.initModality(Modality.APPLICATION_MODAL);
                collaboratorStage.setScene(collaboratorScene);
                collaboratorStage.setTitle("Formulario de Colaborador");
                collaboratorStage.getIcons().add(new Image(getClass().getResourceAsStream("/packetworld/resources/icons/icon.png")));
                collaboratorStage.showAndWait();
        
    }

    @FXML
    private void handleEditCollaborator(ActionEvent event) {
    }

    @FXML
    private void handleAssignCollaborator(ActionEvent event) {
    }

    @FXML
    private void handleDeleteCollaborator(ActionEvent event) {
    }
    
}
