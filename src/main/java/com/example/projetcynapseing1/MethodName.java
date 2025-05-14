package com.example.projetcynapseing1;

/**
 * The {@code MethodName} class is an abstract utility class that serves as a
 * container
 * for enumerations representing various method names. It is not intended to be
 * instantiated or extended.
 * 
 * <p>
 * The class contains the following enumerations:
 * </p>
 * <ul>
 * <li>{@link GenMethodName} - Represents general method names for algorithms
 * such as Kruskal, Prim's, and DFS.</li>
 * <li>{@link SolveMethodName} - Represents solving method names for algorithms
 * such as A*, Right-hand rule, and Tremaux's method.</li>
 * </ul>
 * 
 * <p>
 * This class is primarily used to organize and group related enumerations for
 * better code readability and maintainability.
 * </p>
 */
public class MethodName {
    /**
     * enumerated type of generation methods
     */
    public enum GenMethodName {
        /**
         * Kruskal generation algorithm (use weighted edges)
         */
        KRUSKAL("Kruskal"),
        /**
         * Prim generation method (use weighted edges)
         */
        PRIM("Prim"),
        /**
         * Random Depth first Search Method
         */
        DFS("DFS"),
        /**
         * this type of generation is completly random and don't ensure the maze to be
         * perfect
         */
        UNPERFECT("Unperfect");

        // name of the method
        private final String name;

        /**
         * constructor
         * 
         * @param n: name
         */
        private GenMethodName(String n) {
            name = n;
        }

        /**
         * getter of name
         * 
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * return the method name corresponding to the name given
         * default: PRIMS
         * 
         * @param n: name of the method
         * @return method name
         */
        public GenMethodName fromName(String n) {
            for (GenMethodName m : values()) {
                if (m.getName().equals(n)) {
                    return m;
                }
            }
            return PRIM;
        }
    }

    /**
     * enumerated type of solving methods
     */
    public enum SolveMethodName {

        /**
         * A star resolution method algorithm
         */
        ASTAR("A*"),

        /**
         * Right hand resolution algorithm
         */
        RIGHTHAND("Right hand"),

        /**
         * Trémaux resolution algorithm
         */
        TREMAUX("Tremaux");

        // name of the method
        private final String name;

        /**
         * constructor
         * 
         * @param n: name
         */
        private SolveMethodName(String n) {
            name = n;
        }

        /**
         * getter of name
         * 
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * return the method name corresponding to the name given
         * default: ASTAR
         * 
         * @param n: name of the method
         * @return method name
         */
        public SolveMethodName fromName(String n) {
            for (SolveMethodName m : values()) {
                if (m.getName().equals(n)) {
                    return m;
                }
            }
            return ASTAR;
        }
    }

    /**
     * enumerated type of printing
     */
    /**
     * The {@code Type} enum represents the types of methods available.
     * Each type is associated with a specific name.
     * <p>
     * The available types are:
     * <ul>
     * <li>{@link #STEPPER} - Represents the "stepper" method.</li>
     * <li>{@link #COMPLETE} - Represents the "complete" method.</li>
     * </ul>
     * <p>
     * This enum provides methods to retrieve the name of a type and to find a type
     * based on its name. If no matching type is found, the default type
     * {@link #COMPLETE}
     * is returned.
     */
    public enum Type {
        /**
         * Step-by-step generation/solution.
         * Used to display step generation / solution step-by-step, it incldues the fact
         * the time Step is not null
         */
        STEPPER("stepper"),

        /**
         * Complete generation/solution.
         * Used to display directly the generation/solution or hide it behind a progress
         * bar
         */
        COMPLETE("complete");

        // name of the method
        private final String name;

        /**
         * constructor
         * 
         * @param n: name
         */
        private Type(String n) {
            name = n;
        }

        /**
         * getter of name
         * 
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * return the method name corresponding to the name given
         * default: COMPLETE
         * 
         * @param n: name of the method
         * @return method name
         */
        public Type fromName(String n) {
            for (Type m : values()) {
                if (m.getName().equals(n)) {
                    return m;
                }
            }
            return COMPLETE;
        }
    }
}
