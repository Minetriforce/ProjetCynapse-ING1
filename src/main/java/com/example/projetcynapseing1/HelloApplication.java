package com.example.projetcynapseing1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        /* Test */

        /* JAVAFX Start application */
        launch();
    }

    public static void displayGridGraph(Graph G, int rows, int cols) {
        ArrayList<Vertex> vertices = G.getVertices();

        // Affichage ligne par ligne
        for (int n = 0; n < rows; n++) {
            // --- Afficher les ID + les arêtes horizontales
            for (int m = 0; m < cols; m++) {
                Vertex current = vertices.get(n * cols + m);
                System.out.printf("%2d", current.getID());

                // S'il y a un voisin à droite, on affiche une arête
                if (m < cols - 1) {
                    Vertex right = vertices.get(n * cols + (m + 1));
                    if (current.isNeighbor(right)) {
                        System.out.print("-");
                    } else {
                        System.out.print("  "); // Pas de lien
                    }
                }
            }
            System.out.println(); // Fin de la ligne des IDs

            // --- Afficher les arêtes verticales (vers le bas)
            if (n < rows - 1) {
                for (int m = 0; m < cols; m++) {
                    Vertex current = vertices.get(n * cols + m);
                    Vertex bottom = vertices.get((n + 1) * cols + m);

                    if (current.isNeighbor(bottom)) {
                        System.out.print("|");
                    } else {
                        System.out.print("   "); // Pas de lien
                    }

                    // Petite correction pour éviter un décalage après chaque colonne sauf la
                    // dernière
                    if (m < cols - 1) {
                        System.out.print("  ");
                    }
                }
                System.out.println(); // Fin de la ligne des liens verticaux
            }
        }
    }
}