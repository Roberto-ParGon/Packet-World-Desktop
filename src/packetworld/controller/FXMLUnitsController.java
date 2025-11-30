/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package packetworld.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class FXMLUnitsController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<?, ?> colVIN;
    @FXML
    private TableColumn<?, ?> colNII;
    @FXML
    private TableView<?> tvUnits;
    @FXML
    private TableColumn<?, ?> colBrand;
    @FXML
    private TableColumn<?, ?> colModel;
    @FXML
    private TableColumn<?, ?> colYear;
    @FXML
    private TableColumn<?, ?> colType;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        tvUnits.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }    

    @FXML
    private void handleAddUnit(ActionEvent event) {
    }

    @FXML
    private void handleEditUnit(ActionEvent event) {
    }

    @FXML
    private void handleDeleteUnit(ActionEvent event) {
    }
    
}
