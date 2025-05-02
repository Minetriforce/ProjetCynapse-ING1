package com.example.projetcynapseing1;

import com.example.projetcynapseing1.MethodName.SolveMethodName;

/**
 * <p>
 * Class Solver is used to solve mazes with a specific method and a timestep.
 * <p/>
 * 
 * @author Junjie
 */
public class Solver {
    // name of the method used to solve the maze
    private SolveMethodName method;
    // time step used in the solving process, must be >= 0
    private Float timeStep;

    /**
     * constructor of Solver object
     * @param m: method's name
     * @param t: time step
     */
    public Solver(SolveMethodName m, Float t){
        method = m;
        timeStep = t >= 0 ? t : 0f;
    }
    /**
     * constructor of Solver object
     */
    public Solver(SolveMethodName m){
        this(m, 0f);
    }

    /**
     * getter of method
     * @return method
     */
    public SolveMethodName getMethod() {
        return method;
    }
    /**
     * getter of timeStep
     * @return timeStep
     */
    public Float getTimeStep() {
        return timeStep;
    }

    /**
     * setter of method
     * @param m: new method
     */
    public void setMethod(SolveMethodName m) {
        method = m;
    }
    /**
     * setter of timeStep
     * @param t: new timeStep
     */
    public void setTimeStep(Float t) {
        timeStep = t >= 0 ? t : 0f;
    }

    /**
     * @return method and time step in a String format
     */
    @Override
    public String toString(){
        return "Solver: " + method.toString() + "\ntime step: " + timeStep.toString() + "s";
    }
}
