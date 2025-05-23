package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Generator class is used to generate a maze according to differents
 * parameters.
 * This class use the Graph class (see
 * {@link com.example.projetcynapseing1.Graph})
 *
 * @author Bari-joris
 */
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
     * Each generation method use a random number generator (RNG);
     * The user can change the seed of this RNG.
     */
    private Integer seed;

    /**
     * Constructor of class generator.
     * Rows and columns are used to set the size of a rectangle maze
     *
     * @param rows      follows y axis from bottom to top
     * @param colums    follows x axis from elft to right
     * @param genMethod generation method used for the maze
     * @param seed      integer used in the random number generator (the use of
     *                  this
     *                  generator depends on method generation). The same seed
     *                  returns the same maze
     * @throws Exception used to ensure Generator stays in a logical state
     */
    public Generator(Integer rows, Integer colums, MethodName.GenMethodName genMethod, Integer seed) throws Exception {
        if (rows < 1 || colums < 1 || rows == null || colums == null) {
            throw new IllegalArgumentException("rows or column can't be negative/null");
        } else if (seed < 0) {
            throw new IllegalArgumentException("seed can't be negative");
        }
        this.rows = rows;
        this.columns = colums;
        this.genMethod = genMethod;

        this.seed = seed;
    }

    /* SETTERS */
    /**
     * Set number of rows of the maze
     * 
     * @param rows number of rows
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * Set number of columns
     * 
     * @param columns number of columns
     */
    public void setColumns(Integer columns) {
        if (columns <= 0)
            this.columns = columns;
    }

    /**
     * Set generation method
     * 
     * @param genMethod KRUSKAL / PRIM / DFS / UNPERFECT
     */
    public void setGenMethod(MethodName.GenMethodName genMethod) {
        this.genMethod = genMethod;
    }

    /**
     * Set seed of the randomn number generator
     * 
     * @param seed null to or 0 to have a random seed, else put a strictly positive
     *             integer
     */
    public void setSeed(Integer seed) {
        // In case seed is null, generate a random seed
        if (this.seed == null) {
            System.out.println("Warning : seed is null, generating random seed");
            this.seed = new Random().nextInt(100000);
            System.out.println("Seed generated : " + this.seed);
        } else {
            this.seed = seed;
        }
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
     */
    private Maze makeGridGraph() {
        Maze G = new Maze(this.rows, this.columns);

        // link every vertices to others in order to make a grid
        ArrayList<Vertex> ListVertex = G.getVertices();
        for (Vertex vertex : ListVertex) {
            if ((vertex.getX() + 1) != this.columns) {
                G.addEdge(new Edge(vertex, G.getVertexByID(vertex.getID() + 1)));
            }
            if (vertex.getY() != 0) {
                G.addEdge(new Edge(vertex, G.getVertexByID(vertex.getID() - this.columns)));
            }
        }
        return G;
    }

    /**
     * add a random weight to every Edge in graph using a random number generator
     *
     * @param G
     */
    private void addRandomWeight(Maze G) {
        Random rng = new Random(this.seed);
        for (Edge edge : G.getEdges()) {
            try {
                edge.setWeight(rng.nextInt(100));
            } catch (Exception e) {
                System.out.println("Error occured while adding weight to Edge : " + edge.toString());
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * get the representative Vertex ID of the group of vertex i
     *
     * @param i      ID of the Vertex
     * @param parent list of groups (trees)
     * @return representative ID of the tree OR recusive call
     */
    private int find(Integer i, int[] parent) {
        if (parent[i] != i) {
            parent[i] = find(parent[i], parent); // path compression to reduce access time
        }
        return parent[i];
    }

    /**
     * Create a union between two tree (groups)
     *
     * @param i      Representative ID of the first tree
     * @param j      Representative ID of the second tree
     * @param parent list of groups (trees)
     */
    private void union(Integer i, Integer j, int[] parent) {
        Integer irep = find(i, parent);
        Integer jrep = find(j, parent);

        parent[jrep] = irep;
    }

    /**
     * Complete union find method.
     * It is used to detect cylce in a graph : If two vertices are in the same tree
     * (i.e. have the same representive vertex ID), then return true
     *
     * @param a      First Vertex to check
     * @param b      Second Vertex to check
     * @param parent List of groups (trees)
     * @return True: these two Vertices are in the same tree; False: these two
     *         Vertices are NOT in the same tree
     */
    private Boolean unionFind(Vertex a, Vertex b, int[] parent) {
        int arep = find(a.getID(), parent);
        int brep = find(b.getID(), parent);

        return arep == brep;
    }

    /**
     * Use the Kruskal's algorithm to generate the maze.
     * After this fuction, maze will be the minimum spanning tree (MST)
     *
     * @param baseGraph Grid, weighted graph used as a base to generate maze
     * @param maze      generated maze
     */
    private void kruskal(Maze baseGraph, Maze maze) {
        // Initialize List of representative Vertex ID for the union Find method
        int[] parents = new int[maze.getVertices().size()];
        for (int m = 0; m < maze.getVertices().size(); m++) {
            parents[m] = m; // each Vertex have itself as a representative ID, each vertex have it's unique
            // own tree
        }

        // Uses merge sort algorithm O(n*log(n)), to sort edges in ascending order
        Collections.sort(baseGraph.getEdges());

        for (Edge edge : baseGraph.getEdges()) { // look at each edges in ascending order
            if (unionFind(edge.getVertexA(), edge.getVertexB(), parents) == false) { // add edge to maze only if it does
                // not create a cycle
                maze.addEdge(new Edge(maze.getVertexByID(edge.getVertexA().getID()),
                        maze.getVertexByID(edge.getVertexB().getID()))); // Add adge to maze
                union(edge.getVertexA().getID(), edge.getVertexB().getID(), parents); // merge trees in the list of
                // trees (parents)

                if (maze.getEdges().size() == maze.getVertices().size() - 1) {
                    break;
                }
            }
        }
    }

    /**
     * Create a maze according to Prim's algorithm
     * After this fuction, maze will be the minimum spanning tree (MST)
     *
     * @param baseGraph
     * @param maze
     * @param s
     */
    private void prim(Maze baseGraph, Maze maze, Vertex s) {
        ArrayList<Edge> currentEdges = new ArrayList<Edge>(); // create list of current Edges

        // Union Find algorithm initilializer
        int[] parents = new int[maze.getVertices().size()];

        // Create a mark list for all vertices in maze
        ArrayList<Boolean> mark = new ArrayList<Boolean>();
        for (int i = 0; i < baseGraph.getVertices().size(); i++) {
            mark.add(false);
            parents[i] = i;
        }

        /**
         * Initialization of Prim's algorithm
         * From Vertex s, add all it's edges to possible current Edges
         * Mark vertex s in mark list
         */
        currentEdges.addAll(baseGraph.getEdgesByVertex(s));
        mark.set(s.getID(), true);

        // while number of edges is inferior to number of vertices
        // (see tree definition)
        while (maze.getEdges().size() < maze.getVertices().size() - 1) {
            // Sort current possibles edges and get the first one at each step
            Collections.sort(currentEdges);
            Edge e = currentEdges.getFirst();

            // Check if there is no path between vertices in the edge to prevents cycle
            if (unionFind(e.getVertexA(), e.getVertexB(), parents) == false) {
                // update union find groups
                union(e.getVertexA().getID(), e.getVertexB().getID(), parents);

                // add choosen edge to maze
                maze.addEdge(new Edge(maze.getVertexByID(e.getVertexA().getID()),
                        maze.getVertexByID(e.getVertexB().getID())));

                /**
                 * check wether vertex A or Vertex B is marked (it's Edges is already in
                 * currentEdges) and add it's opposite edges and mark it
                 */
                if (mark.get(e.getVertexA().getID())) {
                    currentEdges.addAll(baseGraph.getEdgesByVertex(e.getVertexB()));
                    mark.set(e.getVertexB().getID(), true);
                } else {
                    currentEdges.addAll(baseGraph.getEdgesByVertex(e.getVertexA()));
                    mark.set(e.getVertexA().getID(), true);
                }
            }
            // delete the choosen edge from the possibility list
            currentEdges.remove(e);
        }
    }

    /**
     * Use the randomized DFS method to create a graph, it's a recursive function
     *
     * @param baseGraph grid graph explored
     * @param maze      maze result
     */
    private void randomDFS(Maze baseGraph, Maze maze) {
        Random rng = new Random(this.seed);
        Vertex currentVertex = baseGraph.getVertices().getFirst();
        ArrayList<Vertex> visitedStack = new ArrayList<>();
        ArrayList<Boolean> mark = new ArrayList<Boolean>();

        for (int i = 0; i < maze.getVertices().size(); i++) {
            mark.add(false);
        }

        while (maze.getEdges().size() < (maze.getVertices().size() - 1)) {
            ArrayList<Vertex> availableNeighbors = new ArrayList<Vertex>();
            mark.set(currentVertex.getID(), true);

            for (Vertex v : currentVertex.getNeighbors()) {
                if (!mark.get(v.getID())) {
                    availableNeighbors.add(v);
                }
            }

            if (availableNeighbors.size() == 0) {
                currentVertex = visitedStack.removeLast();
            } else {
                visitedStack.addLast(currentVertex);
                Vertex nextVertex = availableNeighbors.get(rng.nextInt(availableNeighbors.size()));
                maze.addEdge(
                        new Edge(maze.getVertexByID(currentVertex.getID()), maze.getVertexByID(nextVertex.getID())));
                currentVertex = nextVertex;
                availableNeighbors = null;
            }
        }
    }

    /**
     * generate an imperfect Maze.
     * First step : pick a number between 1/4 numbers of edges and all the edges
     * name numberEdges
     * Second step : make a loop of numberEdges iteration and pick a Edge from
     * gridGraph
     * Third Step : add this Edge to maze
     *
     * @param baseGraph grid base of possible edges
     * @param maze      output maze
     */
    private void imperfect(Maze baseGraph, Maze maze) {
        // get minimum 3/8 of the edge of grid graph and maximum all the edges
        Random rng = new Random(this.seed);
        Integer numberEdges = rng.nextInt((int) (baseGraph.getEdges().size() * (3.0 / 8.0)))
                + (int) (baseGraph.getEdges().size() * (3.0 / 8.0));
        ArrayList<Edge> edgesGridMaze = baseGraph.getEdges();

        for (int m = 0; m < numberEdges; m++) {
            if (edgesGridMaze.size() == 0) {
                break;
            }

            Edge e = edgesGridMaze.get(rng.nextInt(edgesGridMaze.size())); // pick a random Edge in the grid Graph
            edgesGridMaze.remove(e); // removes it from the grid Graph : it makes sure to not pick the same Edge in
                                     // the following iterations

            maze.addEdge(
                    new Edge(maze.getVertexByID(e.getVertexA().getID()), maze.getVertexByID(e.getVertexB().getID())));

            // System.out.println(e);
        }
    }

    /**
     * Create a maze according to a specific method.
     *
     * @return maze : Maze
     * @see Maze
     */
    public Maze makeMaze() {
        // Used to display generation time
        long time = System.currentTimeMillis();

        // Create a basic grid graph and a second graph (maze is the result)
        Maze base = this.makeGridGraph();
        Maze maze = new Maze(this.rows, this.columns);

        switch (this.genMethod) {
            case KRUSKAL:
                this.addRandomWeight(base);
                kruskal(base, maze);
                System.out.println("End of Kruskal Generation");
                break;

            case PRIM:
                this.addRandomWeight(base);
                prim(base, maze, base.getVertices().getFirst());
                System.out.println("End of PRIM generation");
                break;

            case DFS:
                randomDFS(base, maze);
                System.out.println("End of Random DFS generation.");
                break;

            case IMPERFECT:
                imperfect(base, maze);
                System.out.println("End of Imperfect generation.");
                break;
            default:
                maze = base;
                System.out.println("No method has been provied, return a grid maze.");
        }

        System.out.println("Timestamp : " + (System.currentTimeMillis() - time) + "ms");
        return maze;
    }

    @Override
    public String toString() {
        return ("--Graph Generator--" + "\nGeneration Method: " + this.genMethod + "\nSize: "
                + this.columns + "x" + this.rows + "\nseed: " + this.seed);
    }
}