package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Main class that runs the command-line interface (CLI) for generating and
 * solving mazes.
 * It allows the user to choose a maze generation method, set the maze
 * dimensions,
 * and solve the maze using the A* algorithm or other solving strategies.
 * <p>
 * The CLI supports generating a maze with various algorithms, loading a maze
 * from a file,
 * editing walls manually, selecting start and end points, solving the maze,
 * and saving the maze.
 * </p>
 * 
 * <p>Color codes are used for improved CLI visual output.</p>
 * 
 * @author Jonathan
 */
public class MainCLI {
    /** Controller for managing maze operations such as creation, loading, solving, and saving */
    private static final MazeController mazeController = new MazeController();

    /** ANSI escape code to reset terminal color */
    public static final String RESET = "\u001B[0m";
    /** ANSI escape code for red text */
    public static final String RED = "\u001B[31m";
    /** ANSI escape code for red background */
    public static final String REDBACK = "\u001B[48;5;196m";
    /** ANSI escape code for green text */
    public static final String GREEN = "\u001b[38;5;46m";
    /** ANSI escape code for green background */
    public static final String GREENBACK = "\u001b[48;5;46m";
    /** ANSI escape code for yellow text */
    public static final String YELLOW = "\u001B[33m";
    /** ANSI escape code for blue text */
    public static final String BLUE = "\u001B[34m";
    /** ANSI escape code for cyan text */
    public static final String CYAN = "\u001B[36m";
    /** ANSI escape code for magenta text */
    public static final String MAGENTA = "\u001B[35m";
    /** ANSI escape code for bold text */
    public static final String BOLD = "\u001B[1m";
    /** ANSI escape code for italic text */
    public static final String ITALIC = "\u001B[3m";
    /** ANSI escape code for underline text */
    public static final String UNDERLINE = "\u001B[4m";
    /** ANSI escape code for gray text */
    public static final String GRAY = "\u001b[38;5;244m";

    /**
     * Utility method to get a valid positive integer input from the user.
     * Keeps prompting until the user enters an integer greater than zero.
     *
     * @param scanner The Scanner object to read input from.
     * @param prompt  The prompt message to display to the user.
     * @return A positive integer input by the user.
     */
    private static int verifPositiveInteger(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.println(ITALIC + prompt + RESET);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine();
                if (value > 0) {
                    return value;  // valid input, return the value
                } else {
                    System.out.println(RED + "Please enter a positive integer." + RESET);
                }
            } else {
                System.out.println(RED + "Invalid input. Please enter a positive integer." + RESET);
                scanner.nextLine(); // consume the invalid input
            }
        }
    }

    /**
     * Prompts the user with the given message to enter an integer that is zero or positive.
     * Continuously reads input from the provided Scanner until a valid integer >= 0 is entered.
     * If the input is invalid or negative, an error message is displayed and the user is prompted again.
     *
     * @param scanner The Scanner object used to read user input.
     * @param prompt  The message displayed to prompt the user.
     * @return A valid integer input that is zero or positive.
     */
    private static int verifPositiveOrZeroInteger(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.println(ITALIC + prompt + RESET);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value >= 0) {
                    return value;  // valid input, return the value
                } else {
                    System.out.println(RED + "Please enter a positive integer." + RESET);
                }
            } else {
                System.out.println(RED + "Invalid input. Please enter a positive integer." + RESET);
                scanner.nextLine(); // consume the invalid input
            }
        }
    }

    /**
     * Continuously prompts the user to input an integer within the valid range [0, rows * columns - 1].
     * Reads from the provided Scanner until a valid integer is entered.
     * If the input is not an integer or out of the valid range, displays an error message and prompts again.
     *
     * @param sc      The Scanner object used to read user input.
     * @param rows    The number of rows in the maze (used to determine the upper bound).
     * @param columns The number of columns in the maze (used to determine the upper bound).
     * @return A valid integer input within the range [0, rows * columns - 1].
     */
    private static int verifCorrectInterval(Scanner sc, int rows, int columns){
        int value;
        while (true) {
            if (sc.hasNextInt()) {
                value = sc.nextInt();
                sc.nextLine();
                // Check if the input is within the valid range
                if (value >= 0 && value < rows * columns) {
                    return value;
                } else {
                    System.out.println(RED + "Please enter an integer between 0 and " + ((rows * columns) - 1) + ": " + RESET);
                }
            } else {
                System.out.println(RED + "Invalid input. Please enter a integer: " + RESET);
                sc.nextLine(); // consume the invalid input
            }
        }
    }

    /**
     * Reads user input from the given Scanner and validates that it is either
     * "y" or "n" (case-insensitive). The method repeatedly prompts until a valid
     * response is entered.
     *
     * @param sc The Scanner object to read input from
     * @return The validated user input as a lowercase string, either "y" or "n"
     */
    private static String verifYesNo(Scanner sc){
        String YesNoChoice;
        while (true) {
            YesNoChoice = sc.nextLine().toLowerCase().trim();
            if (YesNoChoice.equals("y") || YesNoChoice.equals("n")) {
                return YesNoChoice;  // valid input, exit loop
            } else {
                System.out.println(RED + "Invalid choice. Please enter 'Y' or 'N'." + RESET);
            }
        }
    }
        
    /**
     * Allows the user to add or remove a wall between two adjacent cells in the maze.
     * Prompts for two cell IDs, checks adjacency, and toggles the wall accordingly.
     *
     * @param sc      Scanner for reading user input
     * @param maze    The current Maze object
     */
    private static void toggleWall(Scanner sc, Maze maze) {
        int rows = maze.getRows();
        int columns = maze.getColumns();
        System.out.println(ITALIC + "Enter two adjacent cell IDs to add/remove a wall between them." + RESET);
        int first = -1;
        int second = -1;
    
        while (true) {
            System.out.print("First cell ID: ");
            first = verifCorrectInterval(sc, rows, columns);
            System.out.print("Second cell ID: ");
            second = verifCorrectInterval(sc, rows, columns);
        
            // Check if the two cells are adjacent (difference of 1 in row or column)
            // Calculate the row and column of the first cell
            int r1 = first / columns;
            int c1 = first % columns;
            // Calculate the row and column of the second cell
            int r2 = second / columns;
            int c2 = second % columns;
        
            // Check if the two cells are adjacent
            if (Math.abs(r1 - r2) + Math.abs(c1 - c2) != 1) {
                System.out.println(RED + "Cells are not adjacent! Please try again." + RESET);
                continue;
            }
            break;
        }
    
        Vertex v1 = maze.getVertexByID(first);
        Vertex v2 = maze.getVertexByID(second);
    
        Edge edge = new Edge(v1, v2, true);
    
        // Toggle the presence of the wall (edge)
        if (maze.getEdges().contains(edge)) {
            maze.removeEdge(edge);
            System.out.println("Wall added between " + first + " and " + second);
        } else {
            maze.addEdge(edge);
            System.out.println("Wall removed between " + first + " and " + second);
        }
    }

    /**
     * Main entry point of the application.
     * <p>
     * Provides an interactive CLI menu for generating or loading a maze,
     * editing walls, setting start/end points, solving the maze with different
     * algorithms, and saving the maze.
     * </p>
     * 
     * <p>
     * Supported generation methods: Prim, Kruskal, RNG_DFS, Imperfect.<br>
     * Supported solving methods: A*, BFS, DFS, RightHand, LeftHand.
     * </p>
     * 
     * @param args Command line arguments. If "cli" is passed, the menu is
     *             displayed.
     */
    public static void main(String args[]) {

        // Variables to store maze dimensions and seed value
        int rows = 0;
        int columns = 0;
        int seed = 0;

        // Variables to store user menu choices
        String menuChoice = null;
        String generationChoice = null;
        String solveChoice = null;
        String solvingChoice = null;
        String saveChoice = null;

        // Variables to store the starting and ending point
        int startId = 0; // Top-left corner
        int endId = (rows * columns) - 1; // Bottom-right corner

        // Variables to store the time to solve the maze
        long startTime = 0;
        long endTime = 0;
        long timeMs = 0;

        // Variables to store the number of cell of the solution path and the number of all the visited cells
        int pathCount = 0;
        int visitedCount = 0;

        // Print the program title
        System.out.println(BLUE + BOLD +
                "  ____    ____     ___        _   _____   _____      ____  __   __  _   _      _      ____    ____    _____ \n" + 
                " |  _ \\  |  _ \\   / _ \\      | | | ____| |_   _|    / ___| \\ \\ / / | \\ | |    / \\    |  _ \\  / ___|  | ____|\n" + 
                " | |_) | | |_) | | | | |  _  | | |  _|     | |     | |      \\ V /  |  \\| |   / _ \\   | |_) | \\___ \\  |  _|  \n" +
                " |  __/  |  _ <  | |_| | | |_| | | |___    | |     | |___    | |   | |\\  |  / ___ \\  |  __/   ___) | | |___ \n" +
                " |_|     |_| \\_\\  \\___/   \\___/  |_____|   |_|      \\____|   |_|   |_| \\_| /_/   \\_\\ |_|     |____/  |_____|\n"
                + RESET);

        Scanner sc = new Scanner(System.in);

        // Display the menu
        System.out.println(BOLD + UNDERLINE + "MENU" + RESET);
        System.out.println(BOLD + " 1 " + RESET + "- " + BOLD + "Generate " + RESET + "a maze");
        System.out.println(BOLD + " 2 " + RESET + "- " + BOLD + "Load " + RESET + "a maze from a file" + RESET);

        
        // Read the user's choice for the menu
        while (true) {
            System.out.print("Choose an option: ");
            menuChoice = sc.nextLine().toLowerCase().trim();
            if (menuChoice.equals("1") || menuChoice.equals("generate") ||
                menuChoice.equals("2") || menuChoice.equals("load")) {
                break; // valid input
            } 
            else {
                System.out.println(RED + "Invalid choice. Please enter the first word or the corresponding number" + RESET);
            }
        }

        // If the user chooses to generate a maze
        if (menuChoice.equals("1") || menuChoice.equals("generate")) {
            // Get number of rows
            rows = verifPositiveInteger(sc, ITALIC + "Enter the number of rows:" + RESET);
            
            // Get number of columns
            columns = verifPositiveInteger(sc, ITALIC + "Enter the number of columns:" + RESET);

            // Get the seed
            seed = verifPositiveOrZeroInteger(sc, "Enter a seed (or 0 for random):");
            
            sc.nextLine(); // Consume the newline after nextInt()
            
            // Ask the user to select a generation method
            System.out.println(ITALIC + "How would you like to generate it?" + RESET);
            System.out.println(BOLD + " 1 " + RESET + "- Prim");
            System.out.println(BOLD + " 2 " + RESET + "- Kruskal" + RESET);
            System.out.println(BOLD + " 3 " + RESET + "- RNG_DFS" + RESET);
            System.out.println(BOLD + " 4 " + RESET + "- " + MethodName.GenMethodName.IMPERFECT + RESET);


            while(true){
                generationChoice = sc.nextLine().toLowerCase().trim();
                if(generationChoice.equals("1") || generationChoice.equals("prim")
                || generationChoice.equals("2") || generationChoice.equals("kruskal")
                || generationChoice.equals("3") || generationChoice.equals("rng_dfs")
                || generationChoice.equals("4") || generationChoice.equals("imperfect")){
                    break;
                }
                else{
                    System.out.println(RED + "Invalid choice. Please enter the name or the corresponding number" + RESET);
                }
            }
        }

        else if(menuChoice.equals("2") || menuChoice.equals("load")){
            mazeController.loadMaze();
            rows = mazeController.getCurrentMaze().getRows();
            columns = mazeController.getCurrentMaze().getColumns();
        }

        if(generationChoice!=null){
            // Based on the user's choice, create the maze
            switch (generationChoice) {
                case "1":
                case "prim":
                    // Create the maze using Prim's algorithm
                    mazeController.createMaze(MethodName.GenMethodName.PRIM, rows, columns, seed);

                
                    break;
    
                case "2":
                case "kruskal":
                    // Create the maze using Kruskal's algorithm
                    mazeController.createMaze(MethodName.GenMethodName.KRUSKAL, rows, columns, seed);
    
                    break;
    
                case "3":
                case "rng_dfs":
                    // Create the maze using RNG_DFS's algorithm
                    mazeController.createMaze(MethodName.GenMethodName.DFS, rows, columns, seed);

                    break;
                case "4":
                case "imperfect":
                    // Create the maze using Imperfect's algorithm
                    mazeController.createMaze(MethodName.GenMethodName.IMPERFECT, rows, columns, seed);

            }
        }

        // Retrieve the generated maze
        Maze maze = mazeController.getCurrentMaze();
        System.out.println(UNDERLINE + BOLD + "\nGenerated Maze:" + RESET);
        System.out.println(maze);

        System.out.println(ITALIC + "Do you want to edit walls in the maze? " + RESET + BOLD + "[" + GREEN + "Y" + RESET + "/" + RED + "N" + RESET + "]");

        String editWallsChoice = verifYesNo(sc);

        if (editWallsChoice.equals("y")) {
            while (true) {
                toggleWall(sc, mazeController.getCurrentMaze());
                System.out.println(mazeController.getCurrentMaze());
                System.out.println(ITALIC + "Edit another wall? " + RESET + BOLD + "[" + GREEN + "Y" + RESET + "/" + RED + "N" + RESET + "]");
                String choice = verifYesNo(sc);
                if (choice.equals("n")) break;
            }
        
            System.out.println("\n" + UNDERLINE + BOLD + "Maze after editing walls:" + RESET);
            System.out.println(maze);
        }




        System.out.println(ITALIC + "Starting point : " + RESET);
        startId = verifCorrectInterval(sc, rows, columns);
        
        System.out.println(ITALIC + "Ending point : " + RESET);
        endId = verifCorrectInterval(sc, rows, columns);


        System.out.println(ITALIC + "Solve the maze? " + RESET + BOLD + "[" + GREEN + BOLD + "Y" + RESET +"/"+ RED + BOLD +"N" + RESET + "]" + RESET);


        solveChoice = verifYesNo(sc);

        
        
        if(solveChoice.equals("y")){
            // Ask the user to select a solving method
            System.out.println(ITALIC + "How would you like to solve it?" + RESET);
            System.out.println(BOLD + " 1 " + RESET + "- " + MethodName.SolveMethodName.ASTAR + RESET);
            System.out.println(BOLD + " 2 " + RESET + "- " + MethodName.SolveMethodName.BFS + RESET);
            System.out.println(BOLD + " 3 " + RESET + "- " + MethodName.SolveMethodName.DFS + RESET);
            System.out.println(BOLD + " 4 " + RESET + "- " + MethodName.SolveMethodName.RIGHTHAND + RESET);
            System.out.println(BOLD + " 5 " + RESET + "- " + MethodName.SolveMethodName.LEFTHAND + RESET);

            while(true){
                solvingChoice = sc.nextLine().toLowerCase().trim();
                if(solvingChoice.equals("1") || solvingChoice.equals("astar")
                || solvingChoice.equals("2") || solvingChoice.equals("bfs")
                || solvingChoice.equals("3") || solvingChoice.equals("dfs")
                || solvingChoice.equals("4") || solvingChoice.equals("righthand")
                || solvingChoice.equals("5") || solvingChoice.equals("lefthand")){
                    break;
                }
                else{
                    System.out.println(RED + "Invalid choice. Please enter the name or the corresponding number" + RESET);
                }
            }

            // Based on the user's choice, create the maze
            switch (solvingChoice) {
                case "1":
                case "astar":
                    // Solve the maze using Astar's algorithm
                    startTime = System.currentTimeMillis();
                    mazeController.findSolution(MethodName.SolveMethodName.ASTAR, maze.getVertexByID(startId), maze.getVertexByID(endId));
                    endTime = System.currentTimeMillis();
                    break;
    
                case "2":
                case "bfs":
                    // Solve the maze using BFS algorithm
                    startTime = System.currentTimeMillis();
                    mazeController.findSolution(MethodName.SolveMethodName.BFS, maze.getVertexByID(startId), maze.getVertexByID(endId));
                    endTime = System.currentTimeMillis();
                    break;
    
                case "3":
                case "dfs":
                    // Create the maze using DFS algorithm
                    startTime = System.currentTimeMillis();
                    mazeController.findSolution(MethodName.SolveMethodName.DFS, maze.getVertexByID(startId), maze.getVertexByID(endId));
                    endTime = System.currentTimeMillis();
                    break;
                case "4":
                case "righthand":
                    // Create the maze using Righthand algorithm
                    startTime = System.currentTimeMillis();
                    mazeController.findSolution(MethodName.SolveMethodName.RIGHTHAND, maze.getVertexByID(startId), maze.getVertexByID(endId));
                    endTime = System.currentTimeMillis();
                    break;
                case "5":
                case "lefthand":
                    // Create the maze using Lefthand algorithm
                    startTime = System.currentTimeMillis();
                    mazeController.findSolution(MethodName.SolveMethodName.LEFTHAND, maze.getVertexByID(startId), maze.getVertexByID(endId));
                    endTime = System.currentTimeMillis();
                    break;
            }

            // Display solver details
            Solver solver = mazeController.getSolver();
            System.out.println(solver);
    
            int[] antecedents = solver.solve(maze,
                    maze.getVertexByID(startId),
                    maze.getVertexByID(endId),
                    MethodName.Type.COMPLETE);
            int[] solution = Solver.pathIndex(maze,
                    maze.getVertexByID(endId),
                    antecedents);
    
            System.out.println(UNDERLINE + BOLD + "\nSolution found:" + RESET);
            System.out.println(maze.solutionToString(antecedents,solution, startId, endId));

            for (int i = 0; i < antecedents.length; i++){
                if (antecedents[i] != i){
                    visitedCount++;
                }
            }
            visitedCount++;

            for (int i: solution){
                if (solution[i] != -1){
                    pathCount++;
                }
                else{
                    break;
                }
            }

            ArrayList<Vertex> solutionVertices = Solver.pathVertex(maze, maze.getVertexByID(endId), antecedents);
            pathCount=solutionVertices.size();

            timeMs = endTime - startTime;

            System.out.println(mazeController.getSolver() + ":  Path Length: " + pathCount + " | " + "Visited: "+  visitedCount + "| Time: " + timeMs + " ms");

        }

        System.out.println(ITALIC + "Save the maze? " + RESET + BOLD + "[" + GREEN + BOLD + "Y" + RESET +"/"+ RED + BOLD +"N" + RESET + "]" + RESET);

        saveChoice = verifYesNo(sc);

        if(saveChoice.equals("y")){
            mazeController.saveMaze();
        }
        sc.close();
        System.exit(0);
        
    }
}
