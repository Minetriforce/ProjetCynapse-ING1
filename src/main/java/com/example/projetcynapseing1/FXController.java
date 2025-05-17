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
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.control.Alert;


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
    private boolean isEditingStartEnd = false;

    @FXML
    private CheckBox stepByStepCheckBox;
    @FXML
    private ToggleButton editEdgeButton;

    private boolean isEditingEdges = false;


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

    private Image startIcon;
    private Image endIcon;

    /**
     * Initializes UI bindings and event listeners.
     * Automatically called by the JavaFX framework after FXML loading.
     */
    @FXML
    private void initialize() {
        // Bind background image size to stackpane size
        backgroundImage.fitWidthProperty().bind(stackpane.widthProperty());
        backgroundImage.fitHeightProperty().bind(stackpane.heightProperty());

        // Fill combo boxes with enum values
        generationMethodComboBox.getItems().setAll(MethodName.GenMethodName.values());
        generationMethodComboBox.getSelectionModel().selectFirst();

        solutionMethodComboBox.getItems().setAll(MethodName.SolveMethodName.values());
        solutionMethodComboBox.getSelectionModel().selectFirst();

        // Disable start/end fields initially
        startField.setDisable(true);
        endField.setDisable(true);

        // Toggle button action: enable/disable editing of start/end
        changeStartEndButton.setOnAction(e -> {
            isEditingStartEnd = !isEditingStartEnd;
            if (isEditingStartEnd) {
                changeStartEndButton.setText("Validate Change");
                startField.setDisable(false);
                endField.setDisable(false);
            } else {
                changeStartEndButton.setText("Change Start/End");
                startField.setDisable(true);
                endField.setDisable(true);
            }
            displayMaze(maze);
        });

        // Checkbox to enable/disable timestep input
        stepByStepCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            timeStepField.setDisable(!isNowSelected);
            if (!isNowSelected) {
                timeStepField.setText("");
                timeStep = 0;
            }
        });
        timeStepField.setDisable(true);

        // Mouse click on maze canvas: toggle walls or edit start/end points
        mazeCanvas.setOnMouseClicked(event -> {
            int col = (int) (event.getX() / blockSize);
            int row = (int) (event.getY() / blockSize);

            if (col < 0 || col >= cols || row < 0 || row >= rows) return;

            Vertex clickedVertex = maze.getVertexByID(row * cols + col);
            if (clickedVertex == null) return;

            if (isEditingStartEnd) {
                // Alternately select start or end vertex
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
                    changeStartEndButton.setSelected(false);
                    isEditingStartEnd = false;
                    changeStartEndButton.setText("Change Start/End");
                    startField.setDisable(true);
                    endField.setDisable(true);
                }
                displayMaze(maze);
            } else {
                // Toggle wall between two selected vertices
                if (firstSelectedVertex == null) {
                    firstSelectedVertex = clickedVertex;
                    System.out.println("First cell selected: " + firstSelectedVertex.getID());
                } else {
                    toggleWallBetween(firstSelectedVertex, clickedVertex);
                    firstSelectedVertex = null;
                }
            }
        });

        // Redraw maze when toggle button selection changes
        changeStartEndButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (maze != null) {
                displayMaze(maze);
            }
        });

        // Update start and end fields and redraw when text changes
        startField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                int val = Integer.parseInt(newText);
                if (val >= 0 && val < rows * cols) {
                    start = val;
                    displayMaze(maze);
                }
            } catch (NumberFormatException ignored) {
            }
        });

        endField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                int val = Integer.parseInt(newText);
                if (val >= 0 && val < rows * cols) {
                    end = val;
                    displayMaze(maze);
                }
            } catch (NumberFormatException ignored) {
            }
        });

        startIcon = new Image(getClass().getResourceAsStream("/images/start.png"));
        endIcon = new Image(getClass().getResourceAsStream("/images/end.png"));



        editEdgeButton.setOnAction(e -> {
            isEditingEdges = editEdgeButton.isSelected();
            if (isEditingEdges) {
                editEdgeButton.setText("Confirm Changes");
                firstSelectedVertex = null; // reset selection
                showAlert("Edit Mode Enabled",
                        "Select two cells such that an edge appears or disappears between them.");
            } else {
                editEdgeButton.setText("Add or Remove Edge");
                firstSelectedVertex = null; // reset selection when done
            }
        });

        mazeCanvas.setOnMouseClicked(event -> {
            if (!isEditingEdges) return; // do nothing if not in edge edit mode

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
                firstSelectedVertex = null;
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
                : (rows > 40 || cols > 40) ? 15
                : (rows > 30 || cols > 30) ? 20
                : (rows > 20 || cols > 20) ? 30
                : (rows > 15 || cols > 15) ? 40
                : (rows > 10 || cols > 10) ? 70
                : (rows > 5 || cols > 5) ? 80
                : 100;
    }

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
            showAlert("Error", "Please enter valids integers for rows,cols and seed to generate a maze! You can choose the generation and solving methods. For the generation, you can also enable step-by-step mode and specify the time step you want.");
        }
    }

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

        int[] antecedents = solver.solve(maze, maze.getVertexByID(start), maze.getVertexByID(end), MethodName.Type.COMPLETE);
        int[] orders = solver.solve(maze, maze.getVertexByID(start), maze.getVertexByID(end), MethodName.Type.STEPPER);

        markVisitedAndSolutionPath(orders, antecedents);
    }

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

            if (v.getID() == start) {
                g.drawImage(startIcon, x, y, blockSize, blockSize);}
            if (v.getID() == end) {
                g.drawImage(endIcon, x, y, blockSize, blockSize);}

            if (isEditingStartEnd) {
                String idStr = String.valueOf(id);
                Text text = new Text(idStr);
                text.setFont(g.getFont());
                double textWidth = text.getLayoutBounds().getWidth();
                double textHeight = text.getLayoutBounds().getHeight();

                double textX = x + (blockSize - textWidth) / 2;
                double textY = y + (blockSize + textHeight) / 2;

                g.setFill(Color.BLACK);
                g.fillText(idStr, textX, textY);
            }
        }
    }

    private Color getColorForVertex(Vertex v) {
        return switch (v.getState()) {
            case VISITED -> Color.rgb(169, 169, 169);
            case SOLUTION -> Color.rgb(173, 216, 230);
            default -> Color.WHITE;
        };
    }


    private boolean hasNeighbor(Vertex v, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        Vertex neighbor = maze.getVertexByID(r * cols + c);
        return neighbor != null && visibleEdges.contains(new Edge(v, neighbor, true));
    }

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


    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

}
