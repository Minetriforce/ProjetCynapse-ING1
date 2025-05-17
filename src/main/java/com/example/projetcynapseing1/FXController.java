package com.example.projetcynapseing1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

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
    private static int rows;
    private static int cols;
    private static int seed;
    private static int timeStep = 0;


    private int blockSize = (rows > 90 || cols > 90) ? 5
            : (rows > 40 || cols > 40) ? 12 : (rows > 30 || cols > 30) ? 15 : (rows > 20 || cols > 20) ? 20 : 40;

    private static int destination = rows * cols - 1;
    private Set<Edge> visibleEdges = new HashSet<>();
    private MethodName.SolveMethodName currentSolveMethod;

    private Vertex firstSelectedVertex = null;  // First click

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

            if (col < 0 || col >= cols || row < 0 || row >= rows) return;

            Vertex clickedVertex = maze.getVertexByID(row * cols + col);
            if (clickedVertex == null) return;

            if (firstSelectedVertex == null) {
                firstSelectedVertex = clickedVertex;
                System.out.println("First cell selected: " + firstSelectedVertex.getID());
            } else {
                toggleWallBetween(firstSelectedVertex, clickedVertex);
                firstSelectedVertex = null;  // reset after second click
            }
        });


    }

    public void setMazeController(MazeController mazeController) {
        this.mazeController = mazeController;
    }

    public void setMazeSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        blockSize = (rows > 90 || cols > 90) ? 5
                : (rows > 40 || cols > 40) ? 12 : (rows > 30 || cols > 30) ? 15 : (rows > 20 || cols > 20) ? 20 : 40;
    }

    @FXML
    private void onStartGenerationClick() {
        try {
            int inputRows = Integer.parseInt(rowsField.getText());
            int inputCols = Integer.parseInt(colsField.getText());
            int inputSeed = Integer.parseInt(seedField.getText());
            int inputTimeStep = Integer.parseInt(timeStepField.getText()) < 0 ? 0 : Integer.parseInt(timeStepField.getText());

            this.rows = inputRows;
            this.cols = inputCols;
            this.seed = inputSeed;
            this.destination = rows * cols - 1;
            this.timeStep = inputTimeStep;

            labyrinthIsGenerated = true;
            resolutionLabyrinth.setDisable(false);
            MethodName.GenMethodName selectedGenMethod = generationMethodComboBox.getSelectionModel().getSelectedItem();
            System.out.println("Méthode génération choisie : " + selectedGenMethod);
            new Thread(() -> generateMaze(selectedGenMethod, inputSeed, rows, cols)).start();
        } catch (NumberFormatException e) {
            System.out.println("Rentre des valeurs de taille du labyrinthe. Les valeurs entrées doivent être des nombres entiers valides.");
        }
    }

    @FXML
    private void onStartResolutionClick() {
        MethodName.SolveMethodName selectedSolveMethod = solutionMethodComboBox.getSelectionModel().getSelectedItem();
        System.out.println("Méthode résolution choisie : " + selectedSolveMethod);
        if (maze != null) {
            currentSolveMethod = selectedSolveMethod;
            new Thread(() -> solveMaze(selectedSolveMethod)).start();
        }
    }

    private void generateMaze(MethodName.GenMethodName generationMethod, int seed, int rows, int cols) {
        try {
            mazeController.createMaze(generationMethod, MethodName.Type.COMPLETE, rows, cols, 0.0, seed);
            Maze generatedMaze = mazeController.getCurrentMaze();
            visibleEdges.clear();

            for (Edge e : generatedMaze.getEdges()) {
                visibleEdges.add(e);
                Platform.runLater(() -> displayMaze(generatedMaze));
                Thread.sleep(timeStep);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void solveMaze(MethodName.SolveMethodName solveMethod) {
        Solver solver = new Solver(solveMethod);

        int[] antecedents = solver.solve(maze, maze.getVertexByID(0), maze.getVertexByID(destination), MethodName.Type.COMPLETE);
        int[] orders = solver.solve(maze, maze.getVertexByID(0), maze.getVertexByID(destination), MethodName.Type.STEPPER);

        markVisitedAndSolutionPath(orders, antecedents);
    }

    private void markVisitedAndSolutionPath(int[] orders, int[] antecedents) {
        for (int i = 0; i < orders.length; i++) {
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
            int id = v.getID();
            int row = id / cols;
            int col = id % cols;

            int x = col * blockSize;
            int y = row * blockSize;

            Color fillColor = getColorForVertex(v);

            g.setFill(fillColor);
            g.fillRect(x, y, blockSize, blockSize);

            if (!hasNeighbor(v, row - 1, col)) {
                g.strokeLine(x, y, x + blockSize, y);
            }
            if (!hasNeighbor(v, row, col + 1)) {
                g.strokeLine(x + blockSize, y, x + blockSize, y + blockSize);
            }
            if (!hasNeighbor(v, row + 1, col)) {
                g.strokeLine(x, y + blockSize, x + blockSize, y + blockSize);
            }
            if (!hasNeighbor(v, row, col - 1)) {
                g.strokeLine(x, y, x, y + blockSize);
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

    private boolean hasNeighbor(Vertex v, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols)
            return false;
        Vertex neighbor = maze.getVertexByID(r * cols + c);
        if (neighbor == null)
            return false;
        return visibleEdges.contains(new Edge(v, neighbor, true));
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



}
