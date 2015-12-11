package classification;

import audio.AudioProcessing;
import audio.feature_extraction.MFCC;
import data.vectorquantization.LBG.VectorQuantization;
import tools.Array;

import java.io.File;

/**
 * Created by Fathurrohman on 08-Dec-15.
 * Class Training will handle all the process for speech recognition in Training part
 * The process is
 * 1. Get audio data -> AudioProcessing.java -> get audiodata in double format
 * 2. Get MFCC feature -> MFCC.java -> get feature vector of audiodata in double format
 */
public class Training {


    public Training(String word, File soundFile) {
        // Audio Processing
        AudioProcessing audioProcessing = new AudioProcessing(soundFile);

        // MFCC
        MFCC mfcc = new MFCC(audioProcessing.getAudioData()
                ,audioProcessing.getAudioSampleRate()
        );

        double[][] ceptra = mfcc.getCeptra();
        Array.print("Ceptra from MFCC", ceptra);
        // Vector quantization
        VectorQuantization vq = new VectorQuantization(ceptra,4);
        vq.print();
        int[] obseration = new int[vq.getSampleSize()];
        Array.copy(vq.getObservation(), obseration);
        Array.print("Observation :", obseration);
        // HMM
        HiddenMarkov hmm = new HiddenMarkov(obseration,4, word);


    }
}
