/**
 * Generator class is used to generate a maze according to differents parameters.
 * This class use the Graph class (see {@link com.example.projetcynapseing1.Graph})
 * @author Bari-joris
 * @version 1.0
 */

package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Generator {
    /**
     * Number of rows in the maze, strictly positive
     */
    private Integer rows;
    /**
     * Number of columns in the maze, strictly positive
     */
    private Integer columns;
    /**
     * The genMethod field is used to know wich algorithm will be executed
     * to generate the maze.
     * Please see enumeration GeneMethodName in
     * {@link com.example.projetcynapseing1.MethodName.GenMethodName}
     */
    private MethodName.GenMethodName genMethod;
    /**
     * Time step is the time between each maze generation step.
     * It is only used when type of generation is "Step-by-Step"
     */
    private double timeStep = 0.0;
    /**
     * Ech generation method use a random number generator (RNG);
     * The user can change the seed of this RNG.
     */
    private Integer seed;

    /**
     * <p>
     * Constructor of class Generator
     * Class references :
     * MethodName.GenMethodName :
     * {@link com.example.projetcynapseing1.MethodName.GenMethodName}
     * </p>
     * 
     * @return void
     * @since 1.0
     */
    public Generator(Integer rows, Integer colums, MethodName.GenMethodName genMethod, Integer seed) throws Exception {
        if (rows < 0 || colums < 0) {
            throw new Exception("rows or column can't be negative");
        } else if (timeStep < 0.0) {
            throw new Exception("timeStep can't be negative");
        } else if (seed < 0) {
            throw new Exception("seed can't be negative");
        }
        this.rows = rows;
        this.columns = colums;
        this.genMethod = genMethod;
        this.seed = seed;
    }

    /**
     * <p>
     * This method is used to create a grid graph of size rows * columns.
     * It is used as the first step of every generation method.
     * Class references :
     * Graph : {@link com.example.projetcynapseing1.Graph}
     * </p>
     * 
     * @return a grid graph of size rows * columns
     * @see
     * @since 1.0
     */
    public Graph makeGridGraph() {
        Graph G = new Graph();
        Integer i = 0;

        // Step 1 : Create all the vertices and give them their ID and postion in a grid
        for (int n = 0; n < this.rows; n++) {
            for (int m = 0; m < this.columns; m++) {
                try {
                    Vertex V = new Vertex(n, m);
                    i = i + 1;
                    G.addVertex(V);
                } catch (Exception e) {
                    System.out.println("Error occured while creating Vertex with parameters : ");
                    System.out.println("n : " + n + ", m : " + m + ", i : " + i);
                    System.out.println(e.getMessage());
                }
            }
        }

        ArrayList<Vertex> ListVertex = G.getVertices();

        // Step 2 : link every vertices to others in order to make a grid
        for (int n = 0; n < this.rows; n++) {
            for (int m = 0; m < this.columns; m++) {
                Vertex current = ListVertex.get(n * this.columns + m);

                if (m < this.columns - 1) {
                    Vertex right = ListVertex.get(n * this.columns + (m + 1));
                    G.addEdge(new Edge(current, right));
                }
                if (n < this.rows - 1) {
                    Vertex bottom = ListVertex.get((n + 1) * this.columns + m);
                    G.addEdge(new Edge(current, bottom));
                }
            }
        }
        return G;
    }

    public void addRandomWeight(Graph G) {
        Random rng = new Random();
        ArrayList<Edge> edges = G.getEdges();
        for (Edge edge : edges) {
            try {
                edge.setWeight(rng.nextInt(100));
            } catch (Exception e) {
                System.out.println("Error occured while adding weight to Edge : " + edge.toString());
                System.out.println(e.getMessage());
            }
        }
    }

    public Graph makeMaze(MethodName.Type type) {
        Graph base = this.makeGridGraph();
        Graph maze = new Graph();
        for (Vertex V : base.getVertices()) {
            maze.addVertex(V);
        }

        switch (this.genMethod) {
            case KRUSKAL:
                this.addRandomWeight(base);
                Collections.sort(base.getEdges());
                break;

            default:
                break;
        }
        return maze;
    }
}
