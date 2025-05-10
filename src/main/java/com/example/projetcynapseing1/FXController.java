package com.example.projetcynapseing1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for JavaFX Application
 * TODO : rename this class
 */
public class FXController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}