package classification;

import audio.AudioProcessing;
import audio.feature_extraction.MFCC;
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

    public static void CodeBookGenerator(String word, int cluster, File soundFile) {
        // array list ceptra
        List<Point> ceptra = new ArrayList<Point>();

        for (File file : soundFile.listFiles()
                ) {
            // Audio Processing
            AudioProcessing audioProcessing = new AudioProcessing(file);

            // MFCC
            MFCC mfcc = new MFCC(audioProcessing.getAudioData()
                    , audioProcessing.getAudioSampleRate()
            );

            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }
        }

//        VectorQuantization vectorQuantization = new VectorQuantization(word, ceptra, cluster);
//        vectorQuantization.saveToDatabase();

        GenLloyd g = new GenLloyd(ceptra);
        g.calcClusters(256);

        System.out.println("Codebook " + word + " is created");
    }
}
