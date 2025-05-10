package com.example.projetcynapseing1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main Class of the application
 * TODO : rename this class
 */
public class Main extends Application {

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

        Maze maze = new Maze(3, 3);
        try {
            ArrayList<Vertex> vertices = maze.getVertices();
            vertices.get(0).addNeighbor(vertices.get(1));
            vertices.get(1).addNeighbor(vertices.get(2));
            vertices.get(0).addNeighbor(vertices.get(3));
            vertices.get(1).addNeighbor(vertices.get(4));
            vertices.get(4).addNeighbor(vertices.get(5));
            vertices.get(6).addNeighbor(vertices.get(7));
            vertices.get(7).addNeighbor(vertices.get(8));
            vertices.get(4).addNeighbor(vertices.get(7));

            System.out.println("Maze created:\n");
            System.out.println(maze);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);
        int[] solution = solver.solveAstar(maze, maze.getVertexByIDVertex(0), maze.getVertexByIDVertex(8), MethodName.Type.COMPLETE);
        System.out.println("Solution found:\n");
        System.out.println(maze.solutionToString(solution));

        // mazeController.getFileController().SaveData(mazeController.getCurrentMaze());

        // mazeController.getFileController().loadMaze();
        /* JAVAFX Start application */
        // launch();
    }
}