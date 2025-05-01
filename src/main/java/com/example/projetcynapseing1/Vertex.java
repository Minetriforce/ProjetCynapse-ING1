/**
 * <p>
 * Class Vertex is used in a Graph Object.
 * For a maze, it represent a case if a grid (intersection).
 * </p>
 * 
 * <p>
 * Exemple usage : 
 * <pre>
 * Vertex A = new Vertex(0,0,0);
 * Vertex B = new Vertex(1,1,0);
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
    private Integer PosX;
    private Integer PosY;

    /**
     * ID of the Vertex is used to select a specific vertex.
     */
    private Integer ID;
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
     * @param PosX
     * @param PosY
     * @param ID
     * @throws Exception it can be because the position is negative or because ID is
     *                   negative.
     */
    public Vertex(Integer PosX, Integer PosY, Integer ID) throws Exception {
        if (PosX < 0 || PosY < 0) {
            throw new Exception("Invalid Position");
        }
        if (ID < 0) {
            throw new Exception("Invalid ID");
        }
        this.PosX = PosX;
        this.PosY = PosY;
        this.ID = ID;
        this.neighbors = new ArrayList<Vertex>();
    }

    // Getters
    public Integer getX() {
        return this.PosX;
    }

    public Integer getY() {
        return this.PosY;
    }

    public Integer getID() {
        return this.ID;
    }

    public ArrayList<Vertex> getNeighbors() {
        return this.neighbors;
    }

    /**
     * <p>
     * Add a Vertex as a neighbor of the current Vertex
     * </p>
     * 
     * @param A is a valid vertex
     * @return boolean
     */
    public boolean addNeighbor(Vertex A) {
        if (A == null) {
            System.out.println("can't add a null object");
            return false;
        } else if (this.equals(A)) {
            System.out.println("can't add self a s a neigbor");
            return false;
        } else if (this.neighbors.contains(A)) {
            System.out.println("Vertex already in list");
            return false;
        }
        return this.neighbors.add(A);
    }

    /**
     * <p>
     * Remove a vertex of the neighbor list
     * </p>
     * 
     * @param A is a valid Vertex already in or not in neigbors list
     * @return boolean
     */
    public boolean removeNeighbor(Vertex A) {
        return this.neighbors.remove(A);
    }

    /**
     * <p>
     * used to test if a Vertex is a neigbor of the current vertex
     * </p>
     * 
     * @param Vertex is in the same graph as the current object
     * @return boolean
     * @since 1.0
     */
    public Boolean isNeighbor(Vertex V) {
        if (this.neighbors.contains(V)) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * Ovveride method of super class "Object" to display current Vertex as a string
     * with it's ID and position in graph.
     * </p>
     * 
     * @return the amount of health hero has after attack
     * @since 1.0
     */
    @Override
    public String toString() {
        return ("VertexID:" + this.ID + ";PosX:" + this.PosX + ";PosY:" + this.PosY);
    }
}
