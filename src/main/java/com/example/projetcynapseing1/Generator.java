package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * Generator class is used to generate a maze according to differents
 * parameters.
 * This class use the Graph class (see
 * {@link com.example.projetcynapseing1.Graph})
 * 
 * @author Bari-joris
 */
public class Generator {
    private FXController fxController;
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
        if (rows < 0 || colums < 0 || rows == null || colums == null) {
            throw new IllegalArgumentException("rows or column can't be negative/null");
        } else if (seed < 0) {
            throw new IllegalArgumentException("seed can't be negative");
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
    private Maze makeGridGraph() {
        Maze G = new Maze(this.rows, this.columns, null);

        // link every vertices to others in order to make a grid
        ArrayList<Vertex> ListVertex = G.getVertices();
        for (Vertex vertex : ListVertex) {
            if ((vertex.getX() + 1) != this.columns) {
                G.addEdge(new Edge(vertex, G.getVertexByIDVertex(vertex.getID() + 1)));
            }
            if (vertex.getY() != 0) {
                G.addEdge(new Edge(vertex, G.getVertexByIDVertex(vertex.getID() - this.columns)));
            }
        }
        return G;
    }

    /**
     * add a random weight to every Edge in graph using a random number generator
     * 
     * @param G
     */
    private void addRandomWeight(Graph G) {
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
     * This function is used to know if a path exists between s1 and s2 in graph,
     * using Depth-first search (DFS)
     * 
     * @param graph
     * @param s1    (Vertex)
     * @param s2
     * @return Boolean (true: a path exists, false: there's no path)
     * @see Vertex
     * @see Maze
     */
    private Boolean DFScheck(Maze graph, Vertex s1, Vertex s2) {
        ArrayList<Boolean> mark = new ArrayList<Boolean>();
        for (int i = 0; i < graph.getVertices().size(); i++) {
            mark.add(false);
        }
        DFSRec(graph, s1, mark);
        return (mark.get(s2.getID()));
    }

    /**
     * Recursive Depth-first Search.
     * 
     * @param graph
     * @param s
     * @param mark
     * @return mark
     */
    private ArrayList<Boolean> DFSRec(Maze graph, Vertex s, ArrayList<Boolean> mark) {
        mark.set(s.getID(), true);
        for (Vertex neighbor : s.getNeighbors()) {
            if (mark.get(neighbor.getID()) == false) {
                DFSRec(graph, neighbor, mark);
            }
        }
        return mark;
    }

    /**
     * Apply Kruskal algorithm to baseGraph and uptade the minimum Spanning Tree
     * (MST) in maze
     * 
     * @param baseGraph
     * @param maze
     */
    private void Kruskal(Maze baseGraph, Maze maze) {
        Collections.sort(baseGraph.getEdges());
        for (Edge edge : baseGraph.getEdges()) {
            if (DFScheck(maze, maze.getVertexByIDVertex(edge.getVertexA().getID()),
                    maze.getVertexByIDVertex(edge.getVertexB().getID())) == false) {
                maze.addEdge(new Edge(maze.getVertexByIDVertex(edge.getVertexA().getID()),
                        maze.getVertexByIDVertex(edge.getVertexB().getID())));
            }
        }
    }

    /**
     * Create a maze according to Prim's algorithm
     * 
     * @param baseGraph
     * @param maze
     * @param s
     */
    private void Prim(Maze baseGraph, Maze maze, Vertex s) {
        ArrayList<Edge> currentEdges = new ArrayList<Edge>(); // create list of current Edges

        // Create a mark list for all vertices in maze
        ArrayList<Boolean> mark = new ArrayList<Boolean>();
        for (int i = 0; i < baseGraph.getVertices().size(); i++) {
            mark.add(false);
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
            if (DFScheck(maze, maze.getVertexByIDVertex(e.getVertexA().getID()),
                    maze.getVertexByIDVertex(e.getVertexB().getID())) == false) {
                // add choosen edge to maze
                maze.addEdge(new Edge(maze.getVertexByIDVertex(e.getVertexA().getID()),
                        maze.getVertexByIDVertex(e.getVertexB().getID())));

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
     * @param baseGraph     grid graph explored
     * @param maze          maze result
     * @param visitedStack  stack of vertex already visited, in case the algorithm
     *                      have to go back
     * @param currentVertex vertex used in this iteration
     * @param mark          mark list of all vertices in maze
     * @param randomGen     random number generator, it ensures us to keep the same
     *                      maze if we send the same seed
     */
    private void RandomDFS(Graph baseGraph, Graph maze, Stack<Vertex> visitedStack, Vertex currentVertex,
            ArrayList<Boolean> mark, Random randomGen) {
        mark.set(currentVertex.getID(), true);
        ArrayList<Vertex> availableNeighbors = new ArrayList<Vertex>();

        for (Vertex v : currentVertex.getNeighbors()) {
            if (mark.get(v.getID()) == false) {
                availableNeighbors.add(v);
            }
        }

        if ((maze.getEdges().size() == (maze.getVertices().size() - 1))
                || (availableNeighbors.size() == 0 && visitedStack.size() == 0)) {
            System.out.println("End of RandomDFS generation");
        } else if (availableNeighbors.size() == 0) {
            Vertex previousVertex = visitedStack.pop();
            RandomDFS(baseGraph, maze, visitedStack, previousVertex, mark, randomGen);
        } else {
            Vertex nextVertex = availableNeighbors.get(randomGen.nextInt(availableNeighbors.size()));
            visitedStack.push(currentVertex);
            maze.addEdge(new Edge(maze.getVertexByIDVertex(currentVertex.getID()),
                    maze.getVertexByIDVertex(nextVertex.getID())));
            RandomDFS(baseGraph, maze, visitedStack, nextVertex, mark, randomGen);
        }
    }

    /**
     * Create a maze according to a specific method.
     * 
     * @return maze : Maze
     * @see Maze
     */
    public Maze makeMaze() throws Exception {

        // In case seed is null, generate a random seed
        if (this.seed == null) {
            System.out.println("Warning : seed is null, generating random seed");
            this.seed = new Random().nextInt(100000);
            System.out.println("Seed generated : " + this.seed);
        }

        // Used to display generation time
        long time = System.currentTimeMillis();

        // Create a basic grid graph and a second graph (maze is the result)
        Maze base = this.makeGridGraph();
        Maze maze = new Maze(this.rows, this.columns, this.genMethod);

        // add all vertices created in base to maze (reduce work)
        for (Vertex V : base.getVertices()) {
            try {
                Vertex newV = new Vertex(V.getX(), V.getY(), V.getID());
                maze.addVertex(newV);
            } catch (Exception e) {
                System.err.println("--MAZE GENERATOR ERROR--");
                System.err.println("Error while creating new Vertex for maze");
            }
        }

        switch (this.genMethod) {
            case KRUSKAL:
                this.addRandomWeight(base);
                Kruskal(base, maze);
                base = null;
                System.gc();
                time = System.currentTimeMillis() - time;
                System.out.println("End of Kruskal Generation");
                break;

            case PRIM:
                this.addRandomWeight(base);
                Prim(base, maze, base.getVertices().getFirst());
                base = null;
                System.gc();
                System.out.println("End of PRIM generation");
                break;

            case DFS:
                Random rng = new Random(this.seed);
                ArrayList<Boolean> mark = new ArrayList<Boolean>();
                for (int i = 0; i < maze.getVertices().size(); i++) {
                    mark.add(false);
                }

                if (base.getVertices().getFirst().getNeighbors().size() == 0) {
                    System.err.println("Error in DFS : " + base.getVertices().getFirst() + " has no neighbors");
                } else {
                    RandomDFS(base, maze, new Stack<Vertex>(), base.getVertices().getFirst(), mark, rng);
                }
                break;
        }

        System.out.println("Timestamp : " + time + "ms");
        return maze;
    }

    @Override
    public String toString() {
        return ("--Graph Generator--" + "\nGeneration Method: " + this.genMethod + "\nSize: "
                + this.columns + "x" + this.rows + "\nseed: " + this.seed);
    }
}