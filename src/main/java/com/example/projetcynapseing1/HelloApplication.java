package com.example.projetcynapseing1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main Class of the application
 * TODO : rename this class
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

        HelloController control = new HelloController();
        fxmlLoader.setController(control);

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        try {
            Generator Gen = new Generator(3, 3, MethodName.GenMethodName.KRUSKAL, 10, control);
            control.setGenerator(Gen);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Entry point of application
     * 
     * @param args arguments when lauching java application
     */
    public static void main(String[] args) {
        /* Test */
        /* JAVAFX Start application */
        launch();
    }
}