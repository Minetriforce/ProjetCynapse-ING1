package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for mazes, it must be a rectangular
 */
public class Maze extends Graph {
    private final int rows;
    private final int columns;
    private final MethodName.GenMethodName genMethodName;

    /**
     * constructor: create c*l vertices without any edge
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
     * @return rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * getter of columns
     * @return columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * getter of genMethodName
     * @return genMethoName
     */
    public MethodName.GenMethodName getGenMethod() {
        return this.genMethodName;
    }

    /**
     * @param n: number to convert
     * @param padding: total length of the string
     * @return number to string with padding
     */
    public static String paddingInt(int n, int padding){
        // number of spaces to add
        int pad = padding - (int)(Math.log10((n > 1) ? n : 2) + 1);
        int padRight = pad / 2;
        int padLeft = pad - padRight;
        
        return " ".repeat(padLeft) + n + " ".repeat(padRight);
    }

    /**
     * convert a solution to a string
     * @param solution: parent of each vertex in the solution
     * @return the maze with the solution in a string format
     */
    public String solutionToString(int[] solution) {
        String s = "";
        ArrayList<Vertex> vertices = this.getVertices();
        // number of characters to print an int
        int padding = (int)(Math.log10(rows * columns - 1) + 1);
        // padding must be odd
        padding += (padding % 2 == 0) ? 1 : 0;
        // padding must > 2
        padding = (padding < 3) ? 3 : padding;
        // absence of vertical wall
        String spaceVertical = "   ";
        // absence of horizontal wall
        String spaceHorizontal = " ".repeat(padding);
        // vertical wall
        String wallVertical = " | ";
        // horizontal wall
        String wallHorizontal = "-".repeat(padding);
        // corner
        String corner = " + ";
        // vertical path
        String pathVertical = " * ";
        // horizontal path
        String pathHorizontal = "*".repeat(padding);

        // the maze
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                // id of the vertex
                int n = y * columns + x;
                s += Maze.paddingInt(n, padding);
                // if not the last column
                if (x < columns - 1) {
                    // if vertex n + 1 neighboring
                    if (((vertices.get(n)).getNeighbors()).contains(vertices.get(n + 1))) {
                        // if path
                        if (solution[n] == n + 1 || solution[n + 1] == n) {
                            s += pathHorizontal;
                        }
                        // if not path
                        else {
                            s += spaceVertical;
                        }
                    }
                    // if there's a wall
                    else {
                        s += wallVertical;
                    }
                }
            }
            // line between 2 rows
            s += "\n";
            // if not the last row
            if (y < rows - 1) {
                for (int x = 0; x < columns; x++) {
                    // id of the vertex
                    int n = y * columns + x;
                    // if vertex n + columns neighboring
                    if (((vertices.get(n)).getNeighbors()).contains(vertices.get(n + columns))) {
                        // if path
                        if (solution[n] == n + columns || solution[n + columns] == n) {
                            s += pathVertical;
                        }
                        // if not path
                        else {
                            s += spaceHorizontal;
                        }
                    }
                    // if there's a wall
                    else {
                        s += wallHorizontal;
                    }
                    // if not the last column
                    if (x < columns - 1) {
                        s += corner;
                    }
                }
                // next row
                s += "\n";
            }
        }

        return s;
    }

    /**
     * @return the maze in a string format
     */
    @Override
    public String toString() {
        // other chars: ●;•;·;■;▀;▄;▌;▐;█;▓;▒;░;═;║;╔;╗;╚;╝;╬;┼;─;│;┌;┐;└;┘;+;=;-;|;*
        String s = "";
        List<Vertex> vertices = this.getVertices();
        // number of characters to print an int
        int padding = (int)(Math.log10(rows * columns - 1) + 1);
        // padding must be odd
        padding += (padding % 2 == 0) ? 1 : 0;
        // padding must > 2
        padding = (padding < 3) ? 3 : padding;
        // absence of vertical wall
        String spaceVertical = "   ";
        // absence of horizontal wall
        String spaceHorizontal = " ".repeat(padding);
        // vertical wall
        String wallVertical = " | ";
        // horizontal wall
        String wallHorizontal = "-".repeat(padding);
        // corner
        String corner = " + ";

        // the maze
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                // id of the vertex
                int n = y * columns + x;
                s += Maze.paddingInt(n, padding);
                // if not the last column
                if (x < columns - 1) {
                    // if there's not a wall
                    if (((vertices.get(n)).isNeighbor(vertices.get(n + 1)))){
                        s += spaceVertical;
                    }
                    // if there's a wall
                    else{
                        s += wallVertical;
                    }
                }
            }
            // line between 2 rows
            s += "\n";
            // if not the last row
            if (y < rows - 1) {
                for (int x = 0; x < columns; x++) {
                    // id of the vertex
                    int n = y * columns + x;
                    // if there's not a wall
                    if (((vertices.get(n)).isNeighbor(vertices.get(n + columns)))){
                        s += spaceHorizontal;
                    }
                    // if there's a wall
                    else{
                        s += wallHorizontal;
                    }
                    // if not the last column
                    if (x < columns - 1) {
                        s += corner;
                    }
                }
                // next row
                s += "\n";
            }
        }

        return s;
    }
}
