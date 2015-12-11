package test;

import classification.HiddenMarkov;

/**
 * Created by Fathurrohman on 11-Dec-15.
 */
public class ManualTestHMM {

    public static void main(String[] args) {

        int[] observation = new int[5];
            observation[0] = 1;
            observation[1] = 2;
            observation[2] = 2;
            observation[3] = 1;
            observation[4] = 3;

        HiddenMarkov hmm = new HiddenMarkov(observation,3,"test");
    }

}
