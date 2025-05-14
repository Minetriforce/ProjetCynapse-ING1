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
    private FileController fileController;

    public MazeController() {
        this.fileController = new FileController();
    }

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
                convertSolution.add(maze.getVertexByIDVertex(i));
            }
        }
        return (convertSolution);
    }

    public Generator getGenerator() {
        if (mazeGenerator == null) {
            System.out.println(
                    "No maze generator has been instantiated ! \n Run function MazeController.makeMaze and try again");
        }
        return (mazeGenerator);
    }

    public Solver getSolver() {
        if (mazeSolver == null) {
            System.out.println(
                    "No maze solver has been instantiated ! \n Run function MazeController.findSolution and try again");
        }
        return (mazeSolver);
    }

    public FileController getFileController() {
        return (fileController);
    }

    public void setFXController(FXController fxController) {
        if (fxController == null) {
            System.out.println("-- Maze Controller ");
            System.err.println("Warning : fxController is null");
        }
        this.fxController = fxController;
    }
}