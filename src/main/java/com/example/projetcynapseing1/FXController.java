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
 * This class manages the JavaFX interface, with maze canvas, combo boxes for
 * selecting methods,
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
    private ComboBox<String> backgroundSelector;

    @FXML
    private void onBackgroundSelectionChanged() {
        String selectedImage = backgroundSelector.getValue();
        setBackgroundImage(selectedImage);
    }

    private void setBackgroundImage(String selectedName) {
        String fileName;
        switch (selectedName) {
            case "labyrinth":
                fileName = "images/logo.png";
                break;
            case "sakura":
                fileName = "images/sakura.jpg";
                break;
            case "beach":
                fileName = "images/beach.jpg";
                break;
            case "shootingstar":
                fileName = "images/shootingstar.jpg";
                break;
            default:
                fileName = "images/logo.png";
        }
        Image image = new Image(getClass().getResourceAsStream("/" + fileName));
        backgroundImage.setImage(image);
    }

    @FXML
    private Button resolutionLabyrinth;
    @FXML
    private Button generationLabyrinth;
    @FXML
    private Canvas mazeCanvas;
    @FXML
    private Button loadMaze;
    @FXML
    private Button saveMaze;
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

    private int blockSize = 20; // default initial block size

    private Set<Edge> visibleEdges = new HashSet<>();

    private Vertex firstSelectedVertex = null; // First clicked vertex for toggling walls

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

        backgroundSelector.getItems().addAll("labyrinth", "sakura", "beach", "shootingstar");
        backgroundSelector.setValue("labyrinth");

        // Fill combo boxes with enum values
        generationMethodComboBox.getItems().setAll(MethodName.GenMethodName.values());
        generationMethodComboBox.setPromptText("Choose a generation method");

        solutionMethodComboBox.getItems().setAll(MethodName.SolveMethodName.values());
        solutionMethodComboBox.setPromptText("Choose a solving method");

        resolutionLabyrinth.setDisable(true); // resolution disable at first until we click generation button.

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

                // avoid edge edition to avoid conflicts
                if (isEditingEdges) {
                    isEditingEdges = false;
                    editEdgeButton.setSelected(false);
                    editEdgeButton.setText("Add or Remove Edge");
                }
                editEdgeButton.setDisable(true); // disable other mode button

            } else {
                changeStartEndButton.setText("Change Start/End");
                startField.setDisable(true);
                endField.setDisable(true);

                editEdgeButton.setDisable(false); // re-enable it
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
                firstSelectedVertex = null;

                // disable start/end editing to avoid conflicts
                if (isEditingStartEnd) {
                    isEditingStartEnd = false;
                    changeStartEndButton.setSelected(false);
                    changeStartEndButton.setText("Change Start/End");
                    startField.setDisable(true);
                    endField.setDisable(true);
                }
                changeStartEndButton.setDisable(true); // disable other mode button

                showAlert("Edit Mode Enabled",
                        "Select two cells such that an edge appears or disappears between them.");
            } else {
                editEdgeButton.setText("Add or Remove Edge");
                firstSelectedVertex = null;

                changeStartEndButton.setDisable(false); // re-enable other button
            }
        });

        mazeCanvas.setOnMouseClicked(event -> {
            int col = (int) (event.getX() / blockSize);
            int row = (int) (event.getY() / blockSize);

            if (col < 0 || col >= cols || row < 0 || row >= rows)
                return;

            Vertex clickedVertex = maze.getVertexByID(row * cols + col);
            if (clickedVertex == null)
                return;

            if (isEditingStartEnd) {
                // Alterner entre start et end
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
                    editEdgeButton.setDisable(false);
                }
                displayMaze(maze);
            } else if (isEditingEdges) {
                // Modifier les murs
                resetSolution();
                if (firstSelectedVertex == null) {
                    firstSelectedVertex = clickedVertex;
                    markSelectedVertices(firstSelectedVertex, false);
                    System.out.println("First cell selected: " + firstSelectedVertex.getID());
                } else {
                    markSelectedVertices(firstSelectedVertex, true);
                    toggleWallBetween(firstSelectedVertex, clickedVertex);
                    firstSelectedVertex = null;
                }
            }
        });
    }

    /**
     * When a Vertex is clicked for a modification, this function mark this vertex
     * and the orthers valid vertices around in red if it means to delete a wall,
     * and green to add one
     * 
     * @param v     first selected vertex
     * @param clear clear the colors
     */
    private void markSelectedVertices(Vertex v, Boolean clear) {
        ArrayList<Vertex> temp = new ArrayList<Vertex>();
        if (v.getX() != 0) {
            temp.add(maze.getVertexByID(v.getID() - 1));
        }
        if (v.getY() != 0) {
            temp.add(maze.getVertexByID(v.getID() - this.cols));

        }
        if (v.getX() != this.cols - 1) {
            temp.add(maze.getVertexByID(v.getID() + 1));

        }
        if (v.getY() != this.rows - 1) {
            temp.add(maze.getVertexByID(v.getID() + this.cols));
        }
        if (!clear) {
            v.setState(VertexState.FIRSTSELECTED);
            for (Vertex vTemp : temp) {
                if (v.getNeighbors().contains(vTemp)) {
                    vTemp.setState(VertexState.SELECTEDADD);
                } else {
                    vTemp.setState(VertexState.SELECTEDDEL);
                }
            }
        } else {
            v.setState(VertexState.DEFAULT);
            for (Vertex vTemp : temp) {
                vTemp.setState(VertexState.DEFAULT);
            }
        }
        displayMaze(maze);
    }

    /**
     * Setter for the maze controller Instance, used to generate, solve, load and
     * save a maze
     * 
     * @param mazeController Instance of mazeController
     */
    public void setMazeController(MazeController mazeController) {
        this.mazeController = mazeController;
    }

    /**
     * Resize maze canvas according to number of rows and columns
     * 
     * @param rows integer of rows of maze
     * @param cols integer of columns of maze
     */
    private void setMazeSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        blockSize = (int) Math.max(5, 700 / this.rows);
    }

    /**
     * function to handle click on generation button.
     * Start the generation (and animation) of a maze.
     */
    @FXML
    private void onStartGenerationClick() {
        try {
            if (stepByStepCheckBox.isSelected() && !timeStepField.getText().isEmpty()) {
                this.timeStep = Integer.parseInt(timeStepField.getText());
            }

            labyrinthIsGenerated = false;
            resolutionLabyrinth.setDisable(true);
            saveMaze.setDisable(true);
            editEdgeButton.setDisable(true);

            this.rows = Integer.parseInt(rowsField.getText());
            this.cols = Integer.parseInt(colsField.getText());
            this.seed = Integer.parseInt(seedField.getText());
            this.timeStep = Math.max(0, this.timeStep);
            this.end = rows * cols - 1;

            setMazeSize(rows, cols);

            MethodName.GenMethodName selectedGenMethod = generationMethodComboBox.getSelectionModel().getSelectedItem();
            System.out.println("timeStep = " + this.timeStep);
            System.out.println("Selected generation method: " + selectedGenMethod);

            new Thread(() -> generateMaze(selectedGenMethod, seed, rows, cols)).start();
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid integers for all input fields.");
            showAlert("Error",
                    "Please enter valids integers for rows,cols and seed to generate a maze! You can choose the generation and solving methods. For the generation, you can also enable step-by-step mode and specify the time step you want.");
        }
    }

    /**
     * function to handle click on resolution button.
     * Start the resolution of the current maze and it's animation
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

        if (maze != null && selectedSolveMethod != null) {
            new Thread(() -> solveMaze(selectedSolveMethod)).start();
        }
    }

    /**
     * Generate a maze according to parameters and start animation of the maze
     * 
     * @param generationMethod method used to generate maze
     * @param seed             used in the random number generator
     * @param rows             number of rows
     * @param cols             number of columns
     */
    private void generateMaze(MethodName.GenMethodName generationMethod, int seed, int rows, int cols) {
        try {
            mazeController.createMaze(generationMethod, rows, cols, seed);
            Maze generatedMaze = mazeController.getCurrentMaze();
            visibleEdges.clear();

            for (Edge e : generatedMaze.getEdges()) {
                visibleEdges.add(e);
                Platform.runLater(() -> displayMaze(generatedMaze));
                Thread.sleep(timeStep);
            }

            labyrinthIsGenerated = true;
            resolutionLabyrinth.setDisable(false);
            saveMaze.setDisable(false);
            editEdgeButton.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Solve maze according to given method (only if a maze is instantiated)
     * 
     * @param solveMethod method to solve maze
     */
    private void solveMaze(MethodName.SolveMethodName solveMethod) {
        try {
            mazeController.findSolution(solveMethod, maze.getVertexByID(start), maze.getVertexByID(end));
            editEdgeButton.setDisable(true);
            generationLabyrinth.setDisable(true);
            resolutionLabyrinth.setDisable(true);

            System.out.println(timeStep);
            markVisitedAndSolutionPath(mazeController.getSolution(), mazeController.getVisited());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * add color to vertices visitied during resolving maze
     * 
     * @param orders      color in blue the path between start and end
     * @param antecedents color in grey all the other vertices visited
     */
    private void markVisitedAndSolutionPath(int[] orders, int[] antecedents) {
        try {
            for (int id : orders) {
                if (id == -1)
                    break;

                Vertex v = maze.getVertexByID(id);
                v.setState(VertexState.VISITED);

                Platform.runLater(() -> displayMaze(maze));
                Thread.sleep(timeStep);
            }

            ArrayList<Vertex> solutionVertices = Solver.pathVertex(maze, maze.getVertexByID(end), antecedents);
            for (Vertex v : solutionVertices) {
                v.setState(VertexState.SOLUTION);
                Platform.runLater(() -> displayMaze(maze));
                Thread.sleep(timeStep);
            }
        } catch (InterruptedException ignored) {
        }
        editEdgeButton.setDisable(false);
        generationLabyrinth.setDisable(false);
        resolutionLabyrinth.setDisable(false);
    }

    /**
     * Lauch display maze function
     * 
     * @param maze a maze
     */
    private void displayMaze(Maze maze) {
        this.maze = maze;
        drawMazeWithWalls();
    }

    /**
     * Draw the maze on the maze canvas
     */
    private void drawMazeWithWalls() {
        if (mazeCanvas == null) {
            System.out.println("Canvas is not initialized!");
            return;
        }
        if (rows <= 0 || cols <= 0) {
            System.out.println("Invalid maze dimensions: rows = " + rows + ", cols = " + cols);
            showAlert("MAZE LOADING ERROR", "Invalid file has been provided !\nCheck your file and try again");
            return;
        }

        GraphicsContext g = mazeCanvas.getGraphicsContext2D();
        g.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());
        mazeCanvas.setWidth(cols * blockSize);
        mazeCanvas.setHeight(rows * blockSize);
        g.setLineWidth(2);

        for (Vertex v : maze.getVertices()) {
            int id = v.getID();

            int x = v.getX() * blockSize;
            int y = v.getY() * blockSize;

            g.setFill(getColorForVertex(v));
            g.fillRect(x, y, blockSize, blockSize);

            if (v.getY() != 0) {
                if (!visibleEdges.contains(new Edge(v, maze.getVertexByID(v.getID() - this.cols), true))) { // top
                    g.strokeLine(x, y, x + blockSize, y);
                }
            }
            if (v.getX() != this.cols - 1) {
                if (!visibleEdges.contains(new Edge(v, maze.getVertexByID(v.getID() + 1), true))) { // right
                    g.strokeLine(x + blockSize, y, x + blockSize, y + blockSize);
                }
            }
            if (v.getY() != this.rows - 1) {
                if (!visibleEdges.contains(new Edge(v, maze.getVertexByID(v.getID() + this.cols), true))) { // bottom
                    g.strokeLine(x, y + blockSize, x + blockSize, y + blockSize);
                }
            }
            if (v.getX() != 0) {
                if (!visibleEdges.contains(new Edge(v, maze.getVertexByID(v.getID() - 1), true))) { // left
                    g.strokeLine(x, y, x, y + blockSize);
                }
            }

            if (v.getID() == start) {
                g.drawImage(startIcon, x, y, blockSize, blockSize);
            }
            if (v.getID() == end) {
                g.drawImage(endIcon, x, y, blockSize, blockSize);
            }

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

    /**
     * Return the color of the box according to vertex state
     * 
     * @param v a vertex in the maze
     * @return a color for the box
     */
    private Color getColorForVertex(Vertex v) {
        return switch (v.getState()) {
            case VISITED -> Color.rgb(169, 169, 169);
            case SOLUTION -> Color.rgb(173, 216, 230);
            case SELECTEDADD -> Color.rgb(100, 255, 0);
            case SELECTEDDEL -> Color.rgb(255, 0, 0);
            case FIRSTSELECTED -> Color.rgb(200, 255, 200);
            default -> Color.WHITE;
        };
    }

    /**
     * add or remove a wall between two adjacent vertices
     * 
     * @param v1 First vertex
     * @param v2 Second vertex
     */
    private void toggleWallBetween(Vertex v1, Vertex v2) {
        if (Math.abs(v1.getY() - v2.getY()) + Math.abs(v1.getX() - v2.getX()) != 1) {
            System.out.println("Vertices are not adjacent. Can't toggle wall.");
            return;
        }

        Edge edge = new Edge(v1, v2);

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
     * Clean the maze of displayed solution
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

    /**
     * Open an alert pop-up
     * 
     * @param title   title of the pop-up
     * @param content content of the pop-up
     */
    public static void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Function to handle click on load button.
     * This function also bypass all the requirements to draw directly a maze
     * without doing an animation
     */
    @FXML
    private void onLoadClick() {
        if (mazeController.loadMaze() == true) {
            /* Bypass all generation requirements and changing */
            this.maze = mazeController.getCurrentMaze();
            this.visibleEdges.clear();
            this.visibleEdges.addAll(this.maze.getEdges());
            this.start = 0;
            this.rows = maze.getRows();
            this.cols = maze.getColumns();
            setMazeSize(this.rows, this.cols);
            this.end = maze.getVertices().getLast().getID();
            labyrinthIsGenerated = true;
            resolutionLabyrinth.setDisable(false);

            this.resetSolution();
            displayMaze(this.maze);
            saveMaze.setDisable(false);
        } else {
            showAlert("LOADING MAZE FAIL", "Loaded maze seems to be empty / corrupted, try to load another file.");
        }
    }

    /**
     * Function to handle click on save Button.
     * this function opens an environement saving pop up.
     */
    @FXML
    private void onSaveClick() {
        if (labyrinthIsGenerated) {
            mazeController.saveMaze();
        }
    }

}