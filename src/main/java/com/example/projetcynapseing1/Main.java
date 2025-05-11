package com.example.projetcynapseing1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main Class of the application.
 * Entry point for the JavaFX Maze Generator.
 * TODO: Rename this class to something more specific.
 */
public class Main extends Application {

    private static Maze maze;

    /**
     * Starts a new JavaFX window.
     *
     * @param stage Primary stage for this application.
     * @throws IOException if a problem occurs when creating JavaFX Stage or loading FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Load FXML and controller
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        FXController control = new FXController();
        fxmlLoader.setController(control);

        // Load the interface only once
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Maze Generator");
        stage.setScene(scene);
        stage.show();

        // --- Maze initialization ---
        maze = new Maze(3, 3);

        new Thread(() -> {
            try {
                ArrayList<Vertex> vertices = maze.getVertices();

                // List of connections to animate the maze generation
                int[][] connections = {
                        {0, 1},
                        {1, 2},
                        {0, 3},
                        {1, 4},
                        {4, 5},
                        {6, 7},
                        {7, 8},
                        {4, 7}
                };

                for (int[] conn : connections) {
                    int a = conn[0];
                    int b = conn[1];

                    // Update maze and refresh UI on JavaFX thread
                    Platform.runLater(() -> {
                        connect(vertices.get(a), vertices.get(b));
                        control.displayMaze(maze);
                    });

                    Thread.sleep(300); // Delay between each step (in milliseconds)
                }

                Platform.runLater(() -> {
                    System.out.println("Maze created:\n");
                    System.out.println(maze);
                });

                // --- Solve the maze after generation ---
                Thread.sleep(500); // Optional pause before solving
                Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);
                int[] parents = solver.solveAstar(
                        maze,
                        maze.getVertexByIDVertex(0),
                        maze.getVertexByIDVertex(8),
                        MethodName.Type.COMPLETE
                );
                int[] solution = Solver.path_index(maze, maze.getVertexByIDVertex(8), parents);

                Platform.runLater(() -> {
                    System.out.println("Solution found:\n");
                    System.out.println(maze.solutionToString(solution));
                    control.displayMaze(maze); // Refresh to include solution if visualized
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Entry point of application.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Utility method to connect two vertices as neighbors.
     *
     * @param a First vertex.
     * @param b Second vertex.
     */
    private static void connect(Vertex a, Vertex b) {
        a.addNeighbor(b);
        b.addNeighbor(a);
    }
}