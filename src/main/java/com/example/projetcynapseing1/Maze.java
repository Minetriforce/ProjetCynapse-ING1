package com.example.projetcynapseing1;

import java.util.List;

/**
 * Class for mazes, it must be a rectangular
 */
public class Maze extends Graph{
    private final int nbLines;
    private final int nbColumns;

    /**
     * constructor
     * @param l: number of lines
     * @param c: number of columns
     */
    public Maze(int l, int c) {
        super();
        nbLines = l;
        nbColumns = c;

        for (int y = 0; y < l; y++){
            for (int x = 0; x < c; x++){
                try {
                    this.addVertex(new Vertex(x, y, y * c + x));
                }
                catch (Exception e){
                    System.out.println("Error while creating vertice: (" + x + ", " + y + ")");
                }
            }
        }
    }

    /**
     * getter of nbLines
     * @return nbLines
     */
    public int getNbLines(){
        return nbLines;
    }
    /**
     * getter of nbColumns
     * @return nbColumns
     */
    public int getNbColumns(){
        return nbColumns;
    }

    /**
     * @return the maze in a string format
     */
    public String toString(){
        String s = "";
        List<Vertex> vertices = this.getVertices();

        for (int y = 0; y < nbLines; y++){
            for (int x = 0; x <nbColumns; x++){
                int n = y * nbColumns + x;
                s += String.format("%-2d", n);
                if (x < nbColumns - 1){
                    s += (((vertices.get(n)).getNeighbors()).contains(vertices.get(n + 1))) ? "  " : "||";
                }
            }
            s += "\n";
            if (y < nbLines - 1){
                for (int x = 0; x < nbColumns; x++){
                    int n = y * nbColumns + x;
                    s += (((vertices.get(n)).getNeighbors()).contains(vertices.get(n + nbColumns))) ? "  " : "--";
                    if (x < nbColumns - 1){
                        s += "  ";
                    }
                }
                s += "\n";
            }
        }

        return s;
    }
}
