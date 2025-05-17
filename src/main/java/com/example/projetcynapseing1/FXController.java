package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.ToggleButton;

/**
 * This class manages the JavaFX interface, with maze canvas, combo boxes for selecting methods,
 * user-defined parameters, and displays maze changes dynamically.
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

    @FXML
    private TextField startField;
    @FXML
    private TextField endField;

    @FXML
    private ToggleButton changeStartEndButton;
    private boolean selectingStart = true;

    @FXML
    private CheckBox stepByStepCheckBox;

    private MazeController mazeController;
    private boolean labyrinthIsGenerated = false;

    private Maze maze;
    private int rows;
    private int cols;
    private int seed;
    private int timeStep = 0;
    private int start = 0;
    private int end = 0;

    private int blockSize = 20;  // default initial block size

    private Set<Edge> visibleEdges = new HashSet<>();
    private MethodName.SolveMethodName currentSolveMethod;

    private Vertex firstSelectedVertex = null;  // First clicked vertex for toggling walls

    /**
     * Initializes UI bindings and event listeners.
     * Automatically called by the JavaFX framework after FXML loading.
     */
    @FXML
    private void initialize() {
        backgroundImage.fitWidthProperty().bind(stackpane.widthProperty());
        backgroundImage.fitHeightProperty().bind(stackpane.heightProperty());

        generationMethodComboBox.getItems().setAll(MethodName.GenMethodName.values());
        generationMethodComboBox.getSelectionModel().selectFirst();

        solutionMethodComboBox.getItems().setAll(MethodName.SolveMethodName.values());
        solutionMethodComboBox.getSelectionModel().selectFirst();

        // Disable startField and endField initially
        startField.setDisable(true);
        endField.setDisable(true);

        // Activate start/end fields only when toggle button is selected
        changeStartEndButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            startField.setDisable(!isNowSelected);
            endField.setDisable(!isNowSelected);
        });

        // Listen checkbox to enable/disable timestep input
        stepByStepCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            timeStepField.setDisable(!isNowSelected);
            if (!isNowSelected) {
                timeStepField.setText("");  // Clear field when disabled
                timeStep = 0;
            }
        });
        timeStepField.setDisable(true); // disable timestep initially

        // Mouse click on maze canvas: either toggle walls or set start/end points based on toggle button
        mazeCanvas.setOnMouseClicked(event -> {
            int col = (int) (event.getX() / blockSize);
            int row = (int) (event.getY() / blockSize);

            if (col < 0 || col >= cols || row < 0 || row >= rows) return;

            Vertex clickedVertex = maze.getVertexByID(row * cols + col);
            if (clickedVertex == null) return;

            if (changeStartEndButton.isSelected()) {
                // Select start or end vertex alternately and update fields + redraw
                if (selectingStart) {
                    start = clickedVertex.getID();
                    startField.setText(String.valueOf(start));
                    System.out.println("Start vertex selected: " + start);
                    selectingStart = false;
                } else {
                    end = clickedVertex.getID();
                    endField.setText(String.valueOf(end));
                    System.out.println("End vertex selected: " + end);
                    selectingStart = true;
                }
                displayMaze(maze);  // redraw to update colors immediately
            } else {
                // Wall toggling between two clicked vertices
                if (firstSelectedVertex == null) {
                    firstSelectedVertex = clickedVertex;
                    System.out.println("First cell selected: " + firstSelectedVertex.getID());
                } else {
                    toggleWallBetween(firstSelectedVertex, clickedVertex);
                    firstSelectedVertex = null;
                }
            }
        });
    }

    /**
     * Sets the controller responsible for managing maze logic.
     *
     * @param mazeController the controller to use for maze creation and solving
     */
    public void setMazeController(MazeController mazeController) {
        this.mazeController = mazeController;
    }

    /**
     * Updates the internal maze size and recalculates block size for drawing.
     *
     * @param rows number of rows
     * @param cols number of columns
     */
    public void setMazeSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        blockSize = (rows > 90 || cols > 90) ? 5
                : (rows > 40 || cols > 40) ? 12
                : (rows > 30 || cols > 30) ? 15
                : (rows > 20 || cols > 20) ? 20
                : 40;
    }

    /**
     * Starts maze generation on button click.
     * Reads parameters, updates internal state, and launches generation thread.
     */
    @FXML
    private void onStartGenerationClick() {
        try {
            int inputRows = Integer.parseInt(rowsField.getText());
            int inputCols = Integer.parseInt(colsField.getText());
            int inputSeed = Integer.parseInt(seedField.getText());

            int inputTimeStep = 0;
            if (stepByStepCheckBox.isSelected() && !timeStepField.getText().isEmpty()) {
                inputTimeStep = Integer.parseInt(timeStepField.getText());
            }

            this.rows = inputRows;
            this.cols = inputCols;
            this.seed = inputSeed;
            this.timeStep = Math.max(0, inputTimeStep);
            this.end = rows * cols - 1;

            setMazeSize(rows, cols);

            labyrinthIsGenerated = true;
            resolutionLabyrinth.setDisable(false);

            MethodName.GenMethodName selectedGenMethod = generationMethodComboBox.getSelectionModel().getSelectedItem();
            System.out.println("Selected generation method: " + selectedGenMethod);

            new Thread(() -> generateMaze(selectedGenMethod, seed, rows, cols)).start();
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid integers for all input fields.");
        }
    }

    /**
     * Starts maze solving on button click.
     * Reads start/end points from text fields and launches solving thread.
     */
    @FXML
    private void onStartResolutionClick() {
        resetSolution();

        try {
            this.start = Integer.parseInt(startField.getText());
            this.end = Integer.parseInt(endField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid start or end ID, using defaults.");
            start = 0;
            end = rows * cols - 1;
        }

        MethodName.SolveMethodName selectedSolveMethod = solutionMethodComboBox.getSelectionModel().getSelectedItem();
        System.out.println("Selected solving method: " + selectedSolveMethod);

        if (maze != null) {
            currentSolveMethod = selectedSolveMethod;
            new Thread(() -> solveMaze(selectedSolveMethod)).start();
        }
    }

    /**
     * Generates the maze with the given parameters.
     * Animates the maze build if step-by-step selected.
     *
     * @param generationMethod the generation method selected
     * @param seed             random seed for generation
     * @param rows             number of rows
     * @param cols             number of columns
     */
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

    /**
     * Runs the solving algorithm and animates the visited vertices and solution path.
     *
     * @param solveMethod the solving method selected
     */
    private void solveMaze(MethodName.SolveMethodName solveMethod) {
        Solver solver = new Solver(solveMethod);

        int[] antecedents = solver.solve(maze, maze.getVertexByID(start), maze.getVertexByID(end), MethodName.Type.COMPLETE);
        int[] orders = solver.solve(maze, maze.getVertexByID(start), maze.getVertexByID(end), MethodName.Type.STEPPER);

        markVisitedAndSolutionPath(orders, antecedents);
    }

    /**
     * Animates the maze exploration and solution path highlighting.
     *
     * @param orders      visitation order of vertices
     * @param antecedents solution path predecessor table
     */
    private void markVisitedAndSolutionPath(int[] orders, int[] antecedents) {
        for (int id : orders) {
            if (id == -1) break;

            Vertex v = maze.getVertexByID(id);
            v.setState(VertexState.VISITED);
            Platform.runLater(() -> displayMaze(maze));

            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException ignored) {
            }
        }

        ArrayList<Vertex> solutionVertices = Solver.pathVertex(maze, maze.getVertexByID(end), antecedents);
        for (Vertex v : solutionVertices) {
            v.setState(VertexState.SOLUTION);
            Platform.runLater(() -> displayMaze(maze));
            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Updates the maze reference and triggers redraw.
     *
     * @param maze the current maze object
     */
    public void displayMaze(Maze maze) {
        this.maze = maze;
        drawMazeWithWalls();
    }

    /**
     * Draws the maze grid and walls on the canvas.
     */
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

            g.setFill(getColorForVertex(v));
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

    /**
     * Returns the color associated with the vertex state and start/end.
     *
     * @param v vertex to color
     * @return the fill color
     */
    private Color getColorForVertex(Vertex v) {
        if (v.getID() == start) {
            return Color.RED;
        }
        if (v.getID() == end) {
            return Color.GREEN;
        }

        return switch (v.getState()) {
            case VISITED -> Color.rgb(169, 169, 169);
            case SOLUTION -> Color.rgb(173, 216, 230);
            default -> Color.WHITE;
        };
    }

    /**
     * Checks if the vertex has a visible edge (no wall) with the neighbor at (r, c).
     *
     * @param v vertex to check from
     * @param r row of neighbor
     * @param c column of neighbor
     * @return true if edge visible, false if wall present or out of bounds
     */
    private boolean hasNeighbor(Vertex v, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        Vertex neighbor = maze.getVertexByID(r * cols + c);
        return neighbor != null && visibleEdges.contains(new Edge(v, neighbor, true));
    }

    /**
     * Toggles wall between two adjacent vertices.
     *
     * @param v1 first vertex
     * @param v2 second vertex
     */
    private void toggleWallBetween(Vertex v1, Vertex v2) {
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

    /**
     * Resets all vertex states to default to prepare for new solution.
     */
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