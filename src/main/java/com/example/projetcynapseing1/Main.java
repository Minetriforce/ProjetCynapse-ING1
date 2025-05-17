package com.example.projetcynapseing1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * Main class of the application responsible for launching the JavaFX
 * application.
 * @author Bari-Joris, Lorelle, Florianne, Jonathan, Junjie
 */
public class Main extends Application {

    private final FXController fxController = new FXController();
    private final MazeController mazeController = new MazeController();

    /**
     * Initializes the necessary controllers before the start of the application.
     *
     * @throws Exception if any issue occurs during initialization
     */
    @Override
    public void init() throws Exception {
        super.init();
        fxController.setMazeController(mazeController);
    }

    /**
     * Start the JavaFX application window.
     *
     * @param stage the primary stage for the application
     * @throws Exception if any issues occur when creating the JavaFX Stage or Scene
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file for the layout and set the controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view.fxml"));
        fxmlLoader.setController(fxController);

        // Set up the scene with the loaded layout
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("Cynapse");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/smiley.png")));
        stage.show();
    }

    /**
     * Entry point of the application.
     *
     * @param args command-line arguments when launching the application
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
