package audio.feature_extraction;

import audio.ComplexVector;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class MFCC {

    //Audio property
    int sampleLength;
    int sampleRate;

    //windowing property
    int samplePerFrame;
    int noOfFrames;
    int samplePerFrameStep;
    double windowsSize = 0.025; // in ms
    double stepSize = 0.01; // in ms
    double lowerFilterFrequency = 80; //in hz
    double higerFilterFrequency; //in hz
    int numMelFilter = 30;

    //Audio data
    double[] audioData;
    double[][] framedAudioData;
    //Audio data in complex format for FFT
    ComplexVector[] complexVectors;

    //mag spectrume
    double[][] magSpectrum;

    //filterBank
    int[] cbin;

    // preEmphasis coefficient
    float preEmphasisCoefficient = 0.95f;


    public MFCC(double[] audioData, int sampleLength, int sampleRate) {
        this.audioData = audioData;
        this.sampleLength = sampleLength;
        this.sampleRate = sampleRate;
        higerFilterFrequency = sampleRate / 2;

        //Based on time
        //samplePerFrame = (int) Math.round(sampleRate * windowsSize);
        //samplePerFrameStep = (int) Math.round(sampleRate * stepSize );

        //Based on nearest power of 2 sample size
        samplePerFrame = 2048; // 2048 / 96000 = 0.021 s | 21ms (frame size)
        samplePerFrameStep = 1024; // 1024 / 96000 = 0.010 | 10ms (overlap frame size)


        noOfFrames = (int) (2 * Math.ceil(sampleLength / samplePerFrame)); // overlap 50% (X2)

        preEmphasis();
        framing();
        windowing();
        fft();

    }

    private void preEmphasis() {
        //Calculate preemphasis per sample
        for (int i = 1; i < audioData.length; i++) {
            audioData[i] = (audioData[i] - preEmphasisCoefficient * audioData[i - 1]);
        }
    }

    // Divide audio sample into overlapping frame
    private void framing() {
        System.out.println("Sample Length : " + sampleLength);
        System.out.println("Time : " + sampleLength / (float) sampleRate);
        System.out.println("No of Frames : " + noOfFrames);
        System.out.println("Sample per Frame : " + samplePerFrame);

        framedAudioData = new double[noOfFrames][samplePerFrame];

        int counter = 0;
        for (int i = 0; i < noOfFrames; i++) {
            int indexFrame = (i * samplePerFrameStep);
            for (int j = 0; j < samplePerFrame; j++) {
                if (counter < sampleLength) {
                    framedAudioData[i][j] = audioData[indexFrame + j];
                    counter++;
                } else {
                    framedAudioData[i][j] = 0;
                }
            }
        }
        System.out.println("Done Framing");
    }

    private void windowing() {
        //
        double[] hammingWindow = new double[sampleLength + 1];

        for (int i = 1; i <= samplePerFrame; i++) {
            hammingWindow[i] = (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
        }

        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
                framedAudioData[i][j] = framedAudioData[i][j] * hammingWindow[j + 1];
            }
        }

        System.out.println("Done Windowing");
    }

    private void fft() {

        complexVectors = new ComplexVector[framedAudioData.length];

        //Compute fft per frames
        //loop per frame
        for (int i = 0; i < framedAudioData.length; i++) {
            complexVectors[i] = new ComplexVector(framedAudioData[i]);
        }
        System.out.println("Done FFT");

        //calculate mag spectrume
        magSpectrum = new double[noOfFrames][framedAudioData.length];
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < framedAudioData.length; j++) {
                magSpectrum[i][j] = Math.sqrt(complexVectors[i].real[j] * complexVectors[i].real[j] + complexVectors[i].imaginary[j] * complexVectors[i].imaginary[j]);
            }
        }
    }

    private void makeFilterBank() {
        cbin = new int[numMelFilter + 2];

        double lowMel = hzToMel(lowerFilterFrequency);
        double highMel = hzToMel(lowerFilterFrequency);

    }

    private double hzToMel(double hz) {
        return 2595*Math.log10(1+hz/700);
    }

    private double melToHz(double x) {
        return 700 * (Math.pow(10, x / 2595) - 1);
    }

}
