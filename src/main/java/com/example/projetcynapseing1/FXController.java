package com.example.projetcynapseing1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
        if (! labyrinthIsGenerated) {
            Alert alertLabyrinthNotGenerated = new Alert(AlertType.WARNING);
            alertLabyrinthNotGenerated.setTitle("error lab");
            alertLabyrinthNotGenerated.setHeaderText("Labyrinth isn't generated");
            alertLabyrinthNotGenerated.setContentText("Please start by generating a labyrinth");
            alertLabyrinthNotGenerated.showAndWait();
        }
        else {
            resolutionLabyrinth.setText("Resolving the labyrinth");
        }
}

    @FXML
    protected void onStartGenerationClick() {
        generationLabyrinth.setText("Generating the labyrinth");
        labyrinthIsGenerated = true;

        resolutionLabyrinth.setDisable(false);

    }
}