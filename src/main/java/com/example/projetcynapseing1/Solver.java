package com.example.projetcynapseing1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * Class Solver is used to solve mazes with a specific method and a timestep.
 *
 * @author Junjie
 */
public class Solver {
    // name of the method used to solve the maze
    private MethodName.SolveMethodName method;

    /**
     * constructor of Solver object
     *
     * @param m: method's name
     */
    public Solver(MethodName.SolveMethodName m) {
        method = m;
    }

    /**
     * constructor of Solver object
     */
    public Solver() {
        this(MethodName.SolveMethodName.ASTAR);
    }

    /**
     * getter of method
     *
     * @return method
     */
    public MethodName.SolveMethodName getMethod() {
        return method;
    }

    /**
     * setter of method
     *
     * @param m: new method
     */
    public void setMethod(MethodName.SolveMethodName m) {
        method = m;
    }

    /**
     * solve the maze with the corresponding method
     * the returning value depends of t
     * antecedents: array of antecedents of each vertex in the path
     * orders: the index of vertices visited in order
     * @param m: maze graph
     * @param start: starting vertex
     * @param end: ending vertex
     * @param t: type of printing
     * @return result
     */
    public int[] solve(Maze m, Vertex start, Vertex end, MethodName.Type t) {
        // verification
        if (m.equals(null) || start.equals(null) || end.equals(null) || t.equals(null)) {
            System.out.println("Param null !");
            return null;
        }
        if (!m.getVertices().contains(start)) {
            System.out.println("Vertex start isn't in the maze given !");
            return null;
        }
        if (!m.getVertices().contains(end)) {
            System.out.println("Vertex end isn't in the maze given !");
            return null;
        }

        switch (method) {
            case ASTAR: return this.solveAstar(m, start, end, t);
            case RIGHTHAND: return this.solveHand(m, start, end, t);
            case LEFTHAND: return this.solveHand(m, start, end, t);
            case BFS: return this.solveBFS(m, start, end, t);
            case DFS: return this.solveDFS(m, start, end, t);
            case ASTAR2: return this.solveAstar2(m, start, end, t);
            default: return null;
        }
    }
    /**
     * solve the maze with the A* algorithm
     * the returning value depends of t
     * antecedents: array of antecedents of each vertex in the path
     * orders: the index of vertices visited in order
     * @param m: maze graph
     * @param start: starting vertex
     * @param end: ending vertex
     * @param t: type of printing
     * @return result
     */
    private int[] solveAstar(Maze m, Vertex start, Vertex end, MethodName.Type t) {
        // list of vertices
        ArrayList<Vertex> vertices = m.getVertices();
        // number of vertices
        int n = m.getRows() * m.getColumns();
        // index of the starting vertex
        int si = start.getID();
        // index of the ending vertex
        int ei = end.getID();

        // visited[i] indicates if vertex i has been visited
        boolean[] visited = new boolean[n];
        // antecedents[i] indicates the vertex antecedent taken to access vertex i
        int[] antecedents = new int[n];
        // distances[i] indicates the length of the path between start and vertex i
        int[] distances = new int[n];
        // orders[i] indicates the index of the i-th visited vertex
        int[] orders = new int[n];
        // result to return
        int[] result = (t.equals(MethodName.Type.STEPPER)) ? orders : antecedents;
        // initialisation
        for (int i = 0; i < n; i++) {
            visited[i] = false;
            antecedents[i] = i;
            distances[i] = Integer.MAX_VALUE;
            orders[i] = -1;
        }

        // priority queue for the next vertex to visit, it compares the length of the
        // path to vertex i and also it Manhattan distance to end
        PriorityQueue<Integer> toVisit = new PriorityQueue<Integer>(
                Comparator.comparingInt(i -> distances[i] + distance(vertices.get(i), end)));

        // initialisation
        distances[si] = 0;
        toVisit.add(si);
        int ui;
        int vi;
        int cnt = 0;

        // while there's no path leading to end
        while (! visited[ei]) {
            // if toVisit is empty, it means that there's no path from start to end in this
            // maze
            if (toVisit.isEmpty()) {
                return result;
            }

            // ui the index of the vertex visiting
            ui = toVisit.poll();

            // if vertex ui hasn't been visited
            if (!visited[ui]) {
                for (Vertex v : vertices.get(ui).getNeighbors()) {
                    // vi the index of the vertex neighboring vertex ui
                    vi = vertices.indexOf(v);
                    // if vertex vi has not been visited
                    if (!visited[vi]) {
                        // if the path start -> u -> v is shorter than the actual path start -> v
                        if (distances[vi] > distances[ui] + 1) {
                            // change the antecedent of vi
                            antecedents[vi] = ui;
                            // update the distance
                            distances[vi] = distances[ui] + 1;
                            // add vi in to the vertex that we have to visit
                            toVisit.add(vi);
                        }
                    }
                }

                // vertex ui is now visited
                visited[ui] = true;
                orders[cnt] = ui;
                cnt++;
            }
        }

        // antecedents only shows the vertices that has been visited
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                antecedents[i] = i;
            }
        }

        return result;
    }
    /**
     * solve the maze with the right hand (left hand) algorithm
     * the returning value depends of t
     * antecedents: array of antecedents of each vertex in the path
     * orders: the index of vertices visited in order
     * @param m: maze graph
     * @param start: starting vertex
     * @param end: ending vertex
     * @param t: type of printing
     * @return result
     */
    private int[] solveHand(Maze m, Vertex start, Vertex end, MethodName.Type t) {
        // list of vertices
        ArrayList<Vertex> vertices = m.getVertices();
        // number of vertices
        int n = m.getRows() * m.getColumns();
        // index of the starting vertex
        int si = start.getID();
        // index of the ending vertex
        int ei = end.getID();
        // right, down, left, up: it indicates the number to add to the id for a
        // direction
        int[] directions = {1, m.getColumns(), -1, -m.getColumns()};

        // visited[i] indicates if vertex i has been visited
        boolean[] visited = new boolean[n];
        // antecedents[i] indicates the vertex antecedent taken to access vertex i
        int[] antecedents = new int[n];
        // orders[i] indicates the index of the i-th visited vertex
        int[] orders = new int[n];
        // result to return
        int[] result = (t.equals(MethodName.Type.STEPPER)) ? orders : antecedents;
        // initialisation
        for (int i = 0; i < n; i++) {
            visited[i] = false;
            antecedents[i] = i;
            orders[i] = -1;
        }

        // LIFO
        Stack<Integer> toVisit = new Stack<>();
        int ui;
        int vi;
        int di = 0;
        int cnt = 1;
        int addDirection = (method.equals(MethodName.SolveMethodName.LEFTHAND)) ? 3 : 1;

        // initialisation
        visited[si] = true;
        orders[0] = si;
        for (int i = 0; i < 4; i++){
            for (Vertex v: vertices.get(si).getNeighbors()){
                vi = v.getID();
                if (si + directions[i] == vi) {
                    antecedents[vi] = si;
                    toVisit.push(vi);
                }
            }
        }

        // while end has not been visited
        while (! visited[ei]){
            // if toVisit is empty, it means that there's no path from start to end in this maze
            if (toVisit.isEmpty()){
                return result;
            }

            // ui the index of the vertex visiting
            ui = toVisit.pop();

            // the difference of id between vertex u and vertex antecedent to u
            di = ui - antecedents[ui];
            for (int i = 0; i < 4; i++) {
                // if i is the direction from vertex antecedent to vertex u
                if (di == directions[i]) {
                    // the coming direction to vertex u
                    di = (i + 2) % 4;
                    break;
                }
            }

            // for each direction that is not the coming direction
            for (int i = 1; i < 4; i++) {
                // next direction
                di = (di + addDirection) % 4;
                // for each neighbors
                for (Vertex v : vertices.get(ui).getNeighbors()) {
                    // index of vertex v
                    vi = v.getID();
                    // if vertex v not visited and vertex v in the direction di
                    if ((ui + directions[di] == vi) && (!visited[vi])) {
                        // update the antecedent
                        antecedents[vi] = ui;
                        // add vertex v into the vertices to visit
                        toVisit.push(vi);
                    }
                }
            }

            visited[ui] = true;
            orders[cnt] = ui;
            cnt++;
        }

        // antecedents only shows the vertices that has been visited
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                antecedents[i] = i;
            }
        }

        return result;
    }
    /**
     * solve the maze with the DFS algorithm
     * the returning value depends of t
     * antecedents: array of antecedents of each vertex in the path
     * orders: the index of vertices visited in order
     * @param m: maze graph
     * @param start: starting vertex
     * @param end: ending vertex
     * @param t: type of printing
     * @return result
     */
    private int[] solveDFS(Maze m, Vertex start, Vertex end, MethodName.Type t) {
        // list of vertices
        ArrayList<Vertex> vertices = m.getVertices();
        // number of vertices
        int n = m.getRows() * m.getColumns();
        // index of the starting vertex
        int si = start.getID();
        // index of the ending vertex
        int ei = end.getID();

        // visited[i] indicates if vertex i has been visited
        boolean[] visited = new boolean[n];
        // antecedents[i] indicates the vertex antecedent taken to access vertex i
        int[] antecedents = new int[n];
        // orders[i] indicates the index of the i-th visited vertex
        int[] orders = new int[n];
        // result to return
        int[] result = (t.equals(MethodName.Type.STEPPER)) ? orders : antecedents;
        // initialisation
        for (int i = 0; i < n; i++) {
            visited[i] = false;
            antecedents[i] = i;
            orders[i] = -1;
        }

        Stack<Integer> toVisit = new Stack<>();

        // initialisation
        toVisit.add(si);
        int ui;
        int vi;
        int cnt = 0;

        // while there's no path leading to end
        while (! visited[ei]) {
            // if toVisit is empty, it means that there's no path from start to end in this
            // maze
            if (toVisit.isEmpty()) {
                return result;
            }

            // ui the index of the vertex visiting
            ui = toVisit.pop();

            // if vertex ui hasn't been visited
            if (!visited[ui]) {
                for (Vertex v : vertices.get(ui).getNeighbors()) {
                    // vi the index of the vertex neighboring vertex ui
                    vi = vertices.indexOf(v);
                    // if vertex vi has not been visited
                    if (!visited[vi]) {
                        // change the antecedent of vi
                        antecedents[vi] = ui;
                        // add vi in to the vertex that we have to visit
                        toVisit.add(vi);
                    }
                }

                // vertex ui is now visited
                visited[ui] = true;
                orders[cnt] = ui;
                cnt++;
            }
        }

        // antecedents only shows the vertices that has been visited
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                antecedents[i] = i;
            }
        }

        return result;
    }
    /**
     * solve the maze with the BFS algorithm
     * the returning value depends of t
     * antecedents: array of antecedents of each vertex in the path
     * orders: the index of vertices visited in order
     * @param m: maze graph
     * @param start: starting vertex
     * @param end: ending vertex
     * @param t: type of printing
     * @return result
     */
    private int[] solveBFS(Maze m, Vertex start, Vertex end, MethodName.Type t) {
        // list of vertices
        ArrayList<Vertex> vertices = m.getVertices();
        // number of vertices
        int n = m.getRows() * m.getColumns();
        // index of the starting vertex
        int si = start.getID();
        // index of the ending vertex
        int ei = end.getID();

        // visited[i] indicates if vertex i has been visited
        boolean[] visited = new boolean[n];
        // antecedents[i] indicates the vertex antecedent taken to access vertex i
        int[] antecedents = new int[n];
        // orders[i] indicates the index of the i-th visited vertex
        int[] orders = new int[n];
        // result to return
        int[] result = (t.equals(MethodName.Type.STEPPER)) ? orders : antecedents;
        // initialisation
        for (int i = 0; i < n; i++) {
            visited[i] = false;
            antecedents[i] = i;
            orders[i] = -1;
        }

        Queue<Integer> toVisit = new ArrayDeque<>();

        // initialisation
        toVisit.add(si);
        int ui;
        int vi;
        int cnt = 0;

        // while there's no path leading to end
        while (! visited[ei]) {
            // if toVisit is empty, it means that there's no path from start to end in this
            // maze
            if (toVisit.isEmpty()) {
                return result;
            }

            // ui the index of the vertex visiting
            ui = toVisit.poll();

            // if vertex ui hasn't been visited
            if (!visited[ui]) {
                for (Vertex v : vertices.get(ui).getNeighbors()) {
                    // vi the index of the vertex neighboring vertex ui
                    vi = vertices.indexOf(v);
                    // if vertex vi has not been visited
                    if (!visited[vi]) {
                        // change the antecedent of vi
                        antecedents[vi] = ui;
                        // add vi in to the vertex that we have to visit
                        toVisit.add(vi);
                    }
                }

                // vertex ui is now visited
                visited[ui] = true;
                orders[cnt] = ui;
                cnt++;
            }
        }

        // antecedents only shows the vertices that has been visited
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                antecedents[i] = i;
            }
        }

        return result;
    }
    /**
     * solve the maze with the A* algorithm upgrated
     * the returning value depends of t
     * antecedents: array of antecedents of each vertex in the path
     * orders: the index of vertices visited in order
     * @param m: maze graph
     * @param start: starting vertex
     * @param end: ending vertex
     * @param t: type of printing
     * @return result
     */
    private int[] solveAstar2(Maze m, Vertex start, Vertex end, MethodName.Type t){
        // list of vertices
        ArrayList<Vertex> vertices = m.getVertices();
        // number of vertices
        int n = m.getRows() * m.getColumns();
        // index of the starting vertex
        int si = start.getID();
        // index of the ending vertex
        int ei = end.getID();

        // visited[i] indicates if vertex i has been visited
        boolean[] visited = new boolean[n];
        // antecedents[i] indicates the vertex antecedent taken to access vertex i
        int[] antecedents = new int[n];
        // orders[i] indicates the index of the i-th visited vertex
        int[] orders = new int[n];
        // result to return
        int[] result = (t.equals(MethodName.Type.STEPPER)) ? orders : antecedents;
        // initialisation
        for (int i = 0; i < n; i++) {
            visited[i] = false;
            antecedents[i] = i;
            orders[i] = -1;
        }

        Stack<Integer> toVisit = new Stack<>();

        // initialisation
        toVisit.add(ei);
        int ui = ei;
        int vi;
        int cnt = 0;

        // while there's no path leading to end
        while (! visited[si]) {
            // if toVisit is empty, it means that there's no path from start to end in this maze
            if (toVisit.isEmpty()) {
                return result;
            }

            // if there's choices of path
            if (toVisit.size() > 1){
                break;
            }

            // ui the index of the vertex visiting
            ui = toVisit.pop();

            // if vertex ui hasn't been visited
            if (!visited[ui]) {
                for (Vertex v : vertices.get(ui).getNeighbors()) {
                    // vi the index of the vertex neighboring vertex ui
                    vi = vertices.indexOf(v);
                    // if vertex vi has not been visited
                    if (!visited[vi]) {
                        // change the antecedent of vi
                        antecedents[vi] = ui;
                        // add vi in to the vertex that we have to visit
                        toVisit.add(vi);
                    }
                }

                // vertex ui is now visited
                visited[ui] = true;
                orders[cnt] = ui;
                cnt++;
            }
        }

        // antecedents only shows the vertices that has been visited
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                antecedents[i] = i;
            }
        }

        // Astar from start to the last vertex we reached
        int[] resultAstar = solveAstar(m, start, vertices.get(ui), t);

        if (t.equals(MethodName.Type.STEPPER)){
            for (int i = 0; i < n; i++){
                if (resultAstar[i] == -1){
                    break;
                }
                orders[cnt] = resultAstar[i];
                cnt++;
            }
        }
        else {
            vi = antecedents[ui];
            antecedents[ui] = ui;
            int wi = antecedents[vi];
            while (ui != ei) {
                antecedents[vi] = ui;
                ui = vi;
                vi = wi;
                wi = antecedents[vi];
            }
            for (int i = 0; i < n; i++) {
                if (antecedents[i] == i) {
                    antecedents[i] = resultAstar[i];
                }
            }
        }

        return result;
    }

    /**
     * Manhattan distance
     * @param a: First vertex
     * @param b: Second vertex
     * @return Manhattan distance between 2 vertices
     */
    public static int distance(Vertex a, Vertex b) {
        // |a.x - b.x| + |a.y - b.y|
        return 2 * (Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()));
        // Euclidean distance
        // return Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
    }

    /**
     * index of the path without other information
     *
     * @param m:           maze graph
     * @param end:         ending vertex
     * @param antecedents: array of antecedents of each vertex in the path
     * @return path: list of index (similiar to antecedents)
     */
    public static int[] pathIndex(Maze m, Vertex end, int[] antecedents) {
        // verification
        if (m.equals(null) || end.equals(null) || antecedents.equals(null)) {
            System.out.println("Param null !");
            return null;
        }

        ArrayList<Vertex> vertices = m.getVertices();
        // number of vertices
        int n = m.getRows() * m.getColumns();
        // initialisation
        int[] path = new int[n];
        for (int i = 0; i < n; i++) {
            path[i] = i;
        }

        // verification
        if (n != antecedents.length) {
            System.out
                    .println("Inappropriate length of antecedents: " + antecedents.length + " (insted of " + n + ") !");
            return path;
        }
        if (!vertices.contains(end)) {
            System.out.println("Vertex end isn't in the maze given !");
            return path;
        }
        for (int i = 0; i < n; i++) {
            if (antecedents[i] < 0 || antecedents[i] > n) {
                System.out.println("Table antecedents is inappropriately indexed: antecedents[" + i + "] = "
                        + antecedents[i] + " !");
                return path;
            }
        }

        // counter to avoid infinite while
        int cnt = 0;
        // from end to start
        int i = vertices.indexOf(end);
        while (antecedents[i] != i && cnt <= n) {
            path[i] = antecedents[i];
            i = antecedents[i];
            cnt++;
        }

        return path;
    }

    /**
     * get the path from start to end
     *
     * @param m:           maze graph
     * @param end:         ending vertex
     * @param antecedents: array of antecedents of each vertex in the path
     * @return path: list of vertices in the path
     */
    public static ArrayList<Vertex> pathVertex(Maze m, Vertex end, int[] antecedents) {
        ArrayList<Vertex> path = new ArrayList<>();

        // verification
        if (m.equals(null) || end.equals(null) || antecedents.equals(null)) {
            System.out.println("Param null !");
            return path;
        }

        ArrayList<Vertex> vertices = m.getVertices();
        // number of vertices
        int n = m.getRows() * m.getColumns();

        // verification
        if (!vertices.contains(end)) {
            System.out.println("Vertex end isn't in the maze given !");
            return path;
        }
        for (int i = 0; i < antecedents.length; i++) {
            if (antecedents[i] < 0 || antecedents[i] > n) {
                System.out.println("Table antecedents is inappropriately indexed: antecedents[" + i + "] = "
                        + antecedents[i] + " !");
                return path;
            }
        }

        // counter to avoid infinite while
        int cnt = 0;
        // from end to start
        int i = vertices.indexOf(end);
        while (antecedents[i] != i && cnt <= n) {
            path.add(0, vertices.get(antecedents[i]));
            i = antecedents[i];
            cnt++;
        }
        // if path is solution
        if (cnt != 0 || n == 1){
            path.add(end);
        }

        return path;
    }

    /**
     * get the path from start to end
     *
     * @param m:           maze graph
     * @param end:         ending vertex
     * @param antecedents: array of antecedents of each vertex in the path
     * @return path: list of edges in the path
     */
    public static ArrayList<Edge> pathEdge(Maze m, Vertex end, int[] antecedents) {
        ArrayList<Edge> path = new ArrayList<>();

        // verification
        if (m.equals(null) || end.equals(null) || antecedents.equals(null)) {
            System.out.println("Param null !");
            return path;
        }

        ArrayList<Vertex> vertices = m.getVertices();
        // number of vertices
        int n = m.getRows() * m.getColumns();

        // verification
        if (!vertices.contains(end)) {
            System.out.println("Vertex end isn't in the maze given !");
            return path;
        }
        for (int i = 0; i < antecedents.length; i++) {
            if (antecedents[i] < 0 || antecedents[i] > n) {
                System.out.println("Table antecedents is inappropriately indexed: antecedents[" + i + "] = "
                        + antecedents[i] + " !");
                return path;
            }
        }

        // counter to avoid infinite while
        int cnt = 0;
        // from end to start
        int i = vertices.indexOf(end);
        while (antecedents[i] != i && cnt <= n) {
            path.add(0, m.getEdgeByVertices(vertices.get(antecedents[i]), vertices.get(i)));
            i = antecedents[i];
            cnt++;
        }

        return path;
    }

    /**
     * @return method and time step in a String format
     */
    @Override
    public String toString() {
        return "Solver: " + method.toString();
    }
}