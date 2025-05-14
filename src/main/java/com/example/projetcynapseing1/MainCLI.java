package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main CLI is the second version of the application that is not using JavaFX.
 * Some functionalities aren't available in CLI mode such as Stepper generation
 * / solution
 */
public class MainCLI {

    /**
     * Entry point of main CLI
     * 
     * @param args cli
     */
    public static void main(String args[]) {
        int rows = 0;
        int columns = 0;
        int seed = 0;

        String menuChoice = null;
        String generationChoice = null;

        MazeController mazeController = new MazeController();

        if (args.length > 0) {
            String command = args[0];
            switch (command) {
                case "cli":
                    System.out.println(
                            "  ____    ____     ___        _   _____   _____      ____  __   __  _   _      _      ____    ____    _____ \n"
                                    + //
                                    " |  _ \\  |  _ \\   / _ \\      | | | ____| |_   _|    / ___| \\ \\ / / | \\ | |    / \\    |  _ \\  / ___|  | ____|\n"
                                    + //
                                    " | |_) | | |_) | | | | |  _  | | |  _|     | |     | |      \\ V /  |  \\| |   / _ \\   | |_) | \\___ \\  |  _|  \n"
                                    + //
                                    " |  __/  |  _ <  | |_| | | |_| | | |___    | |     | |___    | |   | |\\  |  / ___ \\  |  __/   ___) | | |___ \n"
                                    + //
                                    " |_|     |_| \\_\\  \\___/   \\___/  |_____|   |_|      \\____|   |_|   |_| \\_| /_/   \\_\\ |_|     |____/  |_____|\n");

                    Scanner sc = new Scanner(System.in);

                    System.out.println("MENU ");
                    System.out.println(" 1 - Generate a labyrinth");
                    System.out.println(" 2 - Load a labyrinth");

                    menuChoice = sc.nextLine().toLowerCase().trim();

                    if (menuChoice.equalsIgnoreCase("1") || menuChoice.equalsIgnoreCase("generate a labyrinth")) {
                        System.out.println("Enter the number of rows:");
                        rows = sc.nextInt();
                        System.out.println("Enter the number of columns:");
                        columns = sc.nextInt();
                        System.out.println("Enter a seed (or 0 for random):");
                        seed = sc.nextInt();

                        System.out.println("How would you like to generate it?");
                        System.out.println(" 1 - Prim");
                        System.out.println(" 2 - Kruskal");
                        System.out.println(" 3 - RNG_DFS");

                        sc.nextLine();

                        generationChoice = sc.nextLine().toLowerCase().trim();
                    }

                    switch (generationChoice) {
                        case "1":
                        case "prim":
                            // Create the maze using Prim's algorithm
                            mazeController.createMaze(MethodName.GenMethodName.PRIM,
                                    MethodName.Type.COMPLETE, rows, columns, 0.0, seed);

                            break;

                        case "2":
                        case "kruskal":
                            // Create the maze using Kruskal's algorithm
                            mazeController.createMaze(MethodName.GenMethodName.KRUSKAL,
                                    MethodName.Type.COMPLETE, rows, columns, 0.0, seed);

                            break;

                        case "3":
                        case "rng_dfs":
                            // Create the maze using RNG_DFS's algorithm
                            mazeController.createMaze(MethodName.GenMethodName.DFS,
                                    MethodName.Type.COMPLETE, rows, columns, 0.0, seed);
                            break;

                    }

                    Maze maze = mazeController.getCurrentMaze();
                    System.out.println("\nGenerated Maze:");
                    System.out.println(maze);
                    // Solve the maze from top-left to bottom-right
                    Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);
                    int startId = 0; // Top-left corner
                    int endId = (rows * columns) - 1; // Bottom-right corner
                    int[] parents = solver.solveAstar(maze,
                            maze.getVertexByID(startId),
                            maze.getVertexByID(endId),
                            MethodName.Type.COMPLETE);
                    int[] solution = Solver.pathIndex(maze,
                            maze.getVertexByID(endId),
                            parents);
                    ArrayList<Edge> path_edge = Solver.pathEdge(maze,
                            maze.getVertexByID(endId),
                            parents);
                    System.out.println("\nSolution found:");
                    System.out.println(maze.solutionToString(solution));
                    System.out.println("\nEdges of the path found:");
                    System.out.println(path_edge);

                    break;
                default:
                    System.out.println("Unvalid command");
                    break;

                /*
                 * case "save":
                 * saveLabyrinthe();
                 * break;
                 * case "load":
                 * loadLabyrinthe();
                 * break;
                 */

            }
        }
    }
}
