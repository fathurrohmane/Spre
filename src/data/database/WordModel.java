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
}
