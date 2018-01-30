package test;

import classification.HiddenMarkov;

/**
 * Created by Fathurrohman on 11-Dec-15.
 */
public class ManualTestHMM {

    public static void main(String[] args) {

        int[][] observation = new int[2][5];
        observation[0][0] = 0;
        observation[0][1] = 1;
        observation[0][2] = 1;
        observation[0][3] = 0;
        observation[0][4] = 2;

        observation[1][0] = 1;
        observation[1][1] = 1;
        observation[1][2] = 0;
        observation[1][3] = 1;
        observation[1][4] = 1;


        HiddenMarkov hmm = new HiddenMarkov("Test",5);
        hmm.setTrainingObservation(observation,10);
        hmm.training();
    }

}
