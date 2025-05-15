package com.example.projetcynapseing1;

import java.util.ArrayList;

/**
 * Class used to manage mazes : creating, saving, solving and loading mazes
 * 
 * @author Bari-joris
 * @see Maze
 */
public class MazeController {
    /**
     * Maze generator is saved each time a maze is generated. It is saved to keep
     * informations of generation
     */
    private Generator mazeGenerator;

    /**
     * FX controller variable is used in case communication is necessary between
     * maze controller and FX Controller
     */
    private FXController fxController;

    /**
     * Maze Solver is saved each time a request to solve a maze is made. It is saved
     * to keep informations of solving
     */
    private Solver mazeSolver;

    /**
     * solution is a list of index of vertices for a path between a vertex A and a
     * vertex B
     * WARNING: maze and solution can be on differents basis, make sure to run
     * findSolution function if you have generated and solved multiple mazes
     */
    private int[] solution;

    /**
     * current maze saved.
     * WARNING: maze and solution can be on differents basis, make sure to run
     * findSolution function if you have generated and solved multiple mazes
     */
    private Maze maze;

    /**
     * Method to generate maze : it creates a specific generator, saves it and use
     * it to generate a maze
     * 
     * @param genMethod algorithm used to generate the maze
     * @param type      step-by-step or complete
     * @param x         strictly positive integer : number of columns
     * @param y         striclty positive integer : number of rows
     * @param timeStep  generation time between each steps
     * @param seed      strictly positive integer : used in the randim number
     *                  generator of the generator
     */
    public void createMaze(MethodName.GenMethodName genMethod, MethodName.Type type, Integer x, Integer y,
            Double timeStep, Integer seed) {
        try {
            mazeGenerator = new Generator(x, y, genMethod, seed);
            maze = mazeGenerator.makeMaze();
        } catch (Exception e) {
            System.err.println("--MazeController Class--");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Can be used only if a maze was generated.
     * make the solution of the maze by finding it's
     * 
     * @param solveMethod method used to solve the maze
     * @param start       a starting vertex
     * @param end         an ending vertex
     * @param type        complete or step-by-step
     * @param timeStep    time stamp between each steps of solution finding
     * @throws Exception if there's no maze instantiated
     */
    public void findSolution(MethodName.SolveMethodName solveMethod, Vertex start, Vertex end, MethodName.Type type,
            Double timeStamp) {
        mazeSolver = new Solver(solveMethod,fxController);
        // solution = mazeSolver.solveAstar(maze, start, end, type);
    }

    public Maze getCurrentMaze() {
        if (maze == null) {
            System.out.println("No maze has been created/instantiated !");
        }
        return (maze);
    }

    /**
     * return solution of the current maze
     * WARNING : this solution can be the solution of a previous generated maze, try
     * running findSolution before calling this function
     * 
     * @return ArrayList of vertices, path found or null
     */
    public ArrayList<Vertex> getSolution() {
        ArrayList<Vertex> convertSolution = new ArrayList<Vertex>();

        if (solution == null) {
            System.out.println("No solution stored actually !");
            if (mazeSolver == null) {
                System.err.println(
                        "Can't lauch Solver becasue it's not instantated yet. \n Run command MazeController.findSolution then try again");
            } else {
                System.out.println("Finding solution with current Solver" + mazeSolver);
            }
        } else {
            for (int i : solution) {
                convertSolution.add(maze.getVertexByID(i));
            }
        }
        return (convertSolution);
    }

    /**
     * return the last generator used
     * 
     * @return Generator Instance
     */
    public Generator getGenerator() {
        if (mazeGenerator == null) {
            System.out.println(
                    "No maze generator has been instantiated ! \n Run function MazeController.makeMaze and try again");
        }
        return (mazeGenerator);
    }

    /**
     * return the last solver used
     * 
     * @return Solver Instance
     */
    public Solver getSolver() {
        if (mazeSolver == null) {
            System.out.println(
                    "No maze solver has been instantiated ! \n Run function MazeController.findSolution and try again");
        }
        return (mazeSolver);
    }

    /**
     * Set the Instance of the fx controller currently used by JavaFX
     * 
     * @param fxController instance of FXController class
     */
    public void setFXController(FXController fxController) {
        if (fxController == null) {
            System.out.println("-- Maze Controller ");
            System.err.println("Warning : fxController is null");
        }
        this.fxController = fxController;
    }

    /**
     * Save current maze with the FileController class
     * 
     * @param mazeName name of file
     * @return confirmation if maze was succesfully saved
     */
    public Boolean saveMaze() {
        try {
            FileController.SaveData(maze);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    /**
     * load maze function
     * Lauch the load Maze function from the filController and asign the loaded maze
     * to current maze variable
     */
    public void loadMaze() {
        maze = FileController.loadMaze();
        if (maze == null) {
            System.out.println("--- Maze Controller ---");
            System.out.println("WARNING : loaded maze seems to be null, try to load it again or change file");
        }
    }
}