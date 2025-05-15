package com.example.projetcynapseing1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * JavaFX Controller for handling maze display, button actions, and maze
 * generation/solving.
 *
 * @author Florianne
 */
public class FXController {

    @FXML
    private ImageView backgroundImage;
    @FXML
    private StackPane stackpane;

    @FXML
    private void initialize() {
        backgroundImage.fitWidthProperty().bind(stackpane.widthProperty());
        backgroundImage.fitHeightProperty().bind(stackpane.heightProperty());
    }

    @FXML
    private Button resolutionLabyrinth;
    @FXML
    private Button generationLabyrinth;
    @FXML
    private Canvas mazeCanvas;

    private MazeController mazeController;
    private boolean labyrinthIsGenerated = false;

    private Maze maze;
    private static int rows = 10;
    private static int cols = 20;
    private int blockSize = (rows > 90 || cols > 90) ? 5
            : (rows > 40 || cols > 40) ? 12 : (rows > 30 || cols > 30) ? 15 : (rows > 20 || cols > 20) ? 20 : 40;
    private int[] antecedents; // Store the solution path antecedents
    private static int destination = rows * cols - 1;

    /**
     * Sets the maze controller.
     *
     * @param mazeController the MazeController instance to be set
     */
    public void setMazeController(MazeController mazeController) {
        this.mazeController = mazeController;
    }

    // Method to set maze size
    public void setMazeSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        if (rows > 20 || cols > 20) {
            blockSize = 20;
        } else {
            blockSize = 40;
        }
    }

    // Called when the generation button is clicked
    @FXML
    protected void onStartGenerationClick() {
        labyrinthIsGenerated = true;
        resolutionLabyrinth.setDisable(false);
        // Start maze generation on a new thread to avoid blocking the UI
        new Thread(() -> generateMaze()).start();
    }

    /**
     * Called when the "Solve" button is clicked. Starts solving the maze.
     */
    @FXML
    protected void onStartResolutionClick() {
        if (maze != null) {
            // Start maze solving on a new thread to avoid blocking the UI
            new Thread(() -> solveMaze()).start();
        }
    }

    /**
     * Generates the maze using the specified algorithm.
     */
    private void generateMaze() {
        try {
            mazeController.createMaze(MethodName.GenMethodName.DFS, MethodName.Type.COMPLETE, rows, cols, 0.0, 9);
            Maze generatedMaze = mazeController.getCurrentMaze();

            maze = new Maze(rows, cols, MethodName.GenMethodName.PRIM);

            for (Edge e : generatedMaze.getEdges()) {
                int fromID = e.getVertexA().getID();
                int toID = e.getVertexB().getID();
                Vertex from = maze.getVertexByID(fromID);
                Vertex to = maze.getVertexByID(toID);
                maze.addEdge(new Edge(from, to));

                Platform.runLater(() -> displayMaze(maze));
                Thread.sleep(10);
            }

            Platform.runLater(() -> System.out.println("Maze generated successfully"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Solves the maze using the specified algorithm, visualizing the steps one by
     * one.
     */

    private void solveMaze() {
        Solver solver = new Solver(MethodName.SolveMethodName.LEFTHAND);

        int[] antecedents = solver.solve(maze, maze.getVertexByID(0), maze.getVertexByID(destination), MethodName.Type.COMPLETE);
        int[] orders = solver.solve(maze, maze.getVertexByID(0), maze.getVertexByID(destination), MethodName.Type.STEPPER);

        try {
            // Start visualizing the solution, marking visited vertices and solution path
            markVisitedAndSolutionPath(orders, antecedents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Marks the visited vertices and solution path, displaying the solution
     * incrementally.
     *
     * @param orders: array of vertices' index in the visited order
     * @param antecedents: the array of antecedents for each vertex
     */
    private void markVisitedAndSolutionPath(int[] orders, int[] antecedents) {

        
        for (int i = 0; i < orders.length; i++) {
            if (orders[i] == -1){
                break;
            }

            Vertex v = maze.getVertexByID(orders[i]);
            v.setState(VertexState.VISITED);
            Platform.runLater(() -> displayMaze(maze));
            try {
                Thread.sleep(50); // Sleep between each update to show the solution path
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Vertex> solutionVertices = Solver.pathVertex(maze, maze.getVertexByID(destination), antecedents);
        for (Vertex v : solutionVertices) {
            v.setState(VertexState.SOLUTION);
            Platform.runLater(() -> displayMaze(maze));
            try {
                Thread.sleep(50); // Sleep between each update to show the solution path
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Displays the maze on the canvas.
     */
    public void displayMaze(Maze maze) {
        this.maze = maze;
        drawMazeWithWalls();
    }

    /**
     * Draws the maze with walls and visited states on the canvas.
     */
    private void drawMazeWithWalls() {
        if (mazeCanvas == null) {
            System.out.println("Canvas is not initialized!");
            return;
        }

        // Check for zero or negative rows/cols to avoid division by zero
        if (rows <= 0 || cols <= 0) {
            System.out.println("Invalid maze dimensions: rows = " + rows + ", cols = " + cols);
            return;
        }

        GraphicsContext g = mazeCanvas.getGraphicsContext2D();
        mazeCanvas.setWidth(cols * blockSize);
        mazeCanvas.setHeight(rows * blockSize);
        g.setLineWidth(2);

        for (Vertex v : maze.getVertices()) {
            int id = v.getID();
            int row = id / cols;
            int col = id % cols;

            int x = col * blockSize;
            int y = row * blockSize;

            Color fillColor = getColorForVertex(v);

            g.setFill(fillColor);
            g.fillRect(x, y, blockSize, blockSize);

            // Draw walls between cells if they're not neighbors
            if (!hasNeighbor(v, row - 1, col)) {
                g.strokeLine(x, y, x + blockSize, y); // top
            }
            if (!hasNeighbor(v, row, col + 1)) {
                g.strokeLine(x + blockSize, y, x + blockSize, y + blockSize); // right
            }
            if (!hasNeighbor(v, row + 1, col)) {
                g.strokeLine(x, y + blockSize, x + blockSize, y + blockSize); // bottom
            }
            if (!hasNeighbor(v, row, col - 1)) {
                g.strokeLine(x, y, x, y + blockSize); // left
            }
        }
    }

    /**
     * Returns the color for a vertex based on its state.
     *
     * @param v the vertex to get the color for
     * @return the color to fill the vertex
     */
    private Color getColorForVertex(Vertex v) {
        switch (v.getState()) {
            case VISITED:
                return Color.rgb(169, 169, 169);
            case SOLUTION:
                return Color.rgb(173, 216, 230);
            default:
                return Color.WHITE;
        }
    }

    /**
     * Checks if a vertex has a neighbor at the specified position.
     *
     * @param v the vertex to check
     * @param r the row of the neighbor
     * @param c the column of the neighbor
     * @return true if the vertex has a neighbor at the given position, false
     *         otherwise
     */
    private boolean hasNeighbor(Vertex v, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols)
            return false;
        Vertex neighbor = maze.getVertexByID(r * cols + c);
        return v.isNeighbor(neighbor);
    }
}