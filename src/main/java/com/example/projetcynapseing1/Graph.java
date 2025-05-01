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
     * List of unique Vertices in graph
     */
    private ArrayList<Vertex> Vertices;
    /**
     * List of unique Edges in graph
     */
    private ArrayList<Edge> Edges;

    public Graph() {
        this.Vertices = new ArrayList<Vertex>();
        this.Edges = new ArrayList<Edge>();
    }

    // Getters
    public ArrayList<Vertex> getVertices() {
        return this.Vertices;
    }

    public ArrayList<Edge> getEdges() {
        return this.Edges;
    }

    /**
     * add a new Vertex to graph
     * 
     * @param V is a valid and unique Vertex
     * @return boolean
     */
    public boolean addVertex(Vertex V) {
        return this.Vertices.add(V);
    }

    /**
     * add a new Edge to graph
     * 
     * @param E is a valid and unique Edge
     * @return
     */
    public boolean addEdge(Edge E) {
        E.getVertexA().getNeighbors().add(E.getVertexB());
        E.getVertexB().getNeighbors().add(E.getVertexA());
        return this.Edges.add(E);
    }

}
