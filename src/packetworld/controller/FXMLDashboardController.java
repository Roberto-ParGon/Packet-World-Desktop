package packetworld.controller;

import animatefx.animation.FadeIn;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import packetworld.pojo.Collaborator;
import packetworld.utility.UserSession;

public class FXMLDashboardController implements Initializable {

    @FXML
    private BorderPane mainPane;
    @FXML
    private StackPane contentArea;
    private List<Button> menuButtons;
    @FXML
    private Label lbCollaboratorName;
    @FXML
    private Label lblCollaboratorRole;
    @FXML
    private Button btnCollaborators;
    @FXML
    private Button btnUnits;
    @FXML
    private Button btnStores;
    @FXML
    private Button btnCustomers;
    @FXML
    private Button btnDeliveries;
    @FXML
    private Button btnPackages;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuButtons = Arrays.asList(btnCollaborators, btnUnits, btnStores, btnCustomers, btnDeliveries, btnPackages);

        loadUserData();
        loadView("/packetworld/view/FXMLCollaborators.fxml");
    }

    private void loadUserData() {
        UserSession session = UserSession.getInstance();
        if (session != null && session.getUser() != null) {
            Collaborator user = session.getUser();
            String fullName = user.getName() + " " + user.getLastname();
            lbCollaboratorName.setText(fullName);
            lblCollaboratorRole.setText(user.getRole());
        } else {
            lbCollaboratorName.setText("Usuario Desconocido");
            lblCollaboratorRole.setText("Invitado");
        }
    }

    @FXML
    private void handleCollaboratorsMenu(ActionEvent event) {
        setActiveMenuButton(btnCollaborators);
        loadView("/packetworld/view/FXMLCollaborators.fxml");
    }

    @FXML
    private void handleUnitsMenu(ActionEvent event) {
        setActiveMenuButton(btnUnits);
        loadView("/packetworld/view/FXMLUnits.fxml");
    }

    @FXML
    private void handleStoresMenu(ActionEvent event) {
        setActiveMenuButton(btnStores);
        loadView("/packetworld/view/FXMLStores.fxml");
    }

    @FXML
    private void handleCustomersMenu(ActionEvent event) {
        setActiveMenuButton(btnCustomers);
        loadView("/packetworld/view/FXMLClients.fxml");
    }

    @FXML
    private void handleDeliveriesMenu(ActionEvent event) {
        setActiveMenuButton(btnDeliveries);
        loadView("/packetworld/view/FXMLEnvios.fxml");
    }

    @FXML
    private void handlePackpagesMenu(ActionEvent event) {
        setActiveMenuButton(btnPackages);
        loadView("/packetworld/view/FXMLPackages.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.cleanUserSession();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packetworld/view/FXMLLogin.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Inicio de sesión de Packet-World");
            loginStage.setMaximized(true);

            try {
                loginStage.getIcons().add(new Image(getClass().getResourceAsStream("/packetworld/resources/icons/icon.png")));
            } catch (Exception e) {
            }

            loginStage.show();
            Stage currentStage = (Stage) mainPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            new FadeIn(contentArea).play();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la vista: " + fxmlPath);
        }
    }

    private void setActiveMenuButton(Button selectedButton) {
        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("menu-button-active");
        }
        if (!selectedButton.getStyleClass().contains("menu-button-active")) {
            selectedButton.getStyleClass().add("menu-button-active");
        }
    }
}
