/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package packetworld.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        /* Pruebas de Notification
        Utility.createNotification("Prueba de Confirmación", NotificationType.CONFIRMATION);
        Utility.createNotification("Prueba de Eliminar", NotificationType.DELETE);
        Utility.createNotification("Prueba de Fallo", NotificationType.FAILURE);
        Utility.createNotification("Prueba de Información", NotificationType.INFORMATION);
        Utility.createNotification("Prueba de Conseguido", NotificationType.SUCCESS);
         */

        /*Pruebas de Alert
        Utility.createAlert("Prueba de Eliminar", "Prueba de Eliminar", NotificationType.DELETE);
        Utility.createAlert("Prueba de Confirmación", "Prueba de confirmación", NotificationType.CONFIRMATION);
        Utility.createAlert("Prueba de Fallo", "Prueba de Fallo", NotificationType.FAILURE);
        Utility.createAlert("Prueba de Información", "Prueba de Información", NotificationType.INFORMATION);
        Utility.createAlert("Prueba de Realizado", "Prueba de Realizado", NotificationType.SUCCESS);
        */

    }

}
