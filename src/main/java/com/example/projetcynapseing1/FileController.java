package com.example.projetcynapseing1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileController {

    public FileController() {
    }

    public Boolean SaveData(Maze maze, String fileName) {
        try {
            // new ProcessBuilder("explorer.exe", "/select," + path).start();
            FileOutputStream fileOut = new FileOutputStream(fileName + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(maze);
            out.close();
            fileOut.close();
            System.out.println("Maze saved with name :");
            return (true);
        } catch (IOException e) {
            e.printStackTrace();
            return (false);
        }
    }

    public Boolean loadMaze() {
        Graph emp = null;
        try {
            FileInputStream fileIn = new FileInputStream("test.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            emp = (Graph) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return false;
        }
        System.out.println("Loaded Maze");
        System.out.println(emp);
        return true;
    }
}
