package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * <p>
 * Class Solver is used to solve mazes with a specific method and a timestep.
 * <p/>
 * 
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
    public Solver(MethodName.SolveMethodName m, float t){
        method = m;
        timeStep = t >= 0 ? t : 0f;
    }
    /**
     * constructor of Solver object
     */
    public Solver(MethodName.SolveMethodName m){
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
        timeStep = t >= 0 ? t : 0f;
    }

    /**
     * solve the maze with the A* algorithm
     * @param g: maze graph
     * @param start: starting vertex
     * @param end: ending vertex
     * @param t: type of printing
     * @return parents: array of parents of each vertex in the path
     */
    public int[] solveAstar(Graph g, Vertex start, Vertex end, MethodName.Type t){
        ArrayList<Vertex> vertices = g.getVertices();
        int n = g.getVertices().size();
        int si = vertices.indexOf(start);
        int ei = vertices.indexOf(end);

        boolean[] seen = new boolean[n];
        int[] parents = new int[n];
        int[] distances = new int[n];
        for (int i = 0; i < n; i++){
            seen[i] = false;
            parents[i] = i;
            distances[i] = Integer.MAX_VALUE;
        }
        PriorityQueue<Integer> toVisit = new PriorityQueue<Integer>(Comparator.comparingInt(i -> distances[parents[i]] + distance(vertices.get(i), end)));

        distances[si] = 0;
        toVisit.add(si);
        while (parents[ei] == ei){
            if (toVisit.isEmpty()){
                return parents;
            }
            int ui = toVisit.poll();

            if (!seen[ui]){
                for (Vertex v : vertices.get(ui).getNeighbors()){
                    int vi = vertices.indexOf(v);
                    if (!seen[vi]){
                        if (distances[vi] > distances[ui] + 1){
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

    /**
     * @param a: vertex
     * @param b: vertex
     * @return Manhattan distance between 2 vertices
     */
    public static int distance(Vertex a, Vertex b){
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * @return method and time step in a String format
     */
    @Override
    public String toString(){
        return "Solver: " + method.toString() + "\ntime step: " + String.valueOf(timeStep) + "s";
    }
}
