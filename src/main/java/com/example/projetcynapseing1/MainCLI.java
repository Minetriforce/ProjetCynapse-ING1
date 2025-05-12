package com.example.projetcynapseing1;

import java.util.ArrayList;
import java.util.Scanner;

public class MainCLI {
    
    public static void main(String args[]){
        MazeController mazeController = new MazeController();

        if (args.length > 0) {
        String command = args[0];
        switch (command) {
            case "cli":
                System.out.println("  ____    ____     ___        _   _____   _____      ____  __   __  _   _      _      ____    ____    _____ \n" + //
                " |  _ \\  |  _ \\   / _ \\      | | | ____| |_   _|    / ___| \\ \\ / / | \\ | |    / \\    |  _ \\  / ___|  | ____|\n" + //
                " | |_) | | |_) | | | | |  _  | | |  _|     | |     | |      \\ V /  |  \\| |   / _ \\   | |_) | \\___ \\  |  _|  \n" + //
                " |  __/  |  _ <  | |_| | | |_| | | |___    | |     | |___    | |   | |\\  |  / ___ \\  |  __/   ___) | | |___ \n" + //
                " |_|     |_| \\_\\  \\___/   \\___/  |_____|   |_|      \\____|   |_|   |_| \\_| /_/   \\_\\ |_|     |____/  |_____|\n");

                Scanner sc = new Scanner(System.in);

                System.out.println("MENU ");
                System.out.println(" 1 - Generate a labyrinth");
                System.out.println(" 2 - Load a labyrinth");

                int choice = sc.nextInt();



                switch(choice){
                    case 1:
                        System.out.println("How would you like to generate it?");
                        System.out.println(" 1 - Prim");
                        System.out.println(" 2 - Kruskal");
                        System.out.println(" 3 - RNG_DFS");
                        
                        int genChoice = sc.nextInt();
                        
                        if (genChoice == 1) {
                            System.out.println("Enter the number of rows:");
                            int rows = sc.nextInt();
                            System.out.println("Enter the number of columns:");
                            int columns = sc.nextInt();
                            System.out.println("Enter a seed (or 0 for random):");
                            int seed = sc.nextInt();
                            
                            // Create a new Maze
                            Maze maze = new Maze(rows, columns,MethodName.GenMethodName.PRIM);

                            // Create the maze using Prim's algorithm
                            mazeController.createMaze(MethodName.GenMethodName.PRIM,
                                    MethodName.Type.COMPLETE, rows, columns, 0.0, seed);
                            
                            // Get the generated maze and copy its edges to the Maze object
                            Graph generatedMaze = mazeController.getCurrentMaze();

                            if (generatedMaze != null) {
                                // Copy all edges from the generated maze to the Maze object
                                for (Edge edge : generatedMaze.getEdges()) {
                                    maze.addEdge(new Edge(
                                        maze.getVertexByIDVertex(edge.getVertexA().getID()),
                                        maze.getVertexByIDVertex(edge.getVertexB().getID())
                                    ));
                                }
                                
                                System.out.println("\nGenerated Maze:");
                                System.out.println(maze);

                                // Solve the maze from top-left to bottom-right
                                Solver solver = new Solver(MethodName.SolveMethodName.ASTAR);
                                int startId = 0;  // Top-left corner
                                int endId = (rows * columns) - 1;  // Bottom-right corner
                                
                                int[] parents = solver.solveAstar(maze, 
                                                                maze.getVertexByIDVertex(startId), 
                                                                maze.getVertexByIDVertex(endId), 
                                                                MethodName.Type.COMPLETE);
                                    
                                int[] solution = Solver.pathIndex(maze, 
                                                                maze.getVertexByIDVertex(endId), 
                                                                parents);
                                    
                                ArrayList<Edge> path_edge = Solver.pathEdge(maze, 
                                                                            maze.getVertexByIDVertex(endId), 
                                                                            parents);

                                System.out.println("\nSolution found:");
                                System.out.println(maze.solutionToString(solution));
                                System.out.println("\nEdges of the path found:");
                                System.out.println(path_edge);
                            }
                        }
                        break;
                    case 2:
                        System.out.println("To do...");
                        break;
                }

                break;
            /* case "save":
                saveLabyrinthe();
                break;
            case "load":
                loadLabyrinthe();
                break; */
            default:
                System.out.println("Commande inconnue");
                break;
        }
    } else {
        System.out.println("Aucune commande spécifiée");
    }

}
}