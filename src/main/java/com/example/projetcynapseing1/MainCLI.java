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

    // Maze controller to manage maze creation and solving
    private static final MazeController mazeController = new MazeController();
    private static final FXController fxController = new FXController();
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
                    System.out.println(BLUE + BOLD
                            + "  ____    ____     ___        _   _____   _____      ____  __   __  _   _      _      ____    ____    _____ \n"
                            + //
                            " |  _ \\  |  _ \\   / _ \\      | | | ____| |_   _|    / ___| \\ \\ / / | \\ | |    / \\    |  _ \\  / ___|  | ____|\n"
                            + //
                            " | |_) | | |_) | | | | |  _  | | |  _|     | |     | |      \\ V /  |  \\| |   / _ \\   | |_) | \\___ \\  |  _|  \n"
                            + //
                            " |  __/  |  _ <  | |_| | | |_| | | |___    | |     | |___    | |   | |\\  |  / ___ \\  |  __/   ___) | | |___ \n"
                            + //
                            " |_|     |_| \\_\\  \\___/   \\___/  |_____|   |_|      \\____|   |_|   |_| \\_| /_/   \\_\\ |_|     |____/  |_____|\n"
                            + RESET);

                    Scanner sc = new Scanner(System.in);

                    // Display the menu
                    System.out.println(BOLD + UNDERLINE + "MENU" + RESET);
                    System.out.println(BOLD + " 1 " + RESET + "- Generate a maze");
                    System.out.println(BOLD + " 2 " + RESET + "- Load a maze from a file" + RESET);

                    
                    // Read the user's choice for the menu
                    while (true) {
                        System.out.print("Choose an option: ");
                        menuChoice = sc.nextLine().toLowerCase().trim();
                        if (menuChoice.equals("1") || menuChoice.equals("generate a maze") ||
                            menuChoice.equals("2") || menuChoice.equals("load a maze from a file")) {
                            break; // valid input
                        } else {
                            System.out.println("Invalid choice. Please enter '1' or '2'.");
                        }
                    }

                    // If the user chooses to generate a maze
                    if (menuChoice.equalsIgnoreCase("1") || menuChoice.equalsIgnoreCase("generate a maze")) {
                        // Ask the user for maze dimensions and seed value
                        System.out.println(ITALIC + "Enter the number of rows:" + RESET);
                        rows = sc.nextInt();
                        System.out.println(ITALIC + "Enter the number of columns:" + RESET);
                        columns = sc.nextInt();
                        System.out.println(ITALIC + "Enter a seed (or 0 for random):" + RESET);
                        seed = sc.nextInt();

                        // Ask the user to select a generation method
                        System.out.println(ITALIC + "How would you like to generate it?" + RESET);
                        System.out.println(BOLD + " 1 " + RESET + "- Prim");
                        System.out.println(BOLD + " 2 " + RESET + "- Kruskal" + RESET);
                        System.out.println(BOLD + " 3 " + RESET + "- RNG_DFS" + RESET);
                        System.out.println(BOLD + " 4 " + RESET + "- " + MethodName.GenMethodName.UNPERFECT + RESET);

                        sc.nextLine(); // Consume the newline after nextInt()

                        generationChoice = sc.nextLine().toLowerCase().trim();
                    }

                    else if(menuChoice.equals("2") || menuChoice.equalsIgnoreCase("load a maze from a file")){
                        mazeController.loadMaze();
                    }

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
                        case "unperfect":
                            // Create the maze using Unperfect's algorithm
                            mazeController.createMaze(MethodName.GenMethodName.UNPERFECT,
                                    MethodName.Type.COMPLETE, rows, columns, 0.0, seed);
                    }

                    // Retrieve the generated maze
                    Maze maze = mazeController.getCurrentMaze();
                    System.out.println(UNDERLINE + BOLD + "\nGenerated Maze:" + RESET);
                    System.out.println(maze);

                    System.out.println("Starting point : ");
                    while (true) {
                        if (sc.hasNextInt()) {
                            startId = sc.nextInt();
                            // Check if the input is within the valid range
                            if (startId >= 0 && startId < rows * columns) {
                                break; // valid value, exit loop
                            } else {
                                System.out.println("Please enter an integer between 0 and " + ((rows * columns) - 1) + ": ");
                            }
                        } else {
                            System.out.println("Invalid input. Please enter an integer: ");
                            sc.next(); // consume the invalid input
                        }
                    }
                    
                    System.out.println("Ending point : ");
                    while (true) {
                        if (sc.hasNextInt()) {
                            endId = sc.nextInt();
                            // Check if the input is within the valid range
                            if (endId >= 0 && endId < rows * columns) {
                                break; // valid value, exit loop
                            } else {
                                System.out.println("Please enter an integer between 0 and " + ((rows * columns) - 1) + ": ");
                            }
                        } else {
                            System.out.println("Invalid input. Please enter an integer: ");
                            sc.next(); // consume the invalid input
                        }
                    }

                    sc.nextLine();

                    System.out.println(ITALIC + "Solve the maze? " + RESET + BOLD + "[Y/N]" + RESET);
                    solveChoice = sc.nextLine().toLowerCase().trim();

                    

                    if(solveChoice.equals("y")){
                        // Solve the maze
                        Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);
    
                        int[] antecedents = solver.solveAstar(maze,
                                maze.getVertexByID(startId),
                                maze.getVertexByID(endId),
                                MethodName.Type.COMPLETE);
                        int[] solution = Solver.pathIndex(maze,
                                maze.getVertexByID(endId),
                                antecedents);
    
                        System.out.println(UNDERLINE + BOLD + "\nSolution found:" + RESET);
                        System.out.println(maze.solutionToString(antecedents,solution));
                    }

                    System.out.println(ITALIC + "Save the maze? " + RESET + BOLD + "[Y/N]" + RESET);
                    saveChoice = sc.nextLine().toLowerCase().trim();

                    if(saveChoice.equals("y")){
                        mazeController.saveMaze();
                    }
                    break;

                default:
                    // If the command is invalid
                    System.out.println("Invalid command");
                    break;


            }
        }
    }
}
