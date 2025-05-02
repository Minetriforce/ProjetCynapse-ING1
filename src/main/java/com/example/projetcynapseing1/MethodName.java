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
    // enumerated type of generation methods
    public enum GenMethodName {
        KRUSKAL("Kruskal"), PRIMS("Prims"), DFS("DFS");

        // name of the method
        private final String name;

        /**
         * constructor
         * @param n: name
         */
        private GenMethodName(String n) {
            name = n;
        }

        /**
         * getter of name
         * @return name
         */
        public String getName(){
            return name;
        }

        /**
         * return the method name corresponding to the name given
         * default: PRIMS
         * @param n: name of the method
         * @return method name
         */
        public GenMethodName fromName(String n){
            for (GenMethodName m: values()){
                if (m.getName().equals(n)){
                    return m;
                }
            }
            return PRIMS;
        }
    }

    // enumerated type of solving methods
    public enum SolveMethodName {
        ASTAR("Astar"), RIGHTHAND("Right hand"), TREMAUX("Tremaux");

        // name of the method
        private final String name;

        /**
         * constructor
         * @param n: name
         */
        private SolveMethodName(String n) {
            name = n;
        }

        /**
         * getter of name
         * @return name
         */
        public String getName(){
            return name;
        }

        /**
         * return the method name corresponding to the name given
         * default: ASTAR
         * @param n: name of the method
         * @return method name
         */
        public SolveMethodName fromName(String n){
            for (SolveMethodName m: values()){
                if (m.getName().equals(n)){
                    return m;
                }
            }
            return ASTAR;
        }
    }

    // enumerated type of printing
    public enum Type {
        STEPPER("stepper"), COMPLETE("complete");

        // name of the method
        private final String name;

        /**
         * constructor
         * @param n: name
         */
        private Type(String n) {
            name = n;
        }

        /**
         * getter of name
         * @return name
         */
        public String getName(){
            return name;
        }

        /**
         * return the method name corresponding to the name given
         * default: COMPLETE
         * @param n: name of the method
         * @return method name
         */
        public Type fromName(String n){
            for (Type m: values()){
                if (m.getName().equals(n)){
                    return m;
                }
            }
            return COMPLETE;
        }
    }
}
