package com.example.projetcynapseing1;

import java.util.ArrayList;

public class MazeController {
    private Generator mazeGenerator;
    private FXController fxController;
    private Solver mazeSolver;
    private int[] solution;
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
        mazeSolver = new Solver(solveMethod);
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