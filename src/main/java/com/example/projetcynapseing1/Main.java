package com.example.projetcynapseing1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javafx.application.Platform;

/**
 * Main Class of the application
 * TODO : rename this class
 */
public class Main extends Application {
    private static Maze maze;
    /**
     * Start a new JavaFX windows
     * 
     * @param stage can be set to null
     * @throws IOException if a problem occurs when creating javaFX Stage or scene
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));

        FXController control = new FXController();
        fxmlLoader.setController(control);

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        maze = new Maze(3, 3);

        new Thread(() -> {
            try {
                // All the connections you had
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

                // Add edges one by one with animation
                for (int[] conn : connections) {
                    final int from = conn[0];
                    final int to = conn[1];

                    maze.addEdge(new Edge(
                            maze.getVertexByIDVertex(from),
                            maze.getVertexByIDVertex(to)
                    ));

                    Platform.runLater(() -> control.displayMaze(maze));
                    Thread.sleep(300); // delay between edge addition
                }

                // Solve the maze
                Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);
                int[] parents = solver.solveAstar(maze, maze.getVertexByIDVertex(0), maze.getVertexByIDVertex(8), MethodName.Type.COMPLETE);
                int[] solution = Solver.pathIndex(maze, maze.getVertexByIDVertex(8), parents);
                ArrayList<Edge> path_edge = Solver.pathEdge(maze, maze.getVertexByIDVertex(8), parents);

                // mark all visited vertices (which are in parents array)
                for (int i = 0; i < parents.length; i++) {
                    if (parents[i] != i || i == 0) {
                        Vertex v = maze.getVertices().get(i);
                        v.setState(VertexState.VISITED);
                        Platform.runLater(() -> control.displayMaze(maze));
                        Thread.sleep(100); // speed of visited path animation
                    }
                }

                // draw the real path in blue (solution)
                for (Vertex v : Solver.pathVertex(maze, maze.getVertexByIDVertex(8), parents)) {
                    v.setState(VertexState.SOLUTION);
                    Platform.runLater(() -> control.displayMaze(maze));
                    Thread.sleep(200);
                }


                    Platform.runLater(() -> {
                    System.out.println("Maze created:\n");
                    System.out.println(maze);
                    System.out.println("Solution found:\n");
                    System.out.println(maze.solutionToString(solution));
                    System.out.println("Edges of the path found:\n");
                    System.out.println(path_edge);
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
        /* Test */
        /*
        MazeController mazeController = new MazeController();

        mazeController.createMaze(MethodName.GenMethodName.KRUSKAL,
                MethodName.Type.COMPLETE, 3, 3, 0.0, 10);
        mazeController.findSolution(MethodName.SolveMethodName.ASTAR,
                mazeController.getCurrentMaze().getVertices().getFirst(),
                mazeController.getCurrentMaze().getVertices().getLast(),
                MethodName.Type.COMPLETE, 0.0);
        System.out.println("--Maze generated--");
        System.out.println(mazeController.getCurrentMaze());
        System.out.println("--Solution found--");
        System.out.println(mazeController.getSolution());
        */



        // mazeController.getFileController().SaveData(mazeController.getCurrentMaze());

        // mazeController.getFileController().loadMaze();
        /* JAVAFX Start application */
        launch();
    }
}