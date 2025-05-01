/**
 * <p>
 * Edge is an entity we use to link multiple Vertices together, with a weight
 * for specifics cases.
 * For vertex class, please refer to
 * {@link com.example.projetcynapseing1.Vertex}
 * <p>
 * 
 * <p>
 * Exemple usage:
 * <pre>
 * Vertex A = new Vertex(0,0,0);
 * Vertex B = new Vertex(1,0,1);
 * 
 * Edge AB = new Edge(A, B);
 * 
 * //To add weight to the edge
 * AB.setWeight(1);
 * </pre>
 * </p>
 * @author Bari-joris
 * @version 1.0
 */

package com.example.projetcynapseing1;

public class Edge implements Comparable<Edge> {
    /**
     * weight is used in generation methods. Keeps value 0 while not beeing used.
     */
    private Integer weight = 0;
    /**
     * Vertices linked by the Edge
     */
    private Vertex VertexA;
    private Vertex VertexB;

    public Edge(Vertex VertexA, Vertex VertexB) {
        // set Values
        this.VertexA = VertexA;
        this.VertexB = VertexB;

        // cross add of Vertices in their neighbors list
        // It can result as an error if user add multiple edges to the same two Vertices
        if (!VertexA.addNeighbor(VertexB) || !VertexB.addNeighbor(VertexA)) {
            System.out.println("--Edge Class--");
            System.out.println("can't add neigbors, maybe it's because VertexID:" + VertexA.getID() + " and VertexID:"
                    + VertexB.getID() + " are already linked by an edge");
        }
    }

    // getters
    public Vertex getVertexA() {
        return this.VertexA;
    }

    public Vertex getVertexB() {
        return this.VertexB;
    }

    public Integer getWeight() {
        return this.weight;
    }

    /**
     * <p>
     * Function use to set weight of the current edge
     * </p>
     * 
     * @param weight of the edge
     * @throws Exception because edge weight can't be negative in generation methods
     */
    public void setWeight(Integer weight) throws Exception {
        if (weight < 0) {
            throw new Exception("weight can't be negative");
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
            return (this.VertexA.getID() + "-" + this.weight + "w-" + this.VertexB.getID());
        }
        return (this.VertexA.getID() + "-" + this.VertexB.getID());
    }

    @Override
    public int compareTo(Edge E) {
        return Integer.compare(this.weight, E.getWeight());
    }
}
