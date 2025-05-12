package com.example.projetcynapseing1;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Main Class of the application
 * TODO : rename this class
 */
public class Main extends Application {
    private static Maze maze;

    @FXML
    private ImageView backgroundImage;
    /**
     * Start a new JavaFX windows
     * 
     * @param stage can be set to null
     * @throws IOException if a problem occurs when creating javaFX Stage or scene
     */
    @Override
    public void start(Stage stage) throws IOException {
        int rows=21;
        int cols=21;
        int destination = rows*cols-1;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hello-view.fxml"));



        FXController control = new FXController();
        fxmlLoader.setController(control);
        control.setMazeSize(rows, cols);
        Scene scene = new Scene(fxmlLoader.load(), 1200,700);

        Image icon = new Image(getClass().getResourceAsStream("/images/smiley.png"));
        stage.getIcons().add(icon);

        stage.setTitle("Cynapse");
        stage.setScene(scene);

    
        backgroundImage.fitWidthProperty().bind(stage.widthProperty());
        backgroundImage.fitHeightProperty().bind(stage.heightProperty());

        stage.show();


        new Thread(() -> {
            try {
                MazeController mazeController = new MazeController();
                mazeController.createMaze(MethodName.GenMethodName.KRUSKAL,
                        MethodName.Type.COMPLETE, rows, cols, 0.0, 8);

                Graph generatedGraph = mazeController.getCurrentMaze();
                maze = new Maze(rows, cols, MethodName.GenMethodName.KRUSKAL);

                for (Edge e : generatedGraph.getEdges()) {
                    int fromID = e.getVertexA().getID();
                    int toID = e.getVertexB().getID();

                    Vertex from = maze.getVertexByIDVertex(fromID);
                    Vertex to = maze.getVertexByIDVertex(toID);

                    maze.addEdge(new Edge(from, to));

                    Platform.runLater(() -> control.displayMaze(maze));
                    Thread.sleep(10);
                }
                Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);

                int[] parents = solver.solveAstar(maze, maze.getVertexByIDVertex(0), maze.getVertexByIDVertex(destination), MethodName.Type.COMPLETE);

                ArrayList<Vertex> solutionVertices = Solver.pathVertex(maze, maze.getVertexByIDVertex(destination), parents);
                // mark all visited vertices (which are in parents array)

                for (int i = 0; i < parents.length; i++) {
                    if (parents[i] != i || i == 0) {
                        Vertex v = maze.getVertices().get(i);
                        v.setState(VertexState.VISITED);
                        Platform.runLater(() -> control.displayMaze(maze));
                        Thread.sleep(25);

                    }

                }


                // draw the real path in blue (solution)
                for (Vertex v : solutionVertices) {
                    v.setState(VertexState.SOLUTION);
                    Platform.runLater(() -> control.displayMaze(maze));
                    Thread.sleep(50);
                }


                Platform.runLater(() -> {
                    System.out.println("Maze created:\n");
                    System.out.println(maze);
                    System.out.println("Solution found:\n");
                    System.out.println(maze.solutionToString(Solver.pathIndex(maze, maze.getVertexByIDVertex(8), parents)));
                    control.displayMaze(maze);
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