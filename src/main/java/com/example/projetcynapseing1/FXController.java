package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

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
    @FXML
    private TextField timeStepField;

    private MazeController mazeController;
    private boolean labyrinthIsGenerated = false;

    private Maze maze;
    private int rows;
    private int cols;
    private int seed;
    private int timeStep = 0;

    private int blockSize = (rows > 90 || cols > 90) ? 5
            : (rows > 40 || cols > 40) ? 12 : (rows > 30 || cols > 30) ? 15 : (rows > 20 || cols > 20) ? 20 : 40;

    private int destination = rows * cols - 1;
    private Set<Edge> visibleEdges = new HashSet<>();
    private MethodName.SolveMethodName currentSolveMethod;

    private Vertex firstSelectedVertex = null; // First click

    @FXML
    private void initialize() {
        backgroundImage.fitWidthProperty().bind(stackpane.widthProperty());
        backgroundImage.fitHeightProperty().bind(stackpane.heightProperty());
        generationMethodComboBox.getItems().setAll(MethodName.GenMethodName.values());
        generationMethodComboBox.getSelectionModel().selectFirst();

        solutionMethodComboBox.getItems().setAll(MethodName.SolveMethodName.values());
        solutionMethodComboBox.getSelectionModel().selectFirst();

        mazeCanvas.setOnMouseClicked(event -> {
            int col = (int) (event.getX() / blockSize);
            int row = (int) (event.getY() / blockSize);

            if (col < 0 || col >= cols || row < 0 || row >= rows)
                return;

            Vertex clickedVertex = maze.getVertexByID(row * cols + col);
            if (clickedVertex == null)
                return;

            if (firstSelectedVertex == null) {
                firstSelectedVertex = clickedVertex;
                System.out.println("First cell selected: " + firstSelectedVertex.getID());
            } else {
                toggleWallBetween(firstSelectedVertex, clickedVertex);
                firstSelectedVertex = null; // reset after second click
            }
        });

    }

    public void setMazeController(MazeController mazeController) {
        this.mazeController = mazeController;
    }

    /**
     * Method to set maze size
     * 
     * @param rows number of vertical vertices
     * @param cols number of horizontal vertices
     */
    public void setMazeSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        blockSize = (rows > 90 || cols > 90) ? 5
                : (rows > 40 || cols > 40) ? 12 : (rows > 30 || cols > 30) ? 15 : (rows > 20 || cols > 20) ? 20 : 40;
    }

    /**
     * Called when the generation button is clicked
     */
    @FXML
    private void onStartGenerationClick() {
        resetSolution();
        try {
            this.rows = Integer.parseInt(rowsField.getText());
            this.cols = Integer.parseInt(colsField.getText());
            this.seed = Integer.parseInt(seedField.getText());
            this.timeStep = Integer.parseInt(timeStepField.getText());
            this.destination = rows * cols - 1;

            System.out.println(this.timeStep);
            labyrinthIsGenerated = true;
            resolutionLabyrinth.setDisable(false);
            MethodName.GenMethodName selectedGenMethod = generationMethodComboBox.getSelectionModel().getSelectedItem();
            System.out.println("Méthode génération choisie : " + selectedGenMethod);
            new Thread(() -> generateMaze(selectedGenMethod, seed, rows, cols)).start();
        } catch (NumberFormatException e) {
            System.out.println(
                    "Rentre des valeurs de taille du labyrinthe. Les valeurs entrées doivent être des nombres entiers valides.");
        }
    }

    /**
     * Called when the "Solve" button is clicked. Starts solving the maze.
     */
    @FXML
    private void onStartResolutionClick() {
        resetSolution();
        this.timeStep = Integer.parseInt(timeStepField.getText());
        MethodName.SolveMethodName selectedSolveMethod = solutionMethodComboBox.getSelectionModel().getSelectedItem();
        System.out.println("Méthode résolution choisie : " + selectedSolveMethod);
        if (maze != null) {
            currentSolveMethod = selectedSolveMethod;
            new Thread(() -> solveMaze(selectedSolveMethod)).start();
        }
    }

    private void generateMaze(MethodName.GenMethodName generationMethod, int seed, int rows, int cols) {
        try {
            mazeController.createMaze(generationMethod, rows, cols, seed);
            Maze generatedMaze = mazeController.getCurrentMaze();
            visibleEdges.clear();

            for (Edge e : generatedMaze.getEdges()) {
                visibleEdges.add(e);
                Platform.runLater(() -> displayMaze(generatedMaze));
                Thread.sleep(this.timeStep);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void solveMaze(MethodName.SolveMethodName solveMethod) {
        try {
            mazeController.findSolution(solveMethod, maze.getVertexByID(0), maze.getVertexByID(destination));
            // Start visualizing the solution, marking visited vertices and solution path
            markVisitedAndSolutionPath(mazeController.getSolution(), mazeController.getVisited());
        } catch (Exception e) {
            System.out.println("--- Fx Controller ---");
            System.out.println(e.getMessage());
            System.out.println("------------------");
            e.printStackTrace();
        }
    }

    /**
     * Marks the visited vertices and solution path, displaying the solution
     * incrementally.
     *
     * @param orders:      array of vertices' index in the visited order
     * @param antecedents: the array of antecedents for each vertex
     */
    private void markVisitedAndSolutionPath(int[] orders, int[] antecedents) {

        for (int i = 0; i < orders.length; i++) {
            if (orders[i] == -1) {
                if (orders[i] == -1) {
                    break;
                }

                Vertex v = maze.getVertexByID(orders[i]);
                v.setState(VertexState.VISITED);
                Platform.runLater(() -> displayMaze(maze));
                try {
                    Thread.sleep(timeStep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<Vertex> solutionVertices = Solver.pathVertex(maze, maze.getVertexByID(destination), antecedents);
            for (Vertex v : solutionVertices) {
                v.setState(VertexState.SOLUTION);
                Platform.runLater(() -> displayMaze(maze));
                try {
                    Thread.sleep(timeStep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Displays the maze on the canvas.
     * 
     * @param maze the maze to display
     */
    public void displayMaze(Maze maze) {
        this.maze = maze;
        drawMazeWithWalls();
    }

    private void drawMazeWithWalls() {
        if (mazeCanvas == null) {
            System.out.println("Canvas is not initialized!");
            return;
        }

        if (rows <= 0 || cols <= 0) {
            System.out.println("Invalid maze dimensions: rows = " + rows + ", cols = " + cols);
            return;
        }

        GraphicsContext g = mazeCanvas.getGraphicsContext2D();
        mazeCanvas.setWidth(cols * blockSize);
        mazeCanvas.setHeight(rows * blockSize);
        g.setLineWidth(2);

        for (Vertex v : maze.getVertices()) {
            int x = v.getX() * blockSize;
            int y = v.getY() * blockSize;

            Color fillColor = getColorForVertex(v);

            g.setFill(fillColor);
            g.fillRect(x, y, blockSize, blockSize);

            // Draw walls between cells if they're not neighbors
            if (v.getY() != 0) {
                if (!v.getNeighbors().contains(maze.getVertexByID(v.getID() - this.cols))) {
                    g.strokeLine(x, y, x + blockSize, y); // top
                }
            }
            if (v.getX() != this.cols - 1) {
                if (!v.getNeighbors().contains(maze.getVertexByID(v.getID() + 1))) {
                    g.strokeLine(x + blockSize, y, x + blockSize, y + blockSize); // right
                }
            }
            if (v.getY() != this.rows - 1)
                if (!v.getNeighbors().contains(maze.getVertexByID(v.getID() + this.cols))) {
                    g.strokeLine(x, y + blockSize, x + blockSize, y + blockSize); // bottom
                }
            if (v.getX() != 0) {
                if (!v.getNeighbors().contains(maze.getVertexByID(v.getID() - 1))) {
                    g.strokeLine(x, y, x, y + blockSize); // left
                }
            }
        }
    }

    private Color getColorForVertex(Vertex v) {
        if (v.getID() == 0) {
            return Color.RED;
        }
        if (v.getID() == destination) {
            return Color.GREEN;
        }
        switch (v.getState()) {
            case VISITED:
                return Color.rgb(169, 169, 169);
            case SOLUTION:
                return Color.rgb(173, 216, 230);
            default:
                return Color.WHITE;

        }
    }

    private void toggleWallBetween(Vertex v1, Vertex v2) {
        // Optional: enforce adjacency to keep maze valid
        int r1 = v1.getID() / cols;
        int c1 = v1.getID() % cols;
        int r2 = v2.getID() / cols;
        int c2 = v2.getID() % cols;
        if (Math.abs(r1 - r2) + Math.abs(c1 - c2) != 1) {
            System.out.println("Vertices are not adjacent. Can't toggle wall.");
            return;
        }

        Edge edge = new Edge(v1, v2, true);

        if (visibleEdges.contains(edge)) {
            visibleEdges.remove(edge);
            maze.removeEdge(edge);
            System.out.println("Wall added between " + v1.getID() + " and " + v2.getID());
        } else {
            visibleEdges.add(edge);
            maze.addEdge(edge);
            System.out.println("Wall removed between " + v1.getID() + " and " + v2.getID());
        }

        Platform.runLater(() -> displayMaze(maze));
    }

    private void resetSolution() {
        if (maze == null) {
            System.out.println("maze is null");
            return;
        }
        for (Vertex v : maze.getVertices()) {
            v.setState(VertexState.DEFAULT);
        }
        Platform.runLater(() -> displayMaze(maze));
    }

}
