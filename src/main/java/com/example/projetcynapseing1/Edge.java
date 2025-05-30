package com.example.projetcynapseing1;

import java.io.Serializable;

/**
 * <p>
 * Edge is an entity we use to link multiple vertices together, with a weight
 * for specifics cases.
 * For vertex class, please refer to
 * {@link com.example.projetcynapseing1.Vertex}
 * 
 * Exemple usage:
 * 
 * <pre>
 * Vertex a = new Vertex(0, 0, 0);
 * Vertex b = new Vertex(1, 0, 1);
 * 
 * Edge ab = new Edge(a, b);
 * 
 * // To add weight to the edge
 * ab.setWeight(1);
 * </pre>
 * 
 * @author Bari-joris
 */
public class Edge implements Comparable<Edge>, Serializable {
    /**
     * weight is used in generation methods. Keeps value 0 while not beeing used.
     */
    private Integer weight = 0;

    /**
     * Left vertex of the Edge
     */
    private Vertex vertexA;

    /**
     * Right vertex of the Edge
     */
    private Vertex vertexB;

    /**
     * Constructor of class Edge
     * 
     * @param vA First Vertex
     * @param vB Second Vertex
     */
    public Edge(Vertex vA, Vertex vB) {
        // set Values
        this.vertexA = vA;
        this.vertexB = vB;

        // cross add of Vertices in their neighbors list
        // It can result as an error if user add multiple edges to the same two Vertices
        if (!vA.addNeighbor(vB) || !vB.addNeighbor(vA)) {
            System.out.println("--Edge Class--");
            System.out.println("Error while adding neigbors to Vertices");
            System.out.println("Vertex A :" + vA + ", Vertex B :" + vB);
        }
    }

    /**
     * This constructor allows creating an edge without modifying the neighbor
     * lists of the involved vertices.
     *
     * @param vA               the first vertex of the edge
     * @param vB               the second vertex of the edge
     * @param skipAddNeighbors if true, neighbor lists of the vertices will not be
     *                         updated
     */
    public Edge(Vertex vA, Vertex vB, boolean skipAddNeighbors) {
        this.vertexA = vA;
        this.vertexB = vB;
    }

    /**
     * Return vertex A in edge
     * 
     * @return vertexA
     */
    public Vertex getVertexA() {
        return this.vertexA;
    }

    /**
     * Return Vertex B in Edge
     * 
     * @return Vertex
     */
    public Vertex getVertexB() {
        return this.vertexB;
    }

    /**
     * get weight of Edge (Integer between 0 and 100)
     * 
     * @return Vertex
     */
    public Integer getWeight() {
        return this.weight;
    }

    /**
     * <p>
     * Function use to set weight of the current edge
     * </p>
     * 
     * @param weight of the edge
     * @throws IllegalArgumentException because edge weight can't be negative in
     *                                  generation methods
     */
    public void setWeight(Integer weight) throws IllegalArgumentException {
        if (weight < 0) {
            throw new IllegalArgumentException("weight can't be negative");
        }
        this.weight = weight;
    }

    /**
     * <p>
     * Ovveride super class Method to display both Vertices linked
     * </p>
     * 
     * @return Edge object as a string according to weight
     * @since 1.0
     */
    @Override
    public String toString() {
        if (weight != 0) {
            return ("(" + vertexA.getX() + ", " + vertexA.getY() + ") - " + weight + "w - (" + vertexB.getX() + ", "
                    + vertexB.getY() + ")");
        }
        return ("(" + vertexA.getX() + ", " + vertexA.getY() + ") - (" + vertexB.getX() + ", " + vertexB.getY() + ")");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge e = (Edge) obj;
            return (this.vertexA.equals(e.getVertexA()) && this.vertexB.equals(e.getVertexB())
                    || this.vertexA.equals(e.getVertexB()) && this.vertexB.equals(e.getVertexA()));
        }
        return false;
    }
    @Override
    public int hashCode() {
        return vertexA.hashCode() + vertexB.hashCode();
    }

    @Override
    public int compareTo(Edge e) {
        return Integer.compare(this.weight, e.getWeight());
    }
}
