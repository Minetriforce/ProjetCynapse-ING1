package com.example.projetcynapseing1;

import java.util.Scanner;

/**
 * Main class that runs the command-line interface (CLI) for generating and
 * solving mazes.
 * It allows the user to choose a maze generation method, set the maze
 * dimensions,
 * and solve the maze using the A* algorithm.
 * 
 * @author Jonathan
 */
public class MainCLI {
    // mazeController / FXController Instantiated
    private static final MazeController mazeController = new MazeController();

    // ANSI escape codes for colors
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001b[38;5;46m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String BOLD = "\u001B[1m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String GRAY = "\u001b[38;5;244m";

    /**
     * Utility method to get a valid positive integer input from the user
     * @param scanner The Scanner object to read input
     * @param prompt The prompt message to display to the user
     * @return A valid positive integer entered by the user
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

    private static int verifCorrectInterval(Scanner sc, int rows, int columns){
        int value;
        while (true) {
            if (sc.hasNextInt()) {
                value = sc.nextInt();
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
     * Main entry point of the application.
     * It prompts the user to choose options from a menu, define maze dimensions,
     * select a generation method, and solve the maze using the A* algorithm.
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

        // Check if command-line arguments are provided
        if (args.length > 0) {
        String command = args[0];
            // If the command is "cli", display the CLI menu
        switch (command) {
            case "cli":
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
                    }

                    if(generationChoice!=null){
                        // Based on the user's choice, create the maze
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
                            case "4":
                            case "imperfect":
                                // Create the maze using Imperfect's algorithm
                                mazeController.createMaze(MethodName.GenMethodName.IMPERFECT,
                                        MethodName.Type.COMPLETE, rows, columns, 0.0, seed);
                        }
                    }

                    // Retrieve the generated maze
                    Maze maze = mazeController.getCurrentMaze();
                    System.out.println(UNDERLINE + BOLD + "\nGenerated Maze:" + RESET);
                    System.out.println(maze);

                    System.out.println(ITALIC + "Starting point : " + RESET);
                    startId = verifCorrectInterval(sc, rows, columns);
                    
                    System.out.println(ITALIC + "Ending point : " + RESET);
                    endId = verifCorrectInterval(sc, rows, columns);

                    sc.nextLine();

                    System.out.println(ITALIC + "Solve the maze? " + RESET + BOLD + "[" + GREEN + BOLD + "Y" + RESET +"/"+ RED + BOLD +"N" + RESET + "]" + RESET);


                    solveChoice = verifYesNo(sc);

                    
                    
                    if(solveChoice.equals("y")){
                        // Ask the user to select a solving method
                        System.out.println(ITALIC + "How would you like to solve it?" + RESET);
                        System.out.println(BOLD + " 1 " + RESET + "- " + MethodName.SolveMethodName.ASTAR + RESET);
                        System.out.println(BOLD + " 2 " + RESET + "- " + MethodName.SolveMethodName.BFS + RESET);
                        System.out.println(BOLD + " 3 " + RESET + "- " + MethodName.SolveMethodName.DFS + RESET);
                        System.out.println(BOLD + " 4 " + RESET + "- " + MethodName.SolveMethodName.RIGHTHAND + RESET);

                        while(true){
                            solvingChoice = sc.nextLine().toLowerCase().trim();
                            if(solvingChoice.equals("1") || solvingChoice.equals("astar")
                            || solvingChoice.equals("2") || solvingChoice.equals("bfs")
                            || solvingChoice.equals("3") || solvingChoice.equals("dfs")
                            || solvingChoice.equals("4") || solvingChoice.equals("righthand")){
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
                                mazeController.findSolution(MethodName.SolveMethodName.ASTAR, maze.getVertexByID(startId), maze.getVertexByID(endId), MethodName.Type.COMPLETE, 0.0);
                                break;
    
                            case "2":
                            case "bfs":
                                // Solve the maze using BFS algorithm
                                mazeController.findSolution(MethodName.SolveMethodName.BFS, maze.getVertexByID(startId), maze.getVertexByID(endId), MethodName.Type.COMPLETE, 0.0);
                                break;
    
                            case "3":
                            case "dfs":
                                // Create the maze using DFS algorithm
                                mazeController.findSolution(MethodName.SolveMethodName.DFS, maze.getVertexByID(startId), maze.getVertexByID(endId), MethodName.Type.COMPLETE, 0.0);
                                break;
                            case "4":
                            case "righthand":
                                // Create the maze using Righthand algorithm
                                mazeController.findSolution(MethodName.SolveMethodName.RIGHTHAND, maze.getVertexByID(startId), maze.getVertexByID(endId), MethodName.Type.COMPLETE, 0.0);
                        }

                        // Solve the maze
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
                        System.out.println(maze.solutionToString(antecedents,solution));
                    }

                    System.out.println(ITALIC + "Save the maze? " + RESET + BOLD + "[" + GREEN + BOLD + "Y" + RESET +"/"+ RED + BOLD +"N" + RESET + "]" + RESET);

                    saveChoice = verifYesNo(sc);

                    if(saveChoice.equals("y")){
                        mazeController.saveMaze();
                    }
                    sc.close();
                    System.exit(0);
                break;


            default:
                    // If the command is invalid
                    System.out.println(RED + "Invalid command" + RESET);
                break;


            }
        }
    }
}
