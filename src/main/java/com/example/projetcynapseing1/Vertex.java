package com.example.projetcynapseing1;

import java.io.Serializable;
import java.util.ArrayList;

enum VertexState {
    DEFAULT, // not visited
    VISITED, // explored
    SOLUTION, // part of final path
    SELECTEDDEL, // selected path to del
    SELECTEDADD,
    FIRSTSELECTED // first vertex
}

/**
 * Class Vertex is used in a Graph Object.
 * For a maze, it represents a case if a grid (intersection).
 * 
 * Exemple usage :
 * 
 * <pre>
 * Vertex a = new Vertex(0, 0, 0);
 * Vertex b = new Vertex(1, 1, 0);
 * </pre>
 * 
 * @author Bari-joris
 * @version 1.0
 */
public class Vertex implements Serializable {

    /**
     * The position of the vertex on the graph, starting from (0,0) to (n,m).
     * X is horizontal
     */
    private final Integer x;

    /**
     * Y vertical position of the vertex
     */
    private final Integer y;

    /**
     * Neigbors contain all neighbors of vertex (other vertex linked to it).
     * It has size of min = 0, max = 4
     */
    private ArrayList<Vertex> neighbors;

    /**
     * unique id of the vertex. used as a name
     */
    private final Integer id;

    /**
     * State of the vertex in the final solution, can be sets to default, visited or
     * in solution
     */
    private VertexState state = VertexState.DEFAULT;

    /**
     * Constructor of Vertex Object
     * 
     * @param x  horizontal coordinate
     * @param y  vertical coordinate
     * @param id unique ID of vertex
     * @throws Exception it can be because the position is negative or because id is
     *                   negative.
     */
    public Vertex(Integer x, Integer y, Integer id) throws Exception {
        if (x < 0 || y < 0) {
            throw new Exception("Invalid Position");
        }
        this.x = x;
        this.y = y;
        this.id = id;
        this.neighbors = new ArrayList<Vertex>();
    }

    // Getters

    /**
     * Get Y coordinate of Vertex in Graph
     * 
     * @return integer between 0 and columns
     */
    public Integer getX() {
        return this.x;
    }

    /**
     * Get X coordinate of Vertex in graph
     * 
     * @return integer between 0 and rows
     */
    public Integer getY() {
        return this.y;
    }

    /**
     * Return the list of neighbors of this Vertex
     * 
     * @return Vertex List
     */
    public ArrayList<Vertex> getNeighbors() {
        return this.neighbors;
    }

    /**
     * return the unique id of the vertex
     * 
     * @return Integer between 0 and0 (rows+columns)-1
     */
    public Integer getID() {
        return this.id;
    }

    /**
     * Get the current state of the vertex (used for coloring in GUI)
     *
     * @return the state of the vertex
     */
    public VertexState getState() {
        return this.state;
    }

    /**
     * Set a new state for the vertex
     *
     * @param state the new state to set
     */
    public void setState(VertexState state) {
        this.state = state;
    }

    /**
     * <p>
     * Add a Vertex as a neighbor of the current Vertex
     * </p>
     * 
     * @param a is a valid vertex
     * @return boolean
     */
    public boolean addNeighbor(Vertex a) {
        if (a == null) {
            System.out.println("can't add a null object");
            return false;
        } else if (this.equals(a)) {
            System.out.println("can't add self a s a neigbor");
            return false;
        } else if (this.neighbors.contains(a)) {
            return false;
        }
        return this.neighbors.add(a);
    }

    /**
     * <p>
     * Remove a vertex of the neighbor list
     * </p>
     * 
     * @param a is a valid Vertex already in or not in neigbors list
     * @return boolean
     */
    public boolean removeNeighbor(Vertex a) {
        return this.neighbors.remove(a);
    }

    /**
     * <p>
     * used to test if a Vertex is a neigbor of the current vertex
     * </p>
     * 
     * @param v is in the same graph as the current object
     * @return boolean
     * @since 1.0
     */
    public Boolean isNeighbor(Vertex v) {
        return this.neighbors.contains(v);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex) {
            Vertex v = (Vertex) obj;
            return x == v.getX() && y == v.getY() && id == v.getID();
        }
        return false;
    }

    /**
     * <p>
     * Ovveride method of super class "Object" to display current Vertex as a string
     * with it's id and position in graph.
     * </p>
     * 
     * @return Vertexid: id; x: x; y: y
     * @since 1.0
     */
    @Override
    public String toString() {
        return ("Vertexid:" + this.id + ";x:" + this.x + ";y:" + this.y);
    }

}
