package com.example.projetcynapseing1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.stage.FileChooser;
import java.awt.FileDialog;
import java.awt.Frame;

/**
 * Class used to manage Saving / loading of Mazes Files
 * It should be acceseed in a static way
 * 
 * @author Bari-joris
 * @see Maze
 * @see Serializable
 */
public class FileController {

    /**
     * Method to save maze in a specific file
     * 
     * @param maze object to save
     * @throws Exception throws when trying to save an empty maze
     * @return if saving was succesfull
     */
    public static Boolean SaveDataFX(Maze maze) throws Exception {
        if (maze == null) {
            throw new Exception("--- File Controller ---\nEXCEPTION : maze is null, saving aborted"); // prevent from
                                                                                                      // saving empty
                                                                                                      // files
        }
        try {
            FileChooser dialog = new FileChooser();
            dialog.setInitialFileName("Save a maze");

            dialog.setInitialFileName("maze" + maze.hashCode() + ".ser");

            File selectedFile = dialog.showSaveDialog(null);

            if (selectedFile != null) {
                FileOutputStream fileOut = new FileOutputStream(selectedFile.getAbsolutePath());
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(maze);
                out.close();
                fileOut.close();
                System.out.println("Maze saved with name : " + selectedFile.getName());
                return true;
            } else {
                System.out.println("Saving aborted");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return (false);
        }
    }

    /**
     * Method to save maze in a specific file
     * 
     * @param maze object to save
     * @throws Exception throws when trying to save an empty maze
     * @return if saving was succesfull
     */
    public static Boolean SaveDataCLI(Maze maze) throws Exception {
        if (maze == null) {
            throw new Exception("--- File Controller ---\nEXCEPTION : maze is null, saving aborted"); // prevent from
                                                                                                      // saving empty
                                                                                                      // files
        }
        try {

            FileDialog dialog = new FileDialog((Frame) null, "Save As Maze", FileDialog.SAVE); // open system "save as"
                                                                                               // dialog
            dialog.setFile("maze" + maze.hashCode()); // default file name
            dialog.setVisible(true);
            String directory = dialog.getDirectory();
            String file = dialog.getFile();

            if (file != null && directory != null) {
                String fullPath = directory + file + ".ser";
                FileOutputStream fileOut = new FileOutputStream(fullPath);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(maze);
                out.close();
                fileOut.close();
                System.out.println("Maze saved with name : " + file + ".ser");
                return true;
            } else {
                System.out.println("Saving aborted");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return (false);
        }
    }

    /**
     * method to load a maze from a specific file
     * 
     * @return the Maze loaded
     */
    public static Maze loadMazeFX() {
        Maze emp = null;
        try {
            // Open a system "Open File" dialog
            FileChooser dialog = new FileChooser();
            dialog.setTitle("Select a maze");

            File selectedFile = dialog.showOpenDialog(null);

            // if selected file is not null

            if (selectedFile != null) {
                // File selectedFile = new File(directory, file); // Create a new File Instance
                // with it's directory and
                // name
                System.out.println("Selected file : " + selectedFile.getAbsolutePath());

                /* Deserialization process */
                FileInputStream fileIn = new FileInputStream(selectedFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                emp = (Maze) in.readObject();

                // Close both Input to prevent rewriting
                in.close();
                fileIn.close();
            }
        } catch (IOException i) {
            System.out.println("--- File Controller ---");
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("--- File Controller ---");
            System.out.println("Maze class not found");
            c.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FXController.showAlert("ERROR WHILE LOADING FILE", e.getMessage());
        }
        System.out.println("Maze Loaded");
        System.out.println(emp);
        return emp;
    }

    /**
     * method to load a maze from a specific file
     * 
     * @return the Maze loaded
     */
    public static Maze loadMazeCLI() {
        Maze emp = null;
        try {
            // Open a system "Open File" dialog
            FileDialog dialog = new FileDialog((Frame) null, "Select File to Open", FileDialog.LOAD);
            dialog.setVisible(true);

            String directory = dialog.getDirectory(); // directory of the file
            String file = dialog.getFile(); // name of the file

            // if selected file is not null
            if (file != null) {
                File selectedFile = new File(directory, file); // Create a new File Instance with it's directory and
                                                               // name
                System.out.println("Selected file : " + selectedFile.getAbsolutePath());

                /* Deserialization process */
                FileInputStream fileIn = new FileInputStream(selectedFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                emp = (Maze) in.readObject();

                // Close both Input to prevent rewriting
                in.close();
                fileIn.close();
            }
        } catch (IOException i) {
            System.out.println("--- File Controller ---");
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("--- File Controller ---");
            System.out.println("Maze class not found");
            c.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            FXController.showAlert("ERROR WHILE LOADING FILE", e.getMessage());
        }
        System.out.println("Maze Loaded");
        System.out.println(emp);
        return emp;
    }
}
