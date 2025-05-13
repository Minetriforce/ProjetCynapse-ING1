package com.example.projetcynapseing1;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Main Class of the application
 */
public class Main extends Application {
    private static Maze maze;

    private final FXController fxController = new FXController();
    private final MazeController mazeController = new MazeController();
    @Override
    public void init() throws Exception {
        super.init();
        mazeController.setFXController(fxController);
    }
    /**
     * Start a new JavaFX windows
     * 
     * @param stage can be set to null
     * @throws IOException if a problem occurs when creating javaFX Stage or scene
     */
    @Override
    public void start(Stage stage) throws IOException {
        int rows=10;
        int cols=10;
        int destination = rows*cols-1;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hello-view.fxml"));

        fxmlLoader.setController(fxController);
        fxController.setMazeSize(rows, cols);
        Scene scene = new Scene(fxmlLoader.load(), 1200,700);

        stage.setTitle("Cynapse");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/smiley.png")));

        stage.show();


        new Thread(() -> {
            try {
                MazeController mazeController = new MazeController();
                mazeController.createMaze(MethodName.GenMethodName.KRUSKAL,
                        MethodName.Type.COMPLETE, rows, cols, 0.0, 9);

                Graph generatedGraph = mazeController.getCurrentMaze();
                maze = new Maze(rows, cols, MethodName.GenMethodName.KRUSKAL);

                for (Edge e : generatedGraph.getEdges()) {
                    int fromID = e.getVertexA().getID();
                    int toID = e.getVertexB().getID();

                    Vertex from = maze.getVertexByIDVertex(fromID);
                    Vertex to = maze.getVertexByIDVertex(toID);

                    maze.addEdge(new Edge(from, to));

                    Platform.runLater(() -> fxController.displayMaze(maze));
                    Thread.sleep(10);
                }
                Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);

                int[] antecedents = solver.solveRightHand(maze, maze.getVertexByIDVertex(0), maze.getVertexByIDVertex(destination), MethodName.Type.COMPLETE);

                ArrayList<Vertex> solutionVertices = Solver.pathVertex(maze, maze.getVertexByIDVertex(destination), antecedents);
                // mark all visited vertices (which are in antecedents array)

                for (int i = 0; i < antecedents.length; i++) {
                    if (antecedents[i] != i || i == 0) {
                        Vertex v = maze.getVertices().get(i);
                        v.setState(VertexState.VISITED);
                        Platform.runLater(() -> fxController.displayMaze(maze));
                        Thread.sleep(25);

                    }

                }


                // draw the real path in blue (solution)
                for (Vertex v : solutionVertices) {
                    v.setState(VertexState.SOLUTION);
                    Platform.runLater(() -> fxController.displayMaze(maze));
                    Thread.sleep(50);
                }


                Platform.runLater(() -> {
                    System.out.println("Maze created:\n");
                    System.out.println(maze);
                    System.out.println("Solution found:\n");
                    System.out.println(maze.solutionToString(Solver.pathIndex(maze, maze.getVertexByIDVertex(destination), antecedents)));
                    fxController.displayMaze(maze);
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    /**
     * Entry point of application
     * 
     * @param args arguments when lauching java application
     */
    public static void main(String[] args) {

        /* JAVAFX Start application */
        launch(args);
    }
}