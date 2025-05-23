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
import javafx.scene.image.Image;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.io.File;
import java.nio.BufferOverflowException;

import javafx.stage.Stage;
import javafx.stage.FileChooser;

/**
 * This class manages the JavaFX interface, with maze canvas, combo boxes for
 * selecting methods,
 * user-defined parameters, and displays maze changes dynamically.
 *
 * @author Florianne, Lorelle
 */
public class FXController {

    @FXML
    private StackPane rightPane;

    @FXML
    private StackPane leftPane;

    private Color[][] colors = {
            { Color.rgb(173, 216, 230), Color.rgb(0, 0, 0), Color.rgb(169, 169, 169) },
            { Color.rgb(255, 185, 211), Color.rgb(200, 113, 151), Color.rgb(169, 169, 169) },
            { Color.rgb(94, 151, 202), Color.rgb(132, 152, 169), Color.rgb(187, 228, 241) },
            { Color.rgb(249, 213, 182), Color.rgb(25, 114, 210), Color.rgb(163, 173, 204) } };
    private int theme = 0;

    @FXML
    private ImageView backgroundImage;
    @FXML
    private StackPane stackPane;

    @FXML
    private ComboBox<String> backgroundSelector;

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
    private CheckBox stepByStepCheckBoxGeneration;

    @FXML
    private TextField timeStepFieldGeneration;

    @FXML
    private CheckBox stepByStepCheckBoxSolution;

    @FXML
    private TextField timeStepFieldSolution;


    @FXML
    private ToggleButton changeStartEndButton;
    private boolean selectingStart = true;
    private boolean isEditingStartEnd = false;


    @FXML
    private ToggleButton editEdgeButton;
    @FXML
    private VBox historyVBox;

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
     * Called when the background selection changes. Loads either a predefined
     * background or opens file chooser for a custom image.
     */
    @FXML
    private void onBackgroundSelectionChanged() {
        String selectedImage = backgroundSelector.getValue();
        if ("choose from your file".equals(selectedImage)) {
            onCustomBackgroundSelected();
        } else {
            setBackgroundImage(selectedImage);
        }
    }

    /**
     * Opens a file chooser dialog to select a custom background image and sets it.
     */
    private void onCustomBackgroundSelected() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select your own background image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"));

        Stage stage = (Stage) stackPane.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            Platform.runLater(() -> backgroundImage.setImage(image));
        }

    }

    /**
     * Sets the background image according to the selected name.
     *
     * @param selectedName name of the background theme
     */
    private void setBackgroundImage(String selectedName) {
        String fileName;
        switch (selectedName) {
            case "labyrinth":
                fileName = "images/logo.png";
                theme = 0;
                break;
            case "sakura":
                fileName = "images/sakura.jpg";
                theme = 1;
                break;
            case "beach":
                fileName = "images/beach.jpg";
                theme = 2;
                break;
            case "shootingstar":
                fileName = "images/shootingstar.jpg";
                theme = 3;
                break;
            default:
                fileName = "images/logo.png";
                theme = 0;
        }
        Image image = new Image(getClass().getResourceAsStream("/" + fileName));
        Platform.runLater(() -> {
            backgroundImage.setImage(image);
            this.timeStep = 0;
            displayMaze(maze);
        });
    }

    /**
     * Initializes UI bindings and event listeners.
     * Automatically called by the JavaFX framework after FXML loading.
     */
    @FXML
    private void initialize() {

        // Bind background image size to stackpane size
        backgroundImage.fitWidthProperty().bind(stackPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(stackPane.heightProperty());

        backgroundSelector.getItems().addAll("labyrinth", "sakura", "beach", "shootingstar", "choose from your file");
        backgroundSelector.setValue("labyrinth");

        // Fill combo boxes with enum values
        generationMethodComboBox.getItems().setAll(MethodName.GenMethodName.values());
        generationMethodComboBox.setPromptText("Choose a generation method");

        solutionMethodComboBox.getItems().setAll(MethodName.SolveMethodName.values());
        solutionMethodComboBox.setPromptText("Choose a solving method");

        setButtonsState(true, false, false, false, false, true);

        // Toggle button action: enable/disable editing of start/end
        changeStartEndButton.setOnAction(e -> {
            isEditingStartEnd = !isEditingStartEnd;

            if (isEditingStartEnd) {
                changeStartEndButton.setText("Validate Change");

                // avoid edge edition to avoid conflicts
                if (isEditingEdges) {
                    isEditingEdges = false;
                    editEdgeButton.setSelected(false);
                    editEdgeButton.setText("Add or Remove Edge");
                }
                editEdgeButton.setDisable(true); // disable other mode button

            } else {
                changeStartEndButton.setText("Change Start/End");

                editEdgeButton.setDisable(false); // re-enable it
            }
        });

        // Step by step for generation
        stepByStepCheckBoxGeneration.selectedProperty().addListener((obs, oldVal, newVal) -> {
            timeStepFieldGeneration.setDisable(!newVal);
            if (!newVal) {
                timeStepFieldGeneration.setText("");
                timeStep = 0;
            }
        });
        timeStepFieldGeneration.setDisable(true);

      // Step by step for solution
        stepByStepCheckBoxSolution.selectedProperty().addListener((obs, oldVal, newVal) -> {
            timeStepFieldSolution.setDisable(!newVal);
            if (!newVal) {
                timeStepFieldSolution.setText("");
                timeStep = 0;
            }
        });
        timeStepFieldSolution.setDisable(true);


        // Redraw maze when toggle button selection changes
        changeStartEndButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (maze != null) {
                Set<Vertex> temp = new HashSet<Vertex>();
                temp.addAll(maze.getVertices());
                Platform.runLater(() -> drawVertexWithWalls(temp));
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
                Set<Vertex> temp = new HashSet<Vertex>();
                if (selectingStart) {
                    temp.add(maze.getVertexByID(this.start));
                    start = clickedVertex.getID();
                    selectingStart = false;
                } else {
                    end = clickedVertex.getID();
                    selectingStart = true;
                    changeStartEndButton.setSelected(false);
                    isEditingStartEnd = false;
                    changeStartEndButton.setText("Change Start/End");
                    editEdgeButton.setDisable(false);
                }
                temp.add(maze.getVertexByID(this.start));
                temp.add(maze.getVertexByID(this.end));
                temp.add(clickedVertex);
                Platform.runLater(() -> drawVertexWithWalls(temp));

            } else if (isEditingEdges) {
                resetSolution();
                if (firstSelectedVertex == null) {
                    firstSelectedVertex = clickedVertex;
                    markSelectedVertices(firstSelectedVertex, false);
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
        Set<Vertex> temp = new HashSet<Vertex>();
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
        temp.add(v);
        Platform.runLater(() -> drawVertexWithWalls(temp));
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

        blockSize = (int) Math.min(stackPane.getHeight() / this.rows, (stackPane.getWidth() - 700) / this.cols);
        blockSize = Math.max(5, blockSize);

        mazeCanvas.setWidth(cols * blockSize);
        mazeCanvas.setHeight(rows * blockSize);
    }

    /**
     * function to handle click on generation button.
     * Start the generation (and animation) of a maze.
     */
    @FXML
    private void onStartGenerationClick() {
        try {
            if (stepByStepCheckBoxGeneration.isSelected() && !timeStepFieldGeneration.getText().isEmpty()) {
                this.timeStep = Integer.parseInt(timeStepFieldGeneration.getText());
            }
            else {
                this.timeStep = 0;
            }

            labyrinthIsGenerated = false;
            if (rowsField.getText().equals("") || colsField.getText().equals("") || seedField.getText().equals("")) {
                throw new Exception(
                        "You must fill the following fields :"
                                + (rowsField.getText().equals("") ? "Rows," : "")
                                + (colsField.getText().equals("") ? "Columns," : "")
                                + (seedField.getText().equals("") ? "Seed" : ""));
            }

            this.rows = Integer.parseInt(rowsField.getText());
            this.cols = Integer.parseInt(colsField.getText());
            this.seed = Integer.parseInt(seedField.getText());
            this.timeStep = Math.max(0, this.timeStep);
            MethodName.GenMethodName selectedGenMethod = generationMethodComboBox.getSelectionModel().getSelectedItem();

            if (this.rows <= 0 || this.cols <= 0 || this.seed < 0) {
                this.rows = (this.rows <= 0) ? 1 : this.rows;
                this.cols = (this.cols <= 0) ? 1 : this.cols;
                this.seed = (this.seed < 0) ? 0 : this.seed;
                rowsField.setText("" + this.rows);
                colsField.setText("" + this.cols);
                seedField.setText("" + this.seed);
                throw new Exception("Please enter valids integers for rows, cols and seed to generate a maze!");
            }

            if (this.rows > 100 || this.cols > 100) {
                this.rows = (this.rows > 100) ? 100 : this.rows;
                this.cols = (this.cols > 100) ? 100 : this.cols;
                rowsField.setText("" + this.rows);
                colsField.setText("" + this.cols);
                throw new Exception("Maximum allowed size is 100x100 for rows and columns.");
            }


            if (selectedGenMethod == null) {
                throw new Exception("You must select a generation method.");
            }

            this.rows = (this.rows > stackPane.getHeight() / 5) ? (int) stackPane.getHeight() / 5 : this.rows;
            this.cols = (this.cols > (stackPane.getWidth() - 700) / 5) ? (int) (stackPane.getWidth() - 700) / 5
                    : this.cols;

            setButtonsState(false, false, false, false, false, false);
            new Thread(() -> generateMaze(selectedGenMethod, seed, rows, cols)).start();

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    /**
     * function to handle click on resolution button.
     * Start the resolution of the current maze and it's animation
     */
    @FXML
    private void onStartResolutionClick() {
        resetSolution();

        MethodName.SolveMethodName selectedSolveMethod = solutionMethodComboBox.getSelectionModel().getSelectedItem();

        try{
            if (maze != null && selectedSolveMethod != null) {
                if (stepByStepCheckBoxSolution.isSelected() && !timeStepFieldSolution.getText().isEmpty()) {
                    this.timeStep = Integer.parseInt(timeStepFieldSolution.getText());
                } else {
                    this.timeStep = 0;
                }

                setButtonsState(false, false, false, false, false, false);
                this.timeStep = Math.max(0, this.timeStep);
                new Thread(() -> solveMaze(selectedSolveMethod)).start();
            }


            if (selectedSolveMethod == null) {
                throw new Exception("You must select a solve method.");
            }
        }
        catch (Exception e) {
            showAlert("Error", e.getMessage());
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

            displayMaze(generatedMaze);

            for (Edge e : generatedMaze.getEdges()) {
                visibleEdges.add(e);
                Set<Vertex> temp = new HashSet<>();
                temp.add(e.getVertexA());
                temp.add(e.getVertexB());
                Platform.runLater(() -> drawVertexWithWalls(temp));
                try {
                    Thread.sleep(timeStep);
                } catch (InterruptedException ignored) {
                }
            }

            labyrinthIsGenerated = true;
            setButtonsState(true, true, true, true, true, true);
        } catch (BufferOverflowException ex) {
            ex.printStackTrace();
            showAlert("ERROR DISPLAYING", ex.getMessage());
            this.maze = null;
            this.rows = 0;
            this.cols = 0;
            setButtonsState(true, false, false, false, false, true);

        }
    }

    /**
     * Solve maze according to given method (only if a maze is instantiated)
     *
     * @param solveMethod method to solve maze
     */
    private void solveMaze(MethodName.SolveMethodName solveMethod) {
        try {
            if (solveMethod == null) {
                throw new Exception("You must select a resolution method to solve the maze");
            }

            long startTime = System.currentTimeMillis();
            mazeController.findSolution(solveMethod, maze.getVertexByID(start), maze.getVertexByID(end));
            long endTime = System.currentTimeMillis();

            long timeMs = endTime - startTime;

            markVisitedAndSolutionPath(mazeController.getVisited(), mazeController.getSolution(), timeMs,
                    solveMethod.name());

        } catch (Exception e) {
            showAlert("Error solving maze", e.getMessage());
        }
    }

    /**
     * Add color to vertices visited during maze solving animation.
     *
     * @param orders          array of visited vertex IDs in visitation order
     * @param antecedents     array encoding the solution path
     * @param timeMs          time taken to solve in milliseconds
     * @param solveMethodName name of the solving method used
     */
    private void markVisitedAndSolutionPath(int[] orders, int[] antecedents, long timeMs, String solveMethodName) {
        int pathLength = 0;
        int visitedCount = 0;

        try {
            for (int id : orders) {
                if (id == -1)
                    break;
                Vertex v = maze.getVertexByID(id);
                v.setState(VertexState.VISITED);
                visitedCount++;

                Set<Vertex> temp = new HashSet<>();
                temp.add(v);

                Platform.runLater(() -> drawVertexWithWalls(temp));
                Thread.sleep(timeStep);
            }

            ArrayList<Vertex> solutionVertices = Solver.pathVertex(maze, maze.getVertexByID(end), antecedents);
            for (Vertex v : solutionVertices) {
                v.setState(VertexState.SOLUTION);
                pathLength++;

                Set<Vertex> temp = new HashSet<>();
                temp.add(v);

                Platform.runLater(() -> drawVertexWithWalls(temp));
                Thread.sleep(timeStep);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (end == start){
            maze.getVertexByID(start).setState(VertexState.SOLUTION);
            visitedCount=1;
            pathLength=1;
        }
        addResolutionStatsToHistory(pathLength, visitedCount, timeMs, solveMethodName);
        setButtonsState(true, true, true, true, true, true);
    }

    /**
     * Function to display an entire maze.
     *
     * @param maze the maze to display
     */
    private void displayMaze(Maze maze) {
        this.maze = maze;
        Set<Vertex> temp = new HashSet<Vertex>();
        temp.addAll(maze.getVertices());

        this.rows = maze.getRows();
        this.cols = maze.getColumns();
        setMazeSize(this.rows, this.cols);

        Platform.runLater(() -> {
            GraphicsContext g = mazeCanvas.getGraphicsContext2D();
            g.clearRect(0, 0, mazeCanvas.getWidth(), mazeCanvas.getHeight());

            this.start = 0;
            this.end = maze.getVertices().getLast().getID();

            drawVertexWithWalls(temp);
        });
    }

    /**
     * Draw or update a specific set of vertices on the maze Canvas.
     *
     * @param vList set of unique vertices to draw
     */
    private void drawVertexWithWalls(Set<Vertex> vList) {
        if (mazeCanvas == null) {
            return;
        }

        GraphicsContext g = mazeCanvas.getGraphicsContext2D();
        g.setLineWidth(2);

        for (Vertex v : vList) {
            int x = v.getX() * blockSize;
            int y = v.getY() * blockSize;

            g.setFill(getColorForVertex(v));
            g.fillRect(x, y, blockSize, blockSize);
            g.setStroke(colors[theme][1]); // Set the color of the line

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
        }
    }

    /**
     * Returns the color of the box according to vertex state.
     *
     * @param v a vertex in the maze
     * @return a color for the box
     */
    private Color getColorForVertex(Vertex v) {
        return switch (v.getState()) {
            case VISITED -> colors[theme][2];
            case SOLUTION -> colors[theme][0];
            case SELECTEDADD -> Color.rgb(100, 255, 0);
            case SELECTEDDEL -> Color.rgb(255, 0, 0);
            case FIRSTSELECTED -> Color.rgb(200, 255, 200);
            default -> Color.WHITE;
        };
    }

    /**
     * Add or remove a wall between two adjacent vertices.
     *
     * @param v1 First vertex
     * @param v2 Second vertex
     */
    private void toggleWallBetween(Vertex v1, Vertex v2) {
        if (Math.abs(v1.getY() - v2.getY()) + Math.abs(v1.getX() - v2.getX()) != 1) {
            return;
        }

        if (maze.getEdgeByVertices(v1, v2) != null) {
            visibleEdges.remove(maze.getEdgeByVertices(v1, v2));
            maze.removeEdge(maze.getEdgeByVertices(v1, v2));
        } else {
            Edge edge = new Edge(v1, v2);
            visibleEdges.add(edge);
            maze.addEdge(edge);
        }

        Set<Vertex> temp = new HashSet<Vertex>();
        temp.add(v1);
        temp.add(v2);

        Platform.runLater(() -> drawVertexWithWalls(temp));
    }

    /**
     * Clean the maze of displayed solution.
     */
    private void resetSolution() {
        Set<Vertex> temp = new HashSet<Vertex>();

        if (maze == null) {
            return;
        }
        for (Vertex v : maze.getVertices()) {
            if (v.getState() != VertexState.DEFAULT) {
                v.setState(VertexState.DEFAULT);
                temp.add(v);
            }
        }
        Platform.runLater(() -> drawVertexWithWalls(temp));
    }

    /**
     * Open an alert pop-up.
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
     * This function bypasses all generation requirements and draws directly a maze
     * without animation.
     */
    @FXML
    private void onLoadClick() {
        if (mazeController.loadMaze()) {
            this.maze = mazeController.getCurrentMaze();
            this.visibleEdges.clear();
            this.visibleEdges.addAll(this.maze.getEdges());
            labyrinthIsGenerated = true;

            Platform.runLater(() -> {
                this.resetSolution();
                displayMaze(this.maze);
                setButtonsState(true, true, true, true, true, true);
            });

        } else {
            showAlert("LOADING MAZE FAIL", "Loaded maze seems to be empty / corrupted, try to load another file.");
        }
    }

    /**
     * Function to handle click on save Button.
     * This function opens an environment saving pop up.
     */
    @FXML
    private void onSaveClick() {
        if (maze != null) {
            mazeController.saveMaze();
        }
    }

    /**
     * Enable or disable all user buttons, according to parameters.
     * False : button disabled
     * True : button enabled
     *
     * @param generation generation button
     * @param solve      solving button
     * @param startEnd   change start-end toggle button
     * @param modify     modify edges toggle button
     * @param save       save maze button
     * @param load       load maze button
     */
    private void setButtonsState(Boolean generation, Boolean solve, Boolean startEnd, Boolean modify, Boolean save,
                                 Boolean load) {
        generationLabyrinth.setDisable(!generation);
        resolutionLabyrinth.setDisable(!solve);
        changeStartEndButton.setDisable(!startEnd);
        editEdgeButton.setDisable(!modify);
        saveMaze.setDisable(!save);
        loadMaze.setDisable(!load);
    }

    /**
     * Adds a summary of the maze resolution statistics to the history panel.
     * Displays the solving method name, path length, number of visited vertices,
     * and time taken for the solution.
     *
     * @param pathLength      the length of the found path from start to end
     * @param visitedCount    the total number of vertices visited during solving
     * @param timeMs          the time taken to solve the maze in milliseconds
     * @param solveMethodName the name of the solving method used
     */
    private void addResolutionStatsToHistory(int pathLength, int visitedCount, long timeMs, String solveMethodName) {
        String text = String.format("%s: Length: %d | Visited: %d | Time: %d ms ",
                solveMethodName, pathLength, visitedCount, timeMs);
        Label statLabel = new Label(text);
        statLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 10.5;");
        Platform.runLater(() -> historyVBox.getChildren().add(0, statLabel));
    }

}
