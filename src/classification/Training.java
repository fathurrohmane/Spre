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
public class Training implements Runnable{

    public Training(String word,int cluster, File soundFile) {
        // Audio Processing
        AudioProcessing audioProcessing = new AudioProcessing(soundFile);

        // MFCC
        MFCC mfcc = new MFCC(audioProcessing.getAudioData()
                ,audioProcessing.getAudioSampleRate()
        );
        double[][] ceptra = mfcc.getCeptra();
        Array.print("Ceptra from MFCC", ceptra);

        // Vector quantization
        VectorQuantization vq = new VectorQuantization(word,ceptra,cluster);
        vq.print();
        int[] observation = new int[vq.getSampleSize()];
        Array.copy(vq.getObservation(), observation);
        Array.print("Observation :", observation);

        // HMM
        HiddenMarkov hmm = new HiddenMarkov(observation,cluster, word);

    }

    @Override
    public void run() {

    }
}
