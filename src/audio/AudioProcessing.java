package audio;

import tools.WaveData;

import javax.sound.sampled.AudioFormat;
import java.io.File;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class AudioProcessing {

    // Audio properties
    private int audioSampleRate;
    private double audioDuration;

    // Audio file data
    private File audioFile;

    // Audio extractor
    private WaveData waveData;

    // Audio data original
    private float[] audioDataOriginal;

    // Audio data
    private double[] audioData;

    public AudioProcessing(File file) {
        this.audioFile = file;
        getAudioData(file);

        // Pre-processing data
        audioData = PreProcessing.normalizeAudioData(audioDataOriginal);

    }

    private void getAudioData(File file) {
        waveData = new WaveData();
        audioDataOriginal = waveData.extractAmplitudeFromFile(file);
        audioDuration = waveData.getDurationSec();
        AudioFormat audioFormat = waveData.getFormat();
        audioSampleRate = (int) audioFormat.getSampleRate();
    }

    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    public double[] getAudioData() {
        return audioData;
    }
}
