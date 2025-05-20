package com.example.projetcynapseing1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for mazes, it must be a rectangular
 * 
 * @author Junjie
 */
public class Maze extends Graph implements Serializable {

    /**
     * number of rows from top to bottom
     */
    private final int rows;

    /**
     * number if columns from left to right
     */
    private final int columns;

    /**
     * constructor: create c*l vertices without any edge
     * 
     * @param l: number of lines
     * @param c: number of columns
     */
    public Maze(int l, int c) {
        super();
        this.rows = (l > 0) ? l : 1;
        this.columns = (c > 0) ? c : 1;

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
    public int getRows() {
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

    /**
     * padding Int
     * 
     * @param n:       number to convert
     * @param padding: total length of the string
     * @return number to string with padding
     */
    public static String paddingInt(int n, int padding) {
        // number of spaces to add
        padding = (padding > 0) ? padding : 1;
        int pad = padding - (int) (Math.log10((n > 1) ? n : 2) + 1);
        int padRight = pad / 2;
        int padLeft = pad - padRight;

        return " ".repeat(padLeft) + n + " ".repeat(padRight);
    }

    /**
     * convert a solution to a string
     * 
     * @param antecedents: parent of each vertex
     * @param solution:    parent of each vertex in the solution path
     * @param start: index of the starting vertex
     * @param end: index of the ending vertex
     * @return the maze with the solution in a string format
     */
    public String solutionToString(int[] antecedents, int[] solution, int start, int end) {
        // number of vertices
        int n = rows * columns;

        // verification
        if (antecedents.equals(null) || solution.equals(null)) {
            System.out.println("Param null !");
            return this.toString();
        }
        if (n != antecedents.length || n != solution.length) {
            System.out.println("Inappropriate length of solution !");
            return this.toString();
        }
        for (int i = 0; i < n; i++) {
            if (antecedents[i] < 0 || antecedents[i] > n) {
                System.out.println("Table antecedents is inappropriately indexed: antecedents[" + i + "] = "
                        + antecedents[i] + " !");
                return this.toString();
            }
            if (solution[i] < 0 || solution[i] > n) {
                System.out.println(
                        "Table solution is inappropriately indexed: solution[" + i + "] = " + solution[i] + " !");
                return this.toString();
            }
        }


        // other chars: ·;■;▀;▄;▌;▐;█;═;║;╔;╗;╚;╝;╬;┼;─;│;┌;┐;└;┘;├;┤;┬;┴;+;=;-;|;*
        String s = "";
        ArrayList<Vertex> vertices = this.getVertices();
        // number of characters to print an int
        int padding = (int) (Math.log10(n - 1) + 1);
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
        String wallVertical = MainCLI.GRAY + " │ " + MainCLI.RESET;
        // horizontal wall
        String wallHorizontal = MainCLI.GRAY + "─".repeat(padding + 2) + MainCLI.RESET;
        // horizontal wall next to border
        String wallHorizontalBorder = MainCLI.GRAY + "─".repeat(padding + 1) + MainCLI.RESET;

        // corners
        String cornerEmpty = " ";
        String cornerRight = MainCLI.GRAY + "─" + MainCLI.RESET;
        String cornerDown = MainCLI.GRAY + "│" + MainCLI.RESET;
        String cornerLeft = MainCLI.GRAY + "─" + MainCLI.RESET;
        String cornerUp = MainCLI.GRAY + "│" + MainCLI.RESET;
        String cornerRightDown = MainCLI.GRAY + "┌" + MainCLI.RESET;
        String cornerRightLeft = MainCLI.GRAY + "─" + MainCLI.RESET;
        String cornerRightUp = MainCLI.GRAY + "└" + MainCLI.RESET;
        String cornerDownLeft = MainCLI.GRAY + "┐" + MainCLI.RESET;
        String cornerDownUp = MainCLI.GRAY + "│" + MainCLI.RESET;
        String cornerLeftUp = MainCLI.GRAY + "┘" + MainCLI.RESET;
        String cornerRightDownLeft = MainCLI.GRAY + "┬" + MainCLI.RESET;
        String cornerRightDownUp = MainCLI.GRAY + "├" + MainCLI.RESET;
        String cornerRightLeftUp = MainCLI.GRAY + "┴" + MainCLI.RESET;
        String cornerDownLeftUp = MainCLI.GRAY + "┤" + MainCLI.RESET;
        String cornerAll = MainCLI.GRAY + "┼" + MainCLI.RESET;
        Map<Integer, String> corners = new HashMap<>();
        corners.put(1, cornerEmpty);
        corners.put(2, cornerRight);
        corners.put(3, cornerDown);
        corners.put(5, cornerLeft);
        corners.put(7, cornerUp);
        corners.put(6, cornerRightDown);
        corners.put(10, cornerRightLeft);
        corners.put(14, cornerRightUp);
        corners.put(15, cornerDownLeft);
        corners.put(21, cornerDownUp);
        corners.put(35, cornerLeftUp);
        corners.put(30, cornerRightDownLeft);
        corners.put(42, cornerRightDownUp);
        corners.put(70, cornerRightLeftUp);
        corners.put(105, cornerDownLeftUp);
        corners.put(210, cornerAll);

        // vertical path
        String pathVertical = MainCLI.GREENBACK + " ".repeat(pad) + "│" + " ".repeat(pad) + MainCLI.RESET;
        // vertical path next to left border
        String pathVerticalBorderLeft = pathVertical + " ";
        // vertical path next to right border
        String pathVerticalBorderRight = " " + pathVertical;
        pathVertical = pathVerticalBorderRight + " ";
        // horizontal path
        String pathHorizontal = MainCLI.GREENBACK + "─".repeat(3) + MainCLI.RESET;

        // wrong vertical path
        String pathWrongVertical = MainCLI.REDBACK + " ".repeat(pad) + "│" + " ".repeat(pad) + MainCLI.RESET;
        // wrong vertical path next to left border
        String pathWrongVerticalBorderLeft = pathWrongVertical + " ";
        // wrong vertical path next to right border
        String pathWrongVerticalBorderRight = " " + pathWrongVertical;
        pathWrongVertical = pathWrongVerticalBorderRight + " ";
        // wrong horizontal path
        String pathWrongHorizontal = MainCLI.REDBACK + "─".repeat(3) + MainCLI.RESET;

        // vertical border
        String borderVertical = MainCLI.GRAY + " ║ " + MainCLI.RESET;
        // border horizontal
        String borderHorizontal = "═".repeat(padding * columns + 3 * (columns - 1) + 2);
        // border up
        String borderUp = MainCLI.GRAY + " ╔" + borderHorizontal + "╗ " + MainCLI.RESET;
        // border down
        String borderDown = MainCLI.GRAY + " ╚" + borderHorizontal + "╝ " + MainCLI.RESET;

        // id of vertex
        int i = 0;
        // counter for borders
        int c;

        // border
        s += borderUp + "\n";

        // the maze
        for (int y = 0; y < rows; y++) {
            s += borderVertical;

            for (int x = 0; x < columns; x++) {
                i = y * columns + x;
                s += (solution[i] != i || (i == start && solution[end] != end)) ? MainCLI.GREENBACK + MainCLI.BOLD : (antecedents[i] != i || i == start) ? MainCLI.REDBACK : "";
                s += Maze.paddingInt(i, padding);
                s += (solution[i] != i || antecedents[i] != i) ? MainCLI.RESET : "";

                // if not the last column
                if (x < columns - 1) {
                    // if vertex n + 1 neighboring
                    if (((vertices.get(i)).getNeighbors()).contains(vertices.get(i + 1))) {
                        // if path solution
                        if (solution[i] == i + 1 || solution[i + 1] == i) {
                            s += pathHorizontal;
                        }
                        // if path
                        else if (antecedents[i] == i + 1 || antecedents[i + 1] == i) {
                            s += pathWrongHorizontal;
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
            s += borderVertical + "\n";
            // if not the last row
            if (y < rows - 1) {
                s += borderVertical;

                for (int x = 0; x < columns; x++) {
                    i = y * columns + x;

                    // if vertex n + columns neighboring
                    if (((vertices.get(i)).getNeighbors()).contains(vertices.get(i + columns))) {
                        // if path solution
                        if (solution[i] == i + columns || solution[i + columns] == i) {
                            s += (x == 0) ? pathVerticalBorderLeft
                                    : (x == columns - 1) ? pathVerticalBorderRight : pathVertical;
                        }
                        // if path
                        else if (antecedents[i] == i + columns || antecedents[i + columns] == i) {
                            s += (x == 0) ? pathWrongVerticalBorderLeft
                                    : (x == columns - 1) ? pathWrongVerticalBorderRight : pathWrongVertical;
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
                        c = 1;

                        // tests to chose the corner
                        if (!((vertices.get(i + 1)).getNeighbors()).contains(vertices.get(i + 1 + columns))) {
                            c *= 2;
                        }
                        if (!((vertices.get(i + columns)).getNeighbors()).contains(vertices.get(i + 1 + columns))) {
                            c *= 3;
                        }
                        if (!((vertices.get(i)).getNeighbors()).contains(vertices.get(i + columns))) {
                            c *= 5;
                        }
                        if (!((vertices.get(i)).getNeighbors()).contains(vertices.get(i + 1))) {
                            c *= 7;
                        }

                        s += corners.get(c);
                    }
                }

                // next row
                s += borderVertical + "\n";
            }
        }

        // border
        s += borderDown + "\n";

        return s;
    }

    /**
     * convert a solution to a string
     * 
     * @param solution: parent of each vertex in the solution path
     * @return the maze with the solution in a string format
     */
    public String solutionToString(int[] solution, int start, int end) {
        return this.solutionToString(solution, solution, start, end);
    }

    /**
     * @return the maze in a string format
     */
    @Override
    public String toString() {
        // other chars: ·;■;▀;▄;▌;▐;█;═;║;╔;╗;╚;╝;╬;┼;─;│;┌;┐;└;┘;+;=;-;|;*
        String s = "";
        List<Vertex> vertices = this.getVertices();
        // number vertices
        int n = rows * columns;
        // number of characters to print an int
        int padding = (int) (Math.log10(n - 1) + 1);
        // padding must be odd
        padding += (padding % 2 == 0) ? 1 : 0;
        // padding must > 2
        padding = (padding < 3) ? 3 : padding;

        // absence of vertical wall
        String spaceVertical = "   ";
        // absence of horizontal wall
        String spaceHorizontal = " ".repeat(padding + 2);
        // absence of horizontal wall next to border
        String spaceHorizontalBorder = " ".repeat(padding + 1);

        // vertical wall
        String wallVertical = MainCLI.GRAY + " │ " + MainCLI.RESET;
        // horizontal wall
        String wallHorizontal = MainCLI.GRAY + "─".repeat(padding + 2) + MainCLI.RESET;
        // horizontal wall next to border
        String wallHorizontalBorder = MainCLI.GRAY + "─".repeat(padding + 1) + MainCLI.RESET;

        // corners
        String cornerEmpty = " ";
        String cornerRight = MainCLI.GRAY + "─" + MainCLI.RESET;
        String cornerDown = MainCLI.GRAY + "│" + MainCLI.RESET;
        String cornerLeft = MainCLI.GRAY + "─" + MainCLI.RESET;
        String cornerUp = MainCLI.GRAY + "│" + MainCLI.RESET;
        String cornerRightDown = MainCLI.GRAY + "┌" + MainCLI.RESET;
        String cornerRightLeft = MainCLI.GRAY + "─" + MainCLI.RESET;
        String cornerRightUp = MainCLI.GRAY + "└" + MainCLI.RESET;
        String cornerDownLeft = MainCLI.GRAY + "┐" + MainCLI.RESET;
        String cornerDownUp = MainCLI.GRAY + "│" + MainCLI.RESET;
        String cornerLeftUp = MainCLI.GRAY + "┘" + MainCLI.RESET;
        String cornerRightDownLeft = MainCLI.GRAY + "┬" + MainCLI.RESET;
        String cornerRightDownUp = MainCLI.GRAY + "├" + MainCLI.RESET;
        String cornerRightLeftUp = MainCLI.GRAY + "┴" + MainCLI.RESET;
        String cornerDownLeftUp = MainCLI.GRAY + "┤" + MainCLI.RESET;
        String cornerAll = MainCLI.GRAY + "┼" + MainCLI.RESET;
        Map<Integer, String> corners = new HashMap<>();
        corners.put(1, cornerEmpty);
        corners.put(2, cornerRight);
        corners.put(3, cornerDown);
        corners.put(5, cornerLeft);
        corners.put(7, cornerUp);
        corners.put(6, cornerRightDown);
        corners.put(10, cornerRightLeft);
        corners.put(14, cornerRightUp);
        corners.put(15, cornerDownLeft);
        corners.put(21, cornerDownUp);
        corners.put(35, cornerLeftUp);
        corners.put(30, cornerRightDownLeft);
        corners.put(42, cornerRightDownUp);
        corners.put(70, cornerRightLeftUp);
        corners.put(105, cornerDownLeftUp);
        corners.put(210, cornerAll);

        // vertical border
        String borderVertical = MainCLI.GRAY + " ║ " + MainCLI.RESET;
        // border horizontal
        String borderHorizontal = "═".repeat(padding * columns + 3 * (columns - 1) + 2);
        // border up
        String borderUp = MainCLI.GRAY + " ╔" + borderHorizontal + "╗ " + MainCLI.RESET;
        // border down
        String borderDown = MainCLI.GRAY + " ╚" + borderHorizontal + "╝ " + MainCLI.RESET;

        // id of vertex
        int i = 0;
        // counter for borders
        int c;

        // border
        s += borderUp + "\n";

        // the maze
        for (int y = 0; y < rows; y++) {
            s += borderVertical;

            for (int x = 0; x < columns; x++) {
                i = y * columns + x;
                s += Maze.paddingInt(i, padding);

                // if not the last column
                if (x < columns - 1) {
                    // if vertex n + 1 neighboring
                    if (((vertices.get(i)).getNeighbors()).contains(vertices.get(i + 1))) {
                        s += spaceVertical;
                    }
                    // if there's a wall
                    else {
                        s += wallVertical;
                    }
                }
            }

            // line between 2 rows
            s += borderVertical + "\n";
            // if not the last row
            if (y < rows - 1) {
                s += borderVertical;

                for (int x = 0; x < columns; x++) {
                    i = y * columns + x;

                    // if vertex n + columns neighboring
                    if (((vertices.get(i)).getNeighbors()).contains(vertices.get(i + columns))) {
                        s += (x > 0 && x < columns - 1) ? spaceHorizontal : spaceHorizontalBorder;
                    }
                    // if there's a wall
                    else {
                        s += (x > 0 && x < columns - 1) ? wallHorizontal : wallHorizontalBorder;
                    }

                    // if not the last column
                    if (x < columns - 1) {
                        c = 1;

                        // tests to chose the corner
                        if (!((vertices.get(i + 1)).getNeighbors()).contains(vertices.get(i + 1 + columns))) {
                            c *= 2;
                        }
                        if (!((vertices.get(i + columns)).getNeighbors()).contains(vertices.get(i + 1 + columns))) {
                            c *= 3;
                        }
                        if (!((vertices.get(i)).getNeighbors()).contains(vertices.get(i + columns))) {
                            c *= 5;
                        }
                        if (!((vertices.get(i)).getNeighbors()).contains(vertices.get(i + 1))) {
                            c *= 7;
                        }

                        s += corners.get(c);
                    }
                }

                // next row
                s += borderVertical + "\n";
            }
        }

        // border
        s += borderDown + "\n";

        return s;
    }
}
