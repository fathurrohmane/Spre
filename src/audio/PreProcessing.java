package audio;

import audio.feature_extraction.MFCC;
import tools.ArrayWriter;

/**
 * Created by Fathurrohman on 5/29/2015.
 */
public class PreProcessing {

    public static double[] normalizeAudioData(float[] audioDataOriginal) {
        //Initialize audio data variable
        double[] audioData = new double[audioDataOriginal.length / 2];

        //Max and Min Data
        float maxData = Float.MIN_VALUE;
        float minData = Float.MAX_VALUE;

        //Max and Min data after normalize
        double max = 1;
        double min = -1;

        //Find max and min audio data
        for (int i = 0; i <audioDataOriginal.length ; i++) {
            if(audioDataOriginal[i] <= minData) {
                minData = audioDataOriginal[i];
            }
            if(audioDataOriginal[i] >= maxData) {
                maxData = audioDataOriginal[i];
            }
        }

        //Normalization
        int counter = 0;

        for (int i = 0; i <audioDataOriginal.length ; i+=2) {
            audioData[counter] = ((audioDataOriginal[i] - minData) / (maxData - minData) - 0.5) * 2;
            counter++;
        }

        //Print data
//        ArrayWriter.printFloatArrayToFile(audioDataOriginal, "test.txt");// FIXME: 28-Dec-15 
//        ArrayWriter.printDoubleArrayToFile(audioData, "test_normalize.txt");
        //ArrayWriter.printDoubleArrayToFile(AudioExtractor.getAudioData(audioFile), "test_audioExtractor.txt");

        // Return audio data after normalization
        return audioData;

    }


}
