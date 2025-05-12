package com.example.projetcynapseing1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javafx.scene.image.Image;

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hello-view.fxml"));

        FXController control = new FXController();
        fxmlLoader.setController(control);

        Image icon = new Image(getClass().getResourceAsStream("/images/smiley.png"));
        stage.getIcons().add(icon);

        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("Cynapse");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Entry point of application
     * 
     * @param args arguments when lauching java application
     */
    public static void main(String[] args) {
        launch(args);
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
            maze.addEdge(new Edge(maze.getVertexByIDVertex(0), maze.getVertexByIDVertex(1)));
            maze.addEdge(new Edge(maze.getVertexByIDVertex(1), maze.getVertexByIDVertex(2)));
            maze.addEdge(new Edge(maze.getVertexByIDVertex(0), maze.getVertexByIDVertex(3)));
            maze.addEdge(new Edge(maze.getVertexByIDVertex(1), maze.getVertexByIDVertex(4)));
            maze.addEdge(new Edge(maze.getVertexByIDVertex(4), maze.getVertexByIDVertex(5)));
            maze.addEdge(new Edge(maze.getVertexByIDVertex(6), maze.getVertexByIDVertex(7)));
            maze.addEdge(new Edge(maze.getVertexByIDVertex(7), maze.getVertexByIDVertex(8)));
            maze.addEdge(new Edge(maze.getVertexByIDVertex(4), maze.getVertexByIDVertex(7)));

            System.out.println("Maze created:\n");
            System.out.println(maze);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);
        int[] parents = solver.solveAstar(maze, maze.getVertexByIDVertex(0), maze.getVertexByIDVertex(8), MethodName.Type.COMPLETE);
        int[] solution = Solver.pathIndex(maze, maze.getVertexByIDVertex(8), parents);
        ArrayList<Edge> path_edge = Solver.pathEdge(maze, maze.getVertexByIDVertex(8), parents);

        System.out.println("Solution found:\n");
        System.out.println(maze.solutionToString(solution));
        System.out.println("Edges of the path found:\n");
        System.out.println(path_edge);

        // mazeController.getFileController().SaveData(mazeController.getCurrentMaze());

        // mazeController.getFileController().loadMaze();
        /* JAVAFX Start application */
        // launch();
    }
}