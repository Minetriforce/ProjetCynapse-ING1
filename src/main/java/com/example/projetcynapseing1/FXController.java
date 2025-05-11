package com.example.projetcynapseing1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * JavaFX Controller for handling maze display and button actions.
 * @author Florianne
 */
public class FXController {

    @FXML
    private Canvas mazeCanvas;

    private int blockSize = 40;
    private Maze maze;
    private int rows;
    private int cols;
    public void setMazeSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        if (rows > 20 || cols > 20) {
            blockSize = 20;
        } else {
            blockSize = 40;
        }
    }



    /**
     * Called by the main application to pass the maze and draw it.
     *
     * @param maze the maze object to display
     */
    public void displayMaze(Maze maze) {
        this.maze = maze;
        drawMazeWithWalls();
    }

    /**
     * Handles click on "Hello" button.
     */
    @FXML
    protected void onHelloButtonClick() {
        System.out.println("Hello button clicked!");
    }

    /**
     * Handles click on "Start Generation" button.
     */
    @FXML
    protected void onStartGenerationClick() {
        System.out.println("Start generation button clicked!");

    }

    /**
     * Draw the maze by rendering each cell and its walls.
     */
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
            //colors based on the state of the case
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

            //draw a wall between two cases if they are not neighbors.
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
     * Check if the vertex has a neighbor in a given direction.
     */
    private boolean hasNeighbor(Vertex v, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols)
            return false;
        Vertex neighbor = maze.getVertexByIDVertex(r * cols + c);
        return v.isNeighbor(neighbor);
    }
}