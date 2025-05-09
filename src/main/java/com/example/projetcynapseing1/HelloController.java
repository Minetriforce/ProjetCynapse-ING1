package com.example.projetcynapseing1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for JavaFX Application
 * TODO : rename this class
 */
public class HelloController {
    private Generator refGenerator;
    @FXML
    private Label welcomeText;

    @FXML
    private Label etapeLabel;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onStartGenerationClick() {
        if (refGenerator != null) {
            Graph maze = refGenerator.makeMaze(MethodName.Type.STEPPER);
            System.out.println(maze);
        } else {
            System.out.println("Générateur non initialisé !");
        }
    }

    public void setGenerator(Generator gen) {
        this.refGenerator = gen;
    }

    public void afficherEtape(Integer etape) {
        Platform.runLater(() -> etapeLabel.setText("Etape " + etape));
    }
}