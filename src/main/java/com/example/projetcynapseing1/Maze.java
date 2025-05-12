package com.example.projetcynapseing1;

import java.util.List;

/**
 * Class for mazes, it must be a rectangular
 */
public class Maze extends Graph {
    private final int rows;
    private final int columns;
    private final MethodName.GenMethodName genMethodName;

    /**
     * constructor
     * 
     * @param l: number of lines
     * @param c: number of columns
     */
    public Maze(int l, int c, MethodName.GenMethodName genMethodName) {
        super();
        this.rows = l;
        this.columns = c;

        this.genMethodName = genMethodName;

        for (int y = 0; y < l; y++) {
            for (int x = 0; x < c; x++) {
                try {
                    this.addVertex(new Vertex(x, y, y * c + x));
                } catch (Exception e) {
                    System.out.print("-- Maze Class --");
                    System.out.println("Error while creating vertice: (" + x + ", " + y + ")");
                }
            }
        }

    }

    /**
     * getter of rows
     * 
     * @return rows
     */
    public int getrows() {
        return rows;
    }

    /**
     * getter of columns
     * 
     * @return columns
     */
    public int getColumns() {
        return columns;
    }

    public MethodName.GenMethodName getGenMethod() {
        return this.genMethodName;
    }

    /**
     * @return the maze in a string format
     */
    public String toString() {
        String s = "";
        List<Vertex> vertices = this.getVertices();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                int n = y * columns + x;
                s += String.format("%-2d", n);
                if (x < columns - 1) {
                    s += (((vertices.get(n)).getNeighbors()).contains(vertices.get(n + 1))) ? "  " : "||";
                }
            }
            s += "\n";
            if (y < rows - 1) {
                for (int x = 0; x < columns; x++) {
                    int n = y * columns + x;
                    s += (((vertices.get(n)).getNeighbors()).contains(vertices.get(n + columns))) ? "  " : "--";
                    if (x < columns - 1) {
                        s += "  ";
                    }
                }
                s += "\n";
            }
        }

        return s;
    }
}
