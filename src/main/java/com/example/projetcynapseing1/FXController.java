package com.example.projetcynapseing1;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class FXController {

    @FXML
    private ImageView backgroundImage;
    @FXML
    private StackPane stackpane;
    @FXML
    private void initialize(){
        backgroundImage.fitWidthProperty().bind(stackpane.widthProperty());
        backgroundImage.fitHeightProperty().bind(stackpane.heightProperty());
    }

    @FXML
    private Button resolutionLabyrinth;

    @FXML
    private Button generationLabyrinth;

    private MazeController mazeController;
    private boolean labyrinthIsGenerated = false;

    @FXML
    private Canvas mazeCanvas;

    private int blockSize = 40;
    private Maze maze;
    private int rows;
    private int cols;


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

    // Called when the resolution button is clicked
    @FXML
    protected void onStartResolutionClick() {
    }

    // Called when the generation button is clicked
    @FXML
    protected void onStartGenerationClick() {
        labyrinthIsGenerated = true;
        resolutionLabyrinth.setDisable(false);
    }

    // Method to display the maze
    public void displayMaze(Maze maze) {
        this.maze = maze;
        drawMazeWithWalls();
    }

    // Method to draw the maze
    private void drawMazeWithWalls() {
        if (mazeCanvas == null) {
            System.out.println("Canvas is not initialized!");
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
            Color fillColor;

            switch (v.getState()) {
                case VISITED:
                    fillColor = Color.rgb(169, 169, 169);
                    break;
                case SOLUTION:
                    fillColor = Color.rgb(173, 216, 230);
                    break;
                default:
                    fillColor = Color.WHITE;
            }

            g.setFill(fillColor);
            g.fillRect(x, y, blockSize, blockSize);

            // Drawing walls between two cells if they are not neighbors
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

    // Method to check if a vertex has a neighbor
    private boolean hasNeighbor(Vertex v, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols)
            return false;
        Vertex neighbor = maze.getVertexByIDVertex(r * cols + c);
        return v.isNeighbor(neighbor);
    }

}
