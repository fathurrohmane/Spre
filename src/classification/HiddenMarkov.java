package classification;

import tools.Array;

import java.util.Random;

/**
 * Created by Fathurrohman on 08-Dec-15.
 * based on https://www.cs.sjsu.edu/~stamp/RUA/HMM.pdf
 */
public class HiddenMarkov {

    private double mThresholdProbability;

    /**
     * Length of observation sequence
     * example {1,3,2,2,1,2} = length is 5
     */
    private int mLengthofObservation;

    /**
     * Number of State
     */
    private int mNumberofState;

    /**
     * Number of Observation Symbol
     * example {1,3,2,2,1,2} = number is 3 (1,2,3)
     */
    private int mNumberofObservationSymbol;

    /**
     * State
     */
    private int[] mStates;

    /**
     * Set of possible Observation
     * example {1,3,2,2,1,2} = {1,2,3}
     */
    private int[] mSetofPossibleObservation;

    /**
     * Scale factor to prevent underflow used in reestimate
     */
    private double[] scaleFactor;

    /**
     * alpha
     * matrix size is [Number of State * Number of State]
     */
    private double[][] alpha;

    /**
     * beta
     * matrix size is [Number of State * Number of Observation Symbol]
     */
    private double[][] beta;


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

    /**
     * Observation Sequence
     */
    private int[] mObservation;

    public HiddenMarkov(int[] observation, int numberofObservationSymbol, String word) {
        this.mObservation = new int[observation.length];
        Array.copy(observation, mObservation);
        this.mLengthofObservation = mObservation.length;
        this.mNumberofObservationSymbol = numberofObservationSymbol;
        this.mNumberofState = 5;// FIXME: 11-Dec-15
        scaleFactor = new double[mLengthofObservation];

    }

    public void training() {

        // 1.Initialize
        double probability = 0;

        initialize();

        do {
            double currentProbability = 0;
            // 2.Compute alpha,beta
            computeAlpha();
            computeBeta();
            reestimate();

        } while (true);

        // Compute alpha(i)

        // Compute beta(i)

        // Compute gamma(i,j) gamma (i)

        // Re-estimate model

        // if P(O, λ) increase below threshold repeate to 2
    }

    private void initialize() {
        /**
         * Initialize phi
         * Left-Right HMM always start at initial state so state 1 probability = 1
         */
        System.out.println("Initialize Value:");
        System.out.println(" phi / initial probability :");
        phi = new double[mNumberofState];

        for (int i = 0; i < mNumberofState; i++) {

            if (i == 0) {
                phi[i] = 1;
            } else {
                phi[i] = 0;
            }
            System.out.print(phi[i] + " ");
        }
        System.out.println("");
        /**
         * Initialize alpha / transition probability
         */
        System.out.println("Alpha :");
        aTransition = new double[mNumberofState][mNumberofState];
        for (int i = 0; i < mNumberofState; i++) {
            double prob = 0.0;
            for (int j = 0; j < mNumberofState; j++) {
                aTransition[i][j] = 0;
                if (i == j) {
                    prob = Math.random();
                    aTransition[i][j] = prob;
                } else if (j == (i + 1)) {
                    aTransition[i][j] = 1 - prob;
                }
                if ((i == (mNumberofState - 1)) && (j == (mNumberofState - 1))) {
                    aTransition[i][j] = 1;
                }
                System.out.print(aTransition[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("is alpha stocastic :" + matrixStochasicChecker(aTransition));

        /**
         * Observation State Distribution
         */
        System.out.println("Beta :");
        bOutput = new double[mNumberofState][mNumberofObservationSymbol];
        for (int i = 0; i < mNumberofState; i++) {
            double remainValue = 0.0;
            for (int j = 0; j < mNumberofObservationSymbol; j++) {
                if (j == 0) {
                    double prob = Math.random();
                    bOutput[i][j] = prob;
                    remainValue = 1 - prob;
                } else if (j == (mNumberofObservationSymbol - 1)) {
                    bOutput[i][j] = remainValue;
                } else {
                    double prob = randomBetween(remainValue, 0.0);
                    bOutput[i][j] = prob;
                    remainValue -= prob;
                }
                System.out.print(bOutput[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("is beta stocastic :" + matrixStochasicChecker(bOutput));
    }

    private void computeAlpha() {
        /**
         * reset scale
         */
        for (int i = 0; i < mLengthofObservation; i++) {
            scaleFactor[i] = 0;
        }

        /**
         * compute alpha(0) / initialize alpha
         */
        for (int i = 0; i < mNumberofState; i++) {
            alpha[0][i] = phi[i]*beta[i][mObservation[0]];
            scaleFactor[0] += alpha[0][i];
        }
        rescaleAlpha(0);

        /**
         * compute alpha(t)
         */

        for (int i = 1; i < mLengthofObservation; i++) {
            scaleFactor[i] = 0;
            for (int j = 0; j < mNumberofState; j++) {
                alpha[i][j] = 0;
                for (int k = 0; k < mNumberofState; k++) {
                    alpha[i][j] += alpha[i - 1][k]*aTransition[k][j];
                }
                alpha[i][j] = alpha[i][j]*bOutput[j][mObservation[i]];
                scaleFactor[i] += alpha[i][j];
            }
            rescaleAlpha(i);
        }
    }

    private void computeBeta() {

        for (int i = 0; i < mNumberofState; i++) {
            beta[mLengthofObservation - 1][i] = scaleFactor[mNumberofState - 1];
        }

        for (int i = mNumberofObservationSymbol - 2; i > 0; i--) {
            for (int j = 0; j < mNumberofState - 1; j++) {
                beta[i][j] = 0;
                for (int k = 0; k < mNumberofState; k++) {
                    beta[i][j] += aTransition[j][k]*bOutput[k][mObservation[i + 1]] * beta[i + 1][k];
                }
                beta[i][j] *= scaleFactor[i];
            }
        }
    }

    private void rescaleAlpha(int t) {

        scaleFactor[t] = 1 / scaleFactor[t];

        for (int i = 0; i < mNumberofState; i++) {
            alpha[t][i] = scaleFactor[t]*alpha[t][i];
        }
    }

    private void reestimate() {
        double[][] y = new double[mNumberofState][mNumberofState];
        for (int i = 0; i < mLengthofObservation - 1; i++) {
            double denominator = 0;
            for (int j = 0; j < mNumberofState; j++) {
                for (int k = 0; k < mNumberofState; k++) {
                    denominator += alpha[i][j]*aTransition[j][k]
                            *bOutput[k][i + 1]*beta[i + 1][k];
                }
            }

            for (int j = 0; j < mNumberofState; j++) {
                y[i][j] = 0;
                for (int k = 0; k < mNumberofState; k++) {
                    // FIXME: 18-Dec-15
                }
            }
        }
    }

    public boolean matrixStochasicChecker(double[][] input) {
        boolean result = true;

        for (int i = 0; i < input.length; i++) {
            double prob = 0.0;
            for (int j = 0; j < input[0].length; j++) {
                prob += input[i][j];
            }
            if (prob != 1.0) {
                result = false;
            }
        }

        return result;
    }

    public double randomBetween(double max, double min) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }
}
