package data.database;

import java.io.Serializable;

/**
 * Created by Fathurrohman on 17-Dec-15.
 */
public class WordModel implements Serializable {

    /**
     * Hidden Markov Model λ = (a,b,phi)
     */

    /**
     * State Transition Probabilities
     * matrix size is [Number of State * Number of State]
     */
    private double[][] aTransition;

    /**
     * Observation State Distribution / Emission
     * matrix size is [Number of State * Number of Observation Symbol]
     */
    private double[][] bOutput;

    /**
     * Initial State Distribution
     * Matix size is [1 * mNumberofState]
     */
    private double[] phi;

    //
    private int numOfState;

    private int numofSymbol;

    public WordModel() {

    }

    public WordModel(double[][] aTransition, double[][] bOutput, double[] phi) {
        this.aTransition = aTransition;
        this.bOutput = bOutput;
        this.phi = phi;
    }

    public double[][] getaTransition() {
        return aTransition;
    }

    public void setaTransition(double[][] aTransition) {
        this.aTransition = aTransition;
    }

    public double[][] getbOutput() {
        return bOutput;
    }

    public void setbOutput(double[][] bOutput) {
        this.bOutput = bOutput;
    }

    public double[] getPhi() {
        return phi;
    }

    public void setPhi(double[] phi) {
        this.phi = phi;
    }

    public int getNumOfState() {
        return numOfState;
    }

    public void setNumOfState(int numOfState) {
        this.numOfState = numOfState;
    }

    public int getNumofSymbol() {
        return numofSymbol;
    }

    public void setNumofSymbol(int numofSymbol) {
        this.numofSymbol = numofSymbol;
    }
}
