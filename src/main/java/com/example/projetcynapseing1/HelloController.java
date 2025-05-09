package com.example.projetcynapseing1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for JavaFX Application
 */
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}