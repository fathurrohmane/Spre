package classification;

import audio.AudioProcessing;
import audio.feature_extraction.MFCC;
import data.vectorquantization.LBG.LBG;
import data.vectorquantization.LBG.Point;
import data.vectorquantization.LBG.VectorQuantization;
import test.validator.mkonrad.GenLloyd;
import tools.Array;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fathurrohman on 08-Dec-15.
 * Class Training will handle all the process for speech recognition in Training part
 * The process is
 * 1. Get audio data -> AudioProcessing.java -> get audiodata in double format
 * 2. Get MFCC feature -> MFCC.java -> get feature vector of audiodata in double format
 */
public class Training {

    public Training(String word, int cluster, File soundFile) {

        // Audio Processing
        AudioProcessing audioProcessing = new AudioProcessing(soundFile);

        // MFCC
        MFCC mfcc = new MFCC(audioProcessing.getAudioData()
                , audioProcessing.getAudioSampleRate()
        );
        double[][] ceptra = mfcc.getCeptra();
        Array.print("Ceptra from MFCC", ceptra);

        // Vector quantization
        VectorQuantization vq = new VectorQuantization(word, ceptra, cluster);
        vq.print();
        int[] observation = new int[vq.getSampleSize()];
        Array.copy(vq.getObservation(), observation);
        Array.print("Observation :", observation);

        // HMM
        HiddenMarkov hmm = new HiddenMarkov(word, 4);
    }

    /** Static method to create new codebook
     *
     * @param word name of word that spoken in sound file
     * @param cluster number of cluster or observation symbol
     * @param soundFile file
     */
    public static void start(String word, int cluster, File soundFile) {
        // array list ceptra
        MFCC[] mfcc = new MFCC[soundFile.listFiles().length];
        List<Point> ceptra = new ArrayList<Point>(); // TODO: 30-Dec-15 optimize this
        int counterFile = 0;

        for (File file :
                soundFile.listFiles() ) {
            // Audio Processing
            AudioProcessing audioProcessing = new AudioProcessing(file);

            // MFCC
            mfcc[counterFile] = new MFCC(audioProcessing.getAudioData()
                    , audioProcessing.getAudioSampleRate()
            );
            for (int i = 0; i < mfcc[counterFile].getCeptra().length; i++) {
                ceptra.add(new Point(mfcc[counterFile].getCeptra()[i]));
            }
            counterFile++;

        }
        // Create codebook
        LBG lbg = new LBG(ceptra);
        lbg.calculateCluster(cluster);
        lbg.saveToDatabase(word);

        // Create Observation Sequence
        int[][] observation = new int[counterFile][];

        for (int i = 0; i < mfcc.length; i++) {
            observation[i] = lbg.getObservationSequence(mfcc[i]);
        }

        Array.print("Observation", observation);

        // Create HMM
        test.validator.hmm.HiddenMarkov hiddenMarkov = new test.validator.hmm.HiddenMarkov(8, cluster); // FIXME: 30-Dec-15
        //hiddenMarkov.setTrainSeq();
    }
}
