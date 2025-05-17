package com.example.projetcynapseing1;

public class TestMain {
    public static void main(String args[]){
        MazeController mazeController = new MazeController();
        mazeController.loadMaze();
        //mazeController.createMaze(MethodName.GenMethodName.KRUSKAL,MethodName.Type.COMPLETE, 10, 10, 0.0, 5);
        Maze maze = mazeController.getCurrentMaze();
        //mazeController.saveMaze();
        System.out.println(maze);
        
    }

}
