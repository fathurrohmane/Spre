package classification;

import tools.array.Array;

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
     * Current Observation Sequence
     */
    private int[] mCurrentObservation;

    /**
     * Length of current observation sequence
     */
    private int mLenghtofCurrentObservationSequence;
    /**
     * All Observation Sequence
     */
    private int[][] mObservations;

    public HiddenMarkov(String word, int mNumberofState) {
        // Set database

        this.mNumberofState = mNumberofState;

    }

    /**
     * Set training sequence
     *
     * @param observationSymbol
     * @param numberOfObservationSymbol
     */
    public void setTrainingObservation(int[][] observationSymbol, int numberOfObservationSymbol) {
        this.mNumberofObservationSymbol = numberOfObservationSymbol;
        // TODO: 21-Dec-15 fix different size of 2nd dimension array
        mObservations = new int[observationSymbol.length][observationSymbol[0].length];
        Array.copy2D(observationSymbol, mObservations);
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
        scaleFactor = new double[mLengthofObservation];

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
            double sum = 0.0;
            int[] temp = new int[3]; // only 1 or 2 state can be jumped at once

            if (((mNumberofState - 1) - i) == 1) {
                for (int j = 0; j < 2; j++) {
                    temp[j] = randomBetween(100, 0);
                    sum += temp[j];
                }
                aTransition[i][mNumberofState - 2] = temp[0] / sum;
                aTransition[i][mNumberofState - 1] = temp[1] / sum;

            } else if (((mNumberofState - 1) - i) == 0) {
                aTransition[i][mNumberofState - 1] = 1;
            } else {
                for (int j = 0; j < 3; j++) {
                    temp[j] = randomBetween(100, 0);
                    sum += temp[j];
                }
                aTransition[i][i] = temp[0] / sum;
                aTransition[i][i + 1] = temp[1] / sum;
                aTransition[i][i + 2] = temp[2] / sum;
            }
        }
        for (int j = 0; j < mNumberofState; j++) {
            // Left-Right model with 1/2 jump per-state
            for (int k = 0; k < mNumberofState; k++) {
                System.out.print(aTransition[j][k] + " ");
            }
            System.out.println();
        }
        System.out.println("is alpha stocastic :" + matrixStochasicChecker(aTransition));

        /**
         * Observation State Distribution
         *
         */
        System.out.println("Beta :");
        bOutput = new double[mNumberofState][mNumberofObservationSymbol];
        for (int i = 0; i < mNumberofState; i++) {
            int[] temp = new int[mNumberofObservationSymbol];
            double sum = 0.0;
            for (int j = 0; j < mNumberofObservationSymbol; j++) {
                temp[j] = randomBetween(100, 0);
                sum += temp[j];
            }
            for (int j = 0; j < mNumberofObservationSymbol; j++) {
                bOutput[i][j] = temp[j] / sum;
                System.out.print(bOutput[i][j] + " ");
            }

            System.out.println();
        }
        System.out.println("is beta stocastic :" + matrixStochasicChecker(bOutput));
        System.out.println();
    }

    /**
     * Forward Algorithm
     */
    private double computeAlpha() {
        /**
         * reset scale
         */
        for (int i = 0; i < mLengthofObservation; i++) {
            scaleFactor[i] = 0;
        }

        /**
         * compute alpha(0) / Initialization:
         */
        for (int i = 0; i < mNumberofState; i++) {
            alpha[0][i] = phi[i] * bOutput[i][mCurrentObservation[0]];
            scaleFactor[0] += alpha[0][i];
        }
        rescaleAlpha(0);

        /**
         * compute alpha(t) / Recursion:
         */

        for (int i = 1; i < mLengthofObservation; i++) {
            scaleFactor[i] = 0;
            for (int j = 0; j < mNumberofState; j++) {
                alpha[i][j] = 0;
                for (int k = 0; k < mNumberofState; k++) {
                    alpha[i][j] += alpha[i - 1][k] * aTransition[k][j];
                }
                alpha[i][j] = alpha[i][j] * bOutput[j][mCurrentObservation[i]];
                scaleFactor[i] += alpha[i][j];
            }
            rescaleAlpha(i);
        }
        //TODO Count probability using log-scale
        double probability = 0;

        // calculate probability P(O|Model) =
        for (int i = 0; i < mNumberofState; i++) {
            probability += alpha[mLengthofObservation - 1][i];
        }

        return probability;
    }

    private void rescaleAlpha(int t) {

        scaleFactor[t] = 1 / scaleFactor[t];

        for (int i = 0; i < mNumberofState; i++) {
            alpha[t][i] = scaleFactor[t] * alpha[t][i];
        }
        System.out.println();
    }

    /**
     * Backward algorithm
     */

    private void computeBeta() {

        /**
         * Initialization :
         */
        for (int i = 0; i < mNumberofState; i++) {
            // TODO: 21-Dec-15 decide these two line
            //beta[mLengthofObservation - 1][i] = scaleFactor[mNumberofState - 1];
            beta[mLengthofObservation - 1][i] = 1;
        }

        /**
         * Recursion :
         */

        for (int t = mNumberofObservationSymbol - 2; t >= 0; t--) {
            for (int i = 0; i < mNumberofState - 1; i++) {
                beta[t][i] = 0;
                for (int j = 0; j < mNumberofState; j++) {
                    beta[t][i] += aTransition[i][j] * bOutput[j][mCurrentObservation[t + 1]] * beta[t + 1][j];
                }
                // Rescale beta
                beta[t][i] *= scaleFactor[t];
            }
        }
    }

    public void setCurrentObservationSequence(int[] sequence) {

        this.mCurrentObservation = sequence;
        this.mLenghtofCurrentObservationSequence = sequence.length;

        alpha = new double[mNumberofState][mNumberofState];
        beta = new double[mNumberofState][mNumberofState];
        scaleFactor = new double[mNumberofObservationSymbol];

    }

    private void reestimate() {
        // Version 1
//        double[][] y = new double[mNumberofState][mNumberofState];
//        for (int i = 0; i < mLengthofObservation - 1; i++) {
//            double denominator = 0;
//            for (int j = 0; j < mNumberofState; j++) {
//                for (int k = 0; k < mNumberofState; k++) {
//                    denominator += alpha[i][j] * aTransition[j][k]
//                            * bOutput[k][i + 1] * beta[i + 1][k];
//                }
//            }
//
//            for (int j = 0; j < mNumberofState; j++) {
//                y[i][j] = 0;
//                for (int k = 0; k < mNumberofState; k++) {
//                    // FIXME: 18-Dec-15
//                }
//            }
//        }

        // Version 2

        double[][] newTransition = new double[mNumberofState][mNumberofState];
        double[][] newOutput = new double[mNumberofState][mNumberofObservationSymbol];
        double[] numerator = new double[mLenghtofCurrentObservationSequence];
        double[] denominator = new double[mLenghtofCurrentObservationSequence];

        // Calculate new Transition Probability

        double probability = 0;

        for (int i = 0; i < mNumberofState; i++) {
            for (int j = 0; j < mNumberofState; j++) {
                if (j < i || j > i + 2) {
                    newTransition[i][j] = 0;
                } else {
                    for (int k = 0; k < mObservations.length; k++) { // TODO: 22-Dec-15 change to variable
                        numerator[k] = 0;
                        denominator[k] = 0;

                        probability += computeAlpha();
                        computeBeta();
                        for (int l = 0; l < mLenghtofCurrentObservationSequence - 1; l++) {
                            numerator[k] += alpha[l][i] * aTransition[i][j] * bOutput[j][l + 1] * beta[l + 1][j];
                            denominator[k] += alpha[l][i] * beta[l][i];
                        }
                    }

                    double denom = 0;
                    for (int k = 0; k < mObservations.length; k++) {
                        newTransition[i][j] += (1 / probability) * numerator[k];
                        denom += (1 / probability) * denominator[k];
                    }
                    newTransition[i][j] /= denom;
                    newTransition[i][j] += 0.0001; // TODO: 22-Dec-15 find out why need to add 0.0001 / min_probability
                }
            }
        }

        // Calculate new Output

    }

    public boolean matrixStochasicChecker(double[][] input) {
        boolean result = true;

        for (int i = 0; i < input.length; i++) {
            double prob = 0.0;
            for (int j = 0; j < input[0].length; j++) {
                prob += input[i][j];
            }
            if (prob != 1.0) {
                System.out.println(prob);
                result = false;
            }
        }

        return result;
    }

    private int randomBetween(int max, int min) {
        Random r = new Random();
        return (int) Math.round(min + (max - min) * r.nextDouble());
    }

    private double randomBetween(double max, double min) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }
}
