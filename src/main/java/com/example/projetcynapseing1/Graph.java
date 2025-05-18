package com.example.projetcynapseing1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The Graph class represents a graph data structure consisting of vertices and
 * edges.
 * It provides methods to manage and manipulate the graph, including adding
 * vertices
 * and edges, and retrieving the list of vertices and edges.
 * 
 * The graph is implemented using an adjacency list representation, where each
 * vertex
 * maintains a list of its neighboring vertices.
 * 
 * 
 * Example usage:
 * 
 * <pre>
 * Graph graph = new Graph();
 * Vertex v1 = new Vertex("A");
 * Vertex v2 = new Vertex("B");
 * graph.addVertex(v1);
 * graph.addVertex(v2);
 * Edge edge = new Edge(v1, v2);
 * graph.addEdge(edge);
 * </pre>
 * 
 * @author Bari-joris
 */
public class Graph implements Serializable {
    /**
     * List of unique vertices in graph
     */
    private ArrayList<Vertex> vertices;
    /**
     * List of unique edges in graph
     */
    private ArrayList<Edge> edges;

    /**
     * Constructor of object Graph.
     * Initialize Arrays of vertices and edges
     */
    public Graph() {
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();
    }

    // Getters
    /**
     * Return all the vertices in the current Graph
     * 
     * @return this.vertices : list of all vertices in this graph
     * @see Vertex
     */
    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    /**
     * Return all the Edges from the Graph
     * 
     * @return this.edges : current list of edges in this graph
     * @see Edge
     */
    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    /**
     * Return a Vertex Object according to it's ID
     * 
     * @param ID integer between 0 and n to identify vertex in graph
     * @return Vertex
     */
    public Vertex getVertexByID(Integer ID) {
        return vertices.get(ID);
    }

    /**
     * Return a list of edges connected to vertex v
     * 
     * @param v Vertex to get Edges from
     * @return edges : list of edges
     */
    public Set<Edge> getEdgesByVertex(Vertex v) {
        Set<Edge> edges = new HashSet<>();

        for (Edge e : this.edges) {
            if (e.getVertexA().equals(v) || e.getVertexB().equals(v)) {
                edges.add(e);
            }
        }
        return edges;
    }

    /**
     * get an Edge instance by it's two vertices
     * 
     * @param u vertex
     * @param v vertex
     * @return edge: the edge connecting u and v
     */
    public Edge getEdgeByVertices(Vertex u, Vertex v) {
        for (Edge e : this.edges) {
            if ((e.getVertexA().equals(u) && e.getVertexB().equals(v))
                    || (e.getVertexA().equals(v) && e.getVertexB().equals(u))) {
                return e;
            }
        }
        return null;
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
     * @return Boolean : if the edge was added
     */
    public boolean addEdge(Edge e) {
        boolean added = this.edges.add(e);
        if (added) {
            Vertex a = e.getVertexA();
            Vertex b = e.getVertexB();
            a.addNeighbor(b);
        }
        return added;
    }



    /**
     * remove an edge from the list of current Edges
     * 
     * @param e edge to remove
     * @return deleting was succesfull or not
     */
    public boolean removeEdge(Edge e) {
        boolean removed = this.edges.remove(e);
        if (removed) {
            Vertex a = e.getVertexA();
            Vertex b = e.getVertexB();
            a.removeNeighbor(b);
            b.removeNeighbor(a);
        }
        return removed;
    }





    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("--Graph--\n");
        sb.append("Vertices:\n");
        for (Vertex v : vertices) {
            sb.append("ID: ").append(v.getID())
                    .append(" (").append(v.getX()).append(", ").append(v.getY()).append(")")
                    .append(" -> Neighbors: ");

            ArrayList<Vertex> neighbors = v.getNeighbors();
            for (Vertex neighbor : neighbors) {
                sb.append(neighbor.getID()).append(" ");
            }
            sb.append("\n");
        }

        sb.append("\nEdges:\n");
        for (Edge e : edges) {
            sb.append("Edge from ")
                    .append(e.getVertexA().getID())
                    .append(" to ")
                    .append(e.getVertexB().getID())
                    .append("\n");
        }
        return sb.toString();
    }
}
