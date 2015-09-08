package audio;

import audio.feature_extraction.MFCC;
import tools.ArrayWriter;
import tools.WaveData;

import java.io.File;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class AudioProcessing {

    //Audio properties
    int audioSampleLength;
    int audioSampleRate = 96000;
    double audioDuration;

    //Audio file data
    File audioFile;

    //Audio extractor
    WaveData waveData;

    //Audio data original
    float[] audioDataOriginal;

    //Audio data
    double[] audioData;

    public AudioProcessing(File file) {
        this.audioFile = file;
        getAudioData(file);

        //Preprosessing data
        audioData = PreProcessing.normalizeAudioData(audioDataOriginal);

        //set audio length
        audioSampleLength = audioData.length;

        //MFCC
        MFCC mfcc = new MFCC(audioData,audioSampleLength,audioSampleRate);

    }

    private void getAudioData(File file) {
        waveData = new WaveData();
        audioDataOriginal = waveData.extractAmplitudeFromFile(file);
    }

    private void calculateMFCCperFrame() {

    }




}
