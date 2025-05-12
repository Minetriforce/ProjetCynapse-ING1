package com.example.projetcynapseing1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for JavaFX Application
 */
public class FXController {
    private MazeController mazeController;

    public void setMazeController(MazeController mazeController) {
        if (mazeController == null) {
            System.out.println("-- FX Controller --");
            System.out.println("Warning : maze controller is null");
        } else {
            this.mazeController = mazeController;
        }
    }

    @FXML
    private Label welcomeText;

    @FXML
    private Label counterLabel;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onStartGenerationClick() {
        mazeController.createMaze(MethodName.GenMethodName.KRUSKAL, MethodName.Type.STEPPER, 4, 4, 1.5, 10);
        welcomeText.setText("Géneration en cours");
        counterLabel.setText("Étape n°0");

    }

    public void currentStepGenerator(Integer i) {
        counterLabel.setText("Étape n°" + i);
    }
}