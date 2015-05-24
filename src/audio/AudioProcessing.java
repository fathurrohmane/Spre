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
        normalizeAudioData();
    }

    private void getAudioData(File file) {
        waveData = new WaveData();
        audioDataOriginal = waveData.extractAmplitudeFromFile(file);
        normalizeAudioData();
    }

    private void normalizeAudioData() {
        //Initialize audio data variable
        audioData = new double[audioDataOriginal.length / 2];

        //Max and Min Data
        float maxData = -10000;
        float minData = 10000;

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
        //http://www.howcast.com/videos/359111-how-to-normalize-data/
        int counter = 0;

        for (int i = 0; i <audioDataOriginal.length ; i+=2) {
            audioData[counter] = ((audioDataOriginal[i] - minData) / (maxData - minData) - 0.5) * 2;
            counter++;
        }

        //calculate number of sample length, and audioduration
        audioSampleLength = counter;
        audioDuration = audioSampleLength / audioSampleRate;

        //Print data
        ArrayWriter.printFloatArrayToFile(audioDataOriginal, "test.txt");
        ArrayWriter.printDoubleArrayToFile(audioData, "test_normalize.txt");
        //ArrayWriter.printDoubleArrayToFile(AudioExtractor.getAudioData(audioFile), "test_audioExtractor.txt");

        //MFCC
        MFCC mfcc = new MFCC(audioData,audioSampleLength,audioSampleRate);

    }


}
