package com.example.projetcynapseing1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller class for JavaFX Application
 * TODO : rename this class
 */
public class FXController {
    @FXML
    private Button resolutionLabyrinth;

    @FXML
    private Button generationLabyrinth;

    private boolean labyrinthIsGenerated = false;

    @FXML
    protected void onStartResolutionClick() {
}

    @FXML
    protected void onStartGenerationClick() {
        labyrinthIsGenerated = true;
        resolutionLabyrinth.setDisable(false);

    }
}