/**
 * <p>
 * Class Vertex is used in a Graph Object.
 * For a maze, it represents a case if a grid (intersection).
 * </p>
 * 
 * <p>
 * Exemple usage : 
 * <pre>
 * Vertex a = new Vertex(0,0,0);
 * Vertex b = new Vertex(1,1,0);
 * </pre>
 * </p>
 * @author Bari-joris
 * @version 1.0
 */

package com.example.projetcynapseing1;

import java.util.ArrayList;

public class Vertex {

    /**
     * The position of the vertex on the graph, starting from (0,0) to (n,m).
     * X is horizontal, Y is vertical
     */
    private Integer x;
    private Integer y;

    /**
     * id of the Vertex is used to select a specific vertex.
     */
    private Integer id;
    /**
     * Neigbors contain all neighbors of vertex (other vertex linked to it).
     * It has size of min = 0, max = 4
     */
    private ArrayList<Vertex> neighbors;

    /**
     * <p>
     * Constructor of Vertex Objet
     * </p>
     * 
     * @param x
     * @param y
     * @param id
     * @throws Exception it can be because the position is negative or because id is
     *                   negative.
     */
    public Vertex(Integer x, Integer y, Integer id) throws Exception {
        if (x < 0 || y < 0) {
            throw new Exception("Invalid Position");
        }
        if (id < 0) {
            throw new Exception("Invalid id");
        }
        this.x = x;
        this.y = y;
        this.id = id;
        this.neighbors = new ArrayList<Vertex>();
    }

    // Getters
    public Integer getX() {
        return this.x;
    }

    public Integer getY() {
        return this.y;
    }

    public Integer getId() {
        return this.id;
    }

    public ArrayList<Vertex> getNeighbors() {
        return this.neighbors;
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
            System.out.println("Vertex already in list");
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
        if (this.neighbors.contains(v)) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * Ovveride method of super class "Object" to display current Vertex as a string
     * with it's id and position in graph.
     * </p>
     * 
     * @return the amount of health hero has after attack
     * @since 1.0
     */
    @Override
    public String toString() {
        return ("Vertexid:" + this.id + ";x:" + this.x + ";y:" + this.y);
    }
}
