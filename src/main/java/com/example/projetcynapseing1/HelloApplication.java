package com.example.projetcynapseing1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main Class of the application
 */
public class HelloApplication extends Application {

    /**
     * Start a new JavaFX windows
     * 
     * @param stage can be set to null
     * @throws IOException if a problem occurs when creating javaFX Stage or scene
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Entry point of application
     * 
     * @param args
     */
    public static void main(String[] args) {
        /* Test */
        try {
            Generator Gen = new Generator(3, 3, MethodName.GenMethodName.PRIM, 10);
            Graph maze = Gen.makeMaze(MethodName.Type.COMPLETE);
            System.out.println(maze);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        /* JAVAFX Start application */
        // launch();
    }
}