/**
 * The Graph class represents a graph data structure consisting of vertices and edges.
 * It provides methods to manage and manipulate the graph, including adding vertices
 * and edges, and retrieving the list of vertices and edges.
 * 
 * <p>
 * The graph is implemented using an adjacency list representation, where each vertex
 * maintains a list of its neighboring vertices.
 * </p>
 * 
 * 
 * <p>
 * Example usage:
 * <pre>
 * Graph graph = new Graph();
 * Vertex v1 = new Vertex("A");
 * Vertex v2 = new Vertex("B");
 * graph.addVertex(v1);
 * graph.addVertex(v2);
 * Edge edge = new Edge(v1, v2);
 * graph.addEdge(edge);
 * </pre>
 * </p>
 * 
 * @author Bari-joris
 * @version 1.0
 */
package com.example.projetcynapseing1;

import java.util.ArrayList;

public class Graph {
    /**
     * List of unique vertices in graph
     */
    private ArrayList<Vertex> vertices;
    /**
     * List of unique edges in graph
     */
    private ArrayList<Edge> edges;

    public Graph() {
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();
    }

    // Getters
    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    /**
     * add a new Vertex to graph
     * 
     * @param v is a valid and unique Vertex
     * @return boolean
     */
    public boolean addVertex(Vertex v) {
        return this.vertices.add(v);
    }

    /**
     * add a new Edge to graph
     * 
     * @param e is a valid and unique Edge
     * @return
     */
    public boolean addEdge(Edge e) {
        e.getVertexA().getNeighbors().add(e.getVertexB());
        e.getVertexB().getNeighbors().add(e.getVertexA());
        return this.edges.add(e);
    }

}
