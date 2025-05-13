package com.example.projetcynapseing1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for mazes, it must be a rectangular
 */
public class Maze extends Graph implements Serializable {
    private final int rows;
    private final int columns;
    private final MethodName.GenMethodName genMethodName;

    // ANSI escape codes for colors
    final String RESET = "\u001B[0m";
    final String GREEN = "\u001b[38;5;46m";
    final String GRAY = "\u001b[38;5;244m";

    /**
     * constructor: create c*l vertices without any edge
     * @param l: number of lines
     * @param c: number of columns
     */
    public Maze(int l, int c, MethodName.GenMethodName genMethodName) {
        super();
        this.rows = (l > 0) ? l : 1;
        this.columns = (c > 0) ? c : 1;

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
        padding = (padding > 0) ? padding : 1;
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
        // number of vertices
        int n = rows * columns;

        // verification
        if (solution.equals(null)){
            System.out.println("Param null !");
            return this.toString();
        }
        if (n != solution.length){
            System.out.println("Inappropriate length of solution: " + solution.length + " (insted of " + n + ") !");
            return this.toString();
        }
        for (int i = 0; i < n; i++){
            if (solution[i] < 0 || solution[i] > n){
                System.out.println("Table solution is inappropriately indexed: solution[" + i + "] = " + solution[i] + " !");
                return this.toString();
            }
        }

        // other chars: ·;■;▀;▄;▌;▐;█;▓;▒;░;═;║;╔;╗;╚;╝;╬;┼;─;│;┌;┐;└;┘;+;=;-;|;*
        String s = "";
        ArrayList<Vertex> vertices = this.getVertices();
        // number of characters to print an int
        int padding = (int)(Math.log10(n - 1) + 1);
        // padding must be odd
        padding += (padding % 2 == 0) ? 1 : 0;
        // padding must > 2
        padding = (padding < 3) ? 3 : padding;
        // padding for special case
        int pad = padding / 2;
        // absence of vertical wall
        String spaceVertical = "   ";
        // absence of horizontal wall
        String spaceHorizontal = " ".repeat(padding + 2);
        // absence of horizontal wall next to border
        String spaceHorizontalBorder = " ".repeat(padding + 1);
        // vertical wall
        String wallVertical = GRAY+" │ "+RESET;
        // horizontal wall
        String wallHorizontal = GRAY+"─".repeat(padding + 2)+RESET;
        // horizontal wall next to border
        String wallHorizontalBorder = GRAY+"─".repeat(padding + 1)+RESET;
        // corner
        String corner = GRAY+"┼"+RESET;
        // vertical path
        String pathVertical = GREEN + " ".repeat(pad) + "│" + " ".repeat(pad) + RESET;
        // vertical path next to left border
        String pathVerticalBorderLeft = GREEN + pathVertical + " " + RESET;
        // vertical path next to right border
        String pathVerticalBorderRight = " " + pathVertical;
        pathVertical = pathVerticalBorderRight + " ";
        // horizontal path
        String pathHorizontal = GREEN + "─".repeat(3) + RESET;

        //border
        s += GRAY+" ╔" + "═".repeat(padding * columns + 3 * (columns - 1) + 2) + "╗ " + "\n"+RESET;

        // id of vertex
        int i = 0;
        // the maze
        for (int y = 0; y < rows; y++) {
            s += GRAY + " ║ " + RESET;
            for (int x = 0; x < columns; x++) {
                i = y * columns + x;
                s += Maze.paddingInt(i, padding);
                // if not the last column
                if (x < columns - 1) {
                    // if vertex n + 1 neighboring
                    if (((vertices.get(i)).getNeighbors()).contains(vertices.get(i + 1))) {
                        // if path
                        if (solution[i] == i + 1 || solution[i + 1] == i) {
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
            s += GRAY+" ║ " + "\n"+RESET;
            // if not the last row
            if (y < rows - 1) {
                s += GRAY+" ║ "+RESET;
                for (int x = 0; x < columns; x++) {
                    i = y * columns + x;
                    // if vertex n + columns neighboring
                    if (((vertices.get(i)).getNeighbors()).contains(vertices.get(i + columns))) {
                        // if path
                        if (solution[i] == i + columns || solution[i + columns] == i) {
                            s += (x == 0) ? pathVerticalBorderLeft : ((x == columns - 1) ? pathVerticalBorderRight : pathVertical);
                        }
                        // if not path
                        else {
                            s += (x > 0 && x < columns - 1) ? spaceHorizontal : spaceHorizontalBorder;
                        }
                    }
                    // if there's a wall
                    else {
                        s += (x > 0 && x < columns - 1) ? wallHorizontal : wallHorizontalBorder;
                    }
                    // if not the last column
                    if (x < columns - 1) {
                        s += corner;
                    }
                }
                // next row
                s += GRAY + " ║ " + "\n" + RESET;
            }
        }

        //border
        s += GRAY+" ╚" + "═".repeat(padding * columns + 3 * (columns - 1) + 2) + "╝ " + "\n"+RESET;

        return s;
    }

    /**
     * @return the maze in a string format
     */
    @Override
    public String toString() {
        // other chars: ·;■;▀;▄;▌;▐;█;▓;▒;░;═;║;╔;╗;╚;╝;╬;┼;─;│;┌;┐;└;┘;+;=;-;|;*
        String s = "";
        List<Vertex> vertices = this.getVertices();
        // number vertices
        int n = rows * columns;
        // number of characters to print an int
        int padding = (int)(Math.log10(n - 1) + 1);
        // padding must be odd
        padding += (padding % 2 == 0) ? 1 : 0;
        // padding must > 2
        padding = (padding < 3) ? 3 : padding;
        // absence of vertical wall
        String spaceVertical = "   ";
        // absence of horizontal wall
        String spaceHorizontal = " ".repeat(padding);
        // vertical wall
        String wallVertical = " │ ";
        // horizontal wall
        String wallHorizontal = "─".repeat(padding);
        // corner
        String corner = " · ";

        //border
        s += " ╔" + "═".repeat(padding * columns + 3 * (columns - 1) + 2) + "╗ " + "\n";

        // id of vertex
        int i = 0;
        // the maze
        for (int y = 0; y < rows; y++) {
            s += " ║ ";
            for (int x = 0; x < columns; x++) {
                i = y * columns + x;
                s += Maze.paddingInt(i, padding);
                // if not the last column
                if (x < columns - 1) {
                    // if there's not a wall
                    if (((vertices.get(i)).isNeighbor(vertices.get(i + 1)))){
                        s += spaceVertical;
                    }
                    // if there's a wall
                    else{
                        s += wallVertical;
                    }
                }
            }
            // line between 2 rows
            s += " ║ " + "\n";
            // if not the last row
            if (y < rows - 1) {
                s += " ║ ";
                for (int x = 0; x < columns; x++) {
                    i = y * columns + x;
                    // if there's not a wall
                    if (((vertices.get(i)).isNeighbor(vertices.get(i + columns)))){
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
                s += " ║ " + "\n";
            }
        }

        //border
        s += " ╚" + "═".repeat(padding * columns + 3 * (columns - 1) + 2) + "╝ " + "\n";

        return s;
    }
}
