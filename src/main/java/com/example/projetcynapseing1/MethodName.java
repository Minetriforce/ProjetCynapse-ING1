/**
 * The {@code MethodName} class is an abstract utility class that serves as a container
 * for enumerations representing various method names. It is not intended to be 
 * instantiated or extended.
 * 
 * <p>The class contains the following enumerations:</p>
 * <ul>
 *   <li>{@link GenMethodName} - Represents general method names for algorithms such as Kruskal, Prim's, and DFS.</li>
 *   <li>{@link SolveMethodName} - Represents solving method names for algorithms such as A*, Right-hand rule, and Tremaux's method.</li>
 * </ul>
 * 
 * <p>This class is primarily used to organize and group related enumerations for 
 * better code readability and maintainability.</p>
 */
package com.example.projetcynapseing1;

public class MethodName {
    public enum GenMethodName {
        KRUSKAL, PRIMS, DFS
    }

    public enum SolveMethodName {
        ASTAR, RIGHTHAND, TREMAUX
    }

    public enum Type {
        STEPPER, COMPLETE
    }
}
