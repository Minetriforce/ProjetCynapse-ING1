package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Class Solver is used to solve mazes with a specific method and a timestep.
 * @author Junjie
 */
public class Solver {
    // name of the method used to solve the maze
    private MethodName.SolveMethodName method;
    // time step used in the solving process, must be >= 0
    private float timeStep;

    /**
     * constructor of Solver object
     * @param m: method's name
     * @param t: time step
     */
    public Solver(MethodName.SolveMethodName m, float t) {
        method = m;
        timeStep = (t >= 0) ? t : 0f;
    }

    /**
     * constructor of Solver object
     * @param m: Solving method name
     */
    public Solver(MethodName.SolveMethodName m) {
        this(m, 0f);
    }

    /**
     * getter of method
     * @return method
     */
    public MethodName.SolveMethodName getMethod() {
        return method;
    }

    /**
     * getter of timeStep
     * @return timeStep
     */
    public float getTimeStep() {
        return timeStep;
    }

    /**
     * setter of method
     * @param m: new method
     */
    public void setMethod(MethodName.SolveMethodName m) {
        method = m;
    }

    /**
     * setter of timeStep
     * @param t: new timeStep
     */
    public void setTimeStep(float t) {
        timeStep = (t >= 0) ? t : 0f;
    }

    /**
     * solve the maze with the A* algorithm
     * @param m: maze graph
     * @param start: starting vertex
     * @param end: ending vertex
     * @param t: type of printing
     * @return parents: array of parents of each vertex in the path
     */
    public int[] solveAstar(Maze m, Vertex start, Vertex end, MethodName.Type t) {
        ArrayList<Vertex> vertices = m.getVertices();
        int n = m.getNbLines() * m.getNbColumns();
        int si = start.getID();
        int ei = start.getID();

        boolean[] seen = new boolean[n];
        int[] parents = new int[n];
        int[] distances = new int[n];
        for (int i = 0; i < n; i++) {
            seen[i] = false;
            parents[i] = i;
            distances[i] = Integer.MAX_VALUE;
        }
        PriorityQueue<Integer> toVisit = new PriorityQueue<Integer>(
            Comparator.comparingInt(i -> distances[parents[i]] + distance(vertices.get(i), end)));

        distances[si] = 0;
        toVisit.add(si);
        int ui;
        int vi;

        while (parents[ei] == ei) {
            if (toVisit.isEmpty()) {
                return parents;
            }

            ui = toVisit.poll();
            if (!seen[ui]) {
                for (Vertex v : vertices.get(ui).getNeighbors()) {
                    vi = v.getID();
                    if (!seen[vi]) {
                        if (distances[vi] > distances[ui] + 1) {
                            parents[vi] = ui;
                            distances[vi] = distances[ui] + 1;
                            toVisit.add(vi);
                        }
                    }
                }

                seen[ui] = true;
            }
        }

        return parents;
    }

    public int[] solveRightHand(Maze m, Vertex start, Vertex end, MethodName.Type t){
        ArrayList<Vertex> vertices = m.getVertices();
        int n = m.getNbLines() * m.getNbColumns();
        int si = start.getID();
        int ei = end.getID();
        int[] directions = {1, m.getNbColumns(), -1, -m.getNbColumns()};

        boolean[] seen = new boolean[n];
        int[] parents = new int[n];
        for (int i = 0; i < n; i++){
            seen[i] = false;
            parents[i] = i;
        }

        Stack<Integer> toVisit = new Stack<>();
        int ui;
        int vi;
        int di = 0;

        seen[si] = true;
        for (Vertex v: vertices.get(si).getNeighbors()){
            vi = v.getID();
            if (si + directions[di] == vi){
                parents[vi] = si;
                toVisit.push(vi);
            }
        }

        while (!seen[ei]){
            if (toVisit.isEmpty()){
                return parents;
            }
            
            ui = toVisit.pop();
            if (seen[ui]){
                return parents;
            }
            di = ui - parents[ui];
            for (int i = 0; i < 4; i++){
                if (di == directions[i]){
                    di = (i + 2) % 4;
                    break;
                }
            }

            System.out.println("Sommet départ: " + ui);
            for (int i = 1; i < 4; i++){
                di = (di + 10) % 4;
                System.out.println("Direction: " + di);
                for (Vertex v: vertices.get(ui).getNeighbors()){
                    vi = v.getID();
                    if ((ui + directions[di] == vi) && (!seen[vi])){
                        parents[vi] = ui;
                        toVisit.push(vi);
                        System.out.println("Sommet: " + vi);
                    }
                }
            }

            seen[ui] = true;
        }

        for (int i = 0; i < n; i++){
            if (!seen[i]){
                parents[i] = i;
            }
        }

        return parents;
    }

    /**
     * distance
     * @param a: First vertex
     * @param b: Second vertex
     * @return Manhattan distance between 2 vertices
     */
    public static int distance(Vertex a, Vertex b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * index of the path without other information
     * @param m: maze graph
     * @param end: ending vertex
     * @param parents: array of parents of each vertex in the path
     * @return path: list of index (similiar to parents)
     */
    public static int[] pathIndex(Maze m, Vertex end, int[] parents) {
        ArrayList<Vertex> vertices = m.getVertices();
        int n = m.getVertices().size();
        int[] solution = new int[n];
        for (int i = 0; i < n; i++) {
            solution[i] = i;
        }

        int i = vertices.indexOf(end);
        while (parents[i] != i) {
            solution[i] = parents[i];
            i = parents[i];
        }

        return solution;
    }

    /**
     * get the path from start to end
     * @param m: maze graph
     * @param end: ending vertex
     * @param parents: array of parents of each vertex in the path
     * @return path: list of vertices in the path
     */
    public static ArrayList<Vertex> pathVertex(Maze m, Vertex end, int[] parents){
        ArrayList<Vertex> path = new ArrayList<>();
        ArrayList<Vertex> vertices = m.getVertices();

        int i = vertices.indexOf(end);

        path.add(0, end);
        while (parents[i] != i){
            path.add(0, vertices.get(parents[i]));
            i = parents[i];
        }

        return path;
    }

    /**
     * get the path from start to end
     * @param m: maze graph
     * @param end: ending vertex
     * @param parents: array of parents of each vertex in the path
     * @return path: list of edges in the path
     */
    public static ArrayList<Edge> pathEdge(Maze m, Vertex end, int[] parents){
        ArrayList<Edge> path = new ArrayList<>();
        ArrayList<Vertex> vertices = m.getVertices();

        int i = vertices.indexOf(end);

        while (parents[i] != i){
            path.add(0, m.getEdgeByVertices(vertices.get(parents[i]), vertices.get(i)));
            i = parents[i];
        }

        return path;
    }

    /**
     * @return method and time step in a String format
     */
    @Override
    public String toString() {
        return "Solver: " + method.toString() + "\ntime step: " + String.valueOf(timeStep) + "s";
    }
}
