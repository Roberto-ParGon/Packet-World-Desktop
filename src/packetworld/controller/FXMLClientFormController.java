package packetworld.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import packetworld.pojo.Client;
import packetworld.utility.NotificationType;
import packetworld.utility.Utility;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

/**
 * Controlador del formulario de Cliente.
 * Soporta:
 *  - setTargetList(ObservableList<Client>) para trabajar directamente sobre la lista en memoria.
 *  - setClient(Client) para edición.
 */
public class FXMLClientFormController implements Initializable {

    @FXML private TextField tfFirstName;
    @FXML private TextField tfLastName;
    @FXML private TextField tfStreet;
    @FXML private TextField tfNumber;
    @FXML private TextField tfColony;
    @FXML private TextField tfZipCode;
    @FXML private TextField tfCity;
    @FXML private TextField tfState;
    @FXML private TextField tfPhone;
    @FXML private TextField tfEmail;

    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Button btnDelete;

    // Photo controls (in FXML: ImageView fx:id="profileImage", Button fx:id="btnSelectPhoto")
    @FXML private ImageView profileImage;
    @FXML private Button btnSelectPhoto;

    private final ValidationSupport validationSupport = new ValidationSupport();

    // Si el formulario se integra con la lista de la vista principal:
    private ObservableList<Client> targetList;

    // Cliente en edición (si aplica)
    private Client currentClient;
    private boolean isEditMode = false;

    // Generador simple de IDs para cargas en memoria (si la lista no tiene ids únicos)
    private static final AtomicInteger ID_GEN = new AtomicInteger(1000);

    // Fichero seleccionado (foto)
    private File selectedPhotoFile = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupValidation();
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);

        // Intentar cargar placeholder si existe
        try {
            Image placeholder = new Image(getClass().getResourceAsStream("/packetworld/resources/images/default-user.png"));
            if (placeholder != null) profileImage.setImage(placeholder);
        } catch (Exception ex) {
            // no placeholder available, ignorar
        }
    }

    private void setupValidation() {
        validationSupport.registerValidator(tfFirstName, Validator.createEmptyValidator("Nombre requerido"));
        validationSupport.registerValidator(tfLastName, Validator.createEmptyValidator("Apellido requerido"));
        validationSupport.registerValidator(tfEmail, Validator.createRegexValidator("Correo inválido",
                "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", null));
        // puedes agregar más validaciones si quieres
    }

    /**
     * Llamar desde el controlador que abre el formulario para proveer la lista compartida.
     * Si la lista es proporcionada, el formulario añadirá/editará/eliminará ahí.
     */
    public void setTargetList(ObservableList<Client> list) {
        this.targetList = list;
        // ajustar generador de ids al valor máximo actual para evitar colisiones
        if (list != null && !list.isEmpty()) {
            list.stream()
                .map(Client::getId)
                .filter(id -> id > 0)
                .max(Comparator.naturalOrder())
                .ifPresent(max -> ID_GEN.set(Math.max(ID_GEN.get(), max + 1)));
        }
    }

    /**
     * Pone el formulario en modo edición con el cliente proporcionado.
     */
    public void setClient(Client c) {
        if (c == null) return;
        this.currentClient = c;
        this.isEditMode = true;
        populateFieldsFromClient(c);
        btnDelete.setVisible(true);
        btnDelete.setManaged(true);

        // Si el cliente tuviera foto, cargarla (si implementas Client.photoPath)
        // try {
        //     if (c.getPhotoPath() != null) {
        //         Image img = new Image(new File(c.getPhotoPath()).toURI().toString(), 200, 200, true, true);
        //         profileImage.setImage(img);
        //     }
        // } catch (Exception ex) { /* ignore */ }
    }

    private void populateFieldsFromClient(Client c) {
        tfFirstName.setText(c.getFirstName());
        tfLastName.setText(c.getLastName());
        tfStreet.setText(c.getStreet());
        tfNumber.setText(c.getNumber());
        tfColony.setText(c.getColony());
        tfZipCode.setText(c.getZipCode());
        tfCity.setText(c.getCity());
        tfState.setText(c.getState());
        tfPhone.setText(c.getPhone());
        tfEmail.setText(c.getEmail());
    }

    private void fillClientFromFields(Client c) {
        c.setFirstName(tfFirstName.getText());
        c.setLastName(tfLastName.getText());
        c.setStreet(tfStreet.getText());
        c.setNumber(tfNumber.getText());
        c.setColony(tfColony.getText());
        c.setZipCode(tfZipCode.getText());
        c.setCity(tfCity.getText());
        c.setState(tfState.getText());
        c.setPhone(tfPhone.getText());
        c.setEmail(tfEmail.getText());
        if (c.getStatus() == null) c.setStatus("Activo");
        // Si quieres guardar la ruta de la foto:
        // if (selectedPhotoFile != null) c.setPhotoPath(selectedPhotoFile.getAbsolutePath());
    }

    @FXML
    private void handleSave() {
        if (validationSupport.isInvalid()) {
            Utility.createNotification("Corrige los campos requeridos", NotificationType.INFORMATION);
            return;
        }

        if (isEditMode) {
            // editar cliente existente
            fillClientFromFields(currentClient);
            Utility.createNotification("Cliente actualizado", NotificationType.SUCCESS);
        } else {
            // crear nuevo cliente
            Client nuevo = new Client();
            fillClientFromFields(nuevo);

            // asignar id (si la lista existe respetar ids)
            int newId = ID_GEN.getAndIncrement();
            nuevo.setId(newId);

            if (targetList != null) {
                targetList.add(nuevo);
            }
            Utility.createNotification("Cliente registrado", NotificationType.SUCCESS);
        }

        closeWindow();
    }

    @FXML
    private void handleDelete() {
        if (!isEditMode || currentClient == null) return;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar este cliente?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        Optional<ButtonType> opt = a.showAndWait();
        if (opt.isPresent() && opt.get() == ButtonType.YES) {
            if (targetList != null) {
                targetList.removeIf(c -> c.getId() == currentClient.getId());
            }
            Utility.createNotification("Cliente eliminado", NotificationType.SUCCESS);
            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Handler para seleccionar fotografía (vinculado desde el FXML: onAction="#handleSelectPhoto")
     */
    @FXML
    private void handleSelectPhoto(ActionEvent event) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Seleccionar fotografía");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
            File f = fc.showOpenDialog(btnSelectPhoto.getScene().getWindow());
            if (f != null) {
                selectedPhotoFile = f;
                Image img = new Image(f.toURI().toString(), 200, 200, true, true);
                profileImage.setImage(img);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Utility.createNotification("No se pudo cargar la imagen", NotificationType.FAILURE);
        }
    }

    private void closeWindow() {
        try {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            Utility.animateExit(btnCancel.getScene().getRoot(), stage);
        } catch (Exception ex) {
            // fallback: cerrar sin animación
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }
}