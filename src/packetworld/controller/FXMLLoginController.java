/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import packetworld.domain.CollaboratorImp;
import packetworld.dto.LoginResponse;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

/**
 *
 * @author Lenovo
 */
public class FXMLLoginController implements Initializable {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField personalNumberField;
    @FXML
    private Label personalNumberError;
    @FXML
    private Label passwordError;
    @FXML
    private Label loginError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hideAllErrors();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        hideAllErrors();

        if (!validateInputs()) {
            return;
        }

        LoginResponse response = CollaboratorImp.login(personalNumberField.getText(), passwordField.getText());

        if (!response.isError() && response.getCollaborator() != null) {
            loginError.setVisible(false);
            Utility.createNotification(response.getMensaje(), NotificationType.SUCCESS);
            
            goToDashboard();
        } else {
            loginError.setText(response.getMensaje());
            loginError.setVisible(true);
        }
    }

    private void goToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/packetworld/view/FXMLDashboard.fxml"));
            Scene dashboardScene = new Scene(root);
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(dashboardScene);
            dashboardStage.setTitle("Panel de Control");

            try {
                dashboardStage.getIcons().add(new Image(getClass().getResourceAsStream("/packetworld/resources/icons/icon.png")));
            } catch (Exception e) { System.err.println("Icono no encontrado"); }
            
            dashboardStage.setMaximized(true);
            dashboardStage.show();

            Stage currentStage = (Stage) personalNumberField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            Utility.createNotification("Error al cargar el Dashboard", NotificationType.FAILURE);
        }
    }

    private boolean validateInputs() {
        boolean valid = true;

        if (isEmpty(personalNumberField)) {
            personalNumberError.setVisible(true);
            valid = false;
        }

        if (isEmpty(passwordField)) {
            passwordError.setVisible(true);
            valid = false;
        }
        return valid;
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private boolean isEmpty(PasswordField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void hideAllErrors() {
        personalNumberError.setVisible(false);
        passwordError.setVisible(false);
        loginError.setVisible(false);
    }
}