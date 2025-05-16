package com.example.projetcynapseing1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import java.util.Set;
import java.util.HashSet;
import javafx.scene.control.ComboBox;

import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * JavaFX Controller for handling maze display, button actions, and maze
 * generation/solving.
 *
 * @author Florianne, Lorelle
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
        generationMethodComboBox.getItems().setAll(MethodName.GenMethodName.values());
        generationMethodComboBox.getSelectionModel().selectFirst();

        solutionMethodComboBox.getItems().setAll(MethodName.SolveMethodName.values());
        solutionMethodComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private Button resolutionLabyrinth;
    @FXML
    private Button generationLabyrinth;
    @FXML
    private Canvas mazeCanvas;

    @FXML
    private ComboBox<MethodName.GenMethodName> generationMethodComboBox;

    @FXML
    private ComboBox<MethodName.SolveMethodName> solutionMethodComboBox;


    @FXML
    private TextField rowsField;
    @FXML
    private TextField colsField;
    @FXML
    private TextField seedField;



    private MazeController mazeController;
    private boolean labyrinthIsGenerated = false;

    private Maze maze;
    private static int rows ;
    private static int cols;
    private static int seed;
    private int blockSize = (rows > 90 || cols > 90) ? 5
            : (rows > 40 || cols > 40) ? 12 : (rows > 30 || cols > 30) ? 15 : (rows > 20 || cols > 20) ? 20 : 40;
    private int[] antecedents; // Store the solution path antecedents
    private static int destination = rows * cols - 1;
    private Set<Edge> visibleEdges = new HashSet<>();


    /**
     * Set the maze controller.
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
        try {

            int inputRows = Integer.parseInt(rowsField.getText());;
        int inputCols = Integer.parseInt(colsField.getText());
        int inputSeed = Integer.parseInt(seedField.getText());
        this.rows = inputRows;
        this.cols = inputCols;
        this.seed = inputSeed;
        this.destination = rows * cols - 1;



            labyrinthIsGenerated = true;
        resolutionLabyrinth.setDisable(false);
        MethodName.GenMethodName selectedGenMethod = generationMethodComboBox.getSelectionModel().getSelectedItem();
        System.out.println("Méthode génération choisie : " + selectedGenMethod);
        new Thread(() -> generateMaze(selectedGenMethod, inputSeed, rows, cols)).start();
    } catch (NumberFormatException e) {
        System.out.println("Rentre des valeurs de taille du labyrinthe. Les valeurs entrées doivent être des nombres entiers valides.");
    }
}


    /**
     * Called when the "Solve" button is clicked. Starts solving the maze.
     */
    @FXML
    protected void onStartResolutionClick() {

        MethodName.SolveMethodName selectedSolveMethod = solutionMethodComboBox.getSelectionModel().getSelectedItem();
        System.out.println("Méthode résolution choisie : " + selectedSolveMethod);
        if (maze != null) {

            new Thread(() -> solveMaze(selectedSolveMethod)).start();
        }
    }

    /**
     * Generates the maze using the specified algorithm.
     */
    private void generateMaze(MethodName.GenMethodName generationMethod, int seed, int rows, int cols) {
        try {
            mazeController.createMaze(generationMethod, MethodName.Type.COMPLETE, rows, cols, 0.0, seed);
            Maze generatedMaze = mazeController.getCurrentMaze();
            visibleEdges.clear();

            for (Edge e : generatedMaze.getEdges()) {
                visibleEdges.add(e);
                Platform.runLater(() -> displayMaze(generatedMaze));
                Thread.sleep(10);
            }}
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Solves the maze using the specified algorithm, visualizing the steps one by
     * one.
     */

    private void solveMaze(MethodName.SolveMethodName solveMethod) {
        Solver solver = new Solver(solveMethod);

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
     * @param maze the maze to display
     */
    public void displayMaze(Maze maze) {
        this.maze = maze;
        drawMazeWithWalls();
    }

    /**
     * If there is an edge connecting the two cells, it means passage is possible
     * between them, so no wall is drawn.
     * If there is no edge between the two cells (i.e., they are not neighbors),
     * a wall is drawn to block the passage.
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
            // TODO : remplacer par les fonctions v.getX() et v.getY()
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
        if (neighbor == null)
            return false;
        return visibleEdges.contains(new Edge(v, neighbor, true));
    }
}