package audio.feature_extraction;

import data.Complex;
import data.FFT;
import data.pca.PCA;
import tools.IProcessListener;
import tools.array.Array;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class MFCC {

    private IProcessListener listener;

    // Audio property
    private int sampleLength;
    private int sampleRate;
    private String word;

    // windowing property
    private int samplePerFrame;
    private int noOfFrames;
    private int samplePerFrameStep;
    private double windowsSize = 0.025; // in ms
    private double stepSize = 0.01; // in ms
    private double lowerFilterFrequency = 80; //in hz
    private double higherFilterFrequency; //in hz
    private int numMelFilter = 30;

    // Audio data
    private double[] audioData;
    private double[][] framedAudioData;

    // Audio data in complex format for FFT
    private Complex[][] complexes;

    // mag spectrume
    private double[][] magSpectrum;

    // filterBank
    private int[] cbin;

    //
    private double[][] tempMelFilter;

    //Ceptra (MFCC Coeeficient)
    private int ceptralCoef = 12;
    private int totalMfccFeature = (ceptralCoef + 1) * 3; // 1 = energy
    private double[][] ceptra;
    private double[][] deltaCeptra;
    private double[][] deltaDeltaCeptra;
    private double[] energy;
    private double[] deltaEnergy;
    private double[] deltaDeltaEnergy;
    private double[][] mfcc;

    // preEmphasis coefficient
    private float preEmphasisCoefficient = 0.95f;

    /**
     * @param audioData  data in double format
     * @param sampleRate sample rate of audio
     */

    public MFCC(double[] audioData, int sampleRate, String word) {
        this.word = word;
        this.audioData = new double[audioData.length];
        System.arraycopy(audioData, 0, this.audioData, 0, audioData.length);
        this.sampleLength = audioData.length;
        this.sampleRate = sampleRate;
        higherFilterFrequency = sampleRate / 2;

        // Based on time
        //samplePerFrame = (int) Math.round(sampleRate * windowsSize);
        //samplePerFrameStep = (int) Math.round(sampleRate * stepSize );

        // Based on nearest power of 2 sample size
        samplePerFrame = 2048; // 2048 / 96000 = 0.021 s | 21ms (frame size)
        samplePerFrameStep = 1024; // 1024 / 96000 = 0.010 | 10ms (overlap frame size)

        noOfFrames = (int) (Math.ceil(sampleLength / samplePerFrame)) + 1; // overlap 50% (X2)

    }

    public void doMFCC() {
        preEmphasis();
        framing();
        windowing();
        energy();
        fft();
        makeFilterBank();
        melFilter();
        dct();

        deltaCeptra = delta(ceptra);
        deltaDeltaCeptra = delta(deltaCeptra);
        deltaEnergy = delta(energy);
        deltaDeltaEnergy = delta(deltaEnergy);

        writeMessage("MFCC Done");
    }

    private void preEmphasis() {
        // Calculate preemphasis per sample
        for (int i = 1; i < audioData.length; i++) {
            audioData[i] = (audioData[i] - preEmphasisCoefficient * audioData[i - 1]);
        }
    }

    // Divide audio sample into overlapping frame
    private void framing() {
        writeMessage("Sample Length : " + sampleLength);
        writeMessage("Time : " + sampleLength / (float) sampleRate);
        writeMessage("No of Frames : " + noOfFrames);
        writeMessage("Sample per Frame : " + samplePerFrame);

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
        writeMessage("Done Framing");
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

        writeMessage("Done Windowing");
    }

    private void fft() {

        complexes = new Complex[framedAudioData.length][samplePerFrame];

        Complex[][] complexAfterFFT = new Complex[framedAudioData.length][samplePerFrame];

        //Compute fft per frames
        //loop per frame
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
                complexes[i][j] = new Complex(framedAudioData[i][j], 0.0);
            }
            complexAfterFFT[i] = FFT.fft(complexes[i]);
        }

        writeMessage("Done FFT");

        // calculate mag spectrum
        magSpectrum = new double[noOfFrames][];
        for (int i = 0; i < noOfFrames; i++) {
            magSpectrum[i] = new double[framedAudioData[i].length];
            for (int j = 0; j < framedAudioData[i].length; j++) {

                magSpectrum[i][j] = Math.sqrt(Math.pow(complexAfterFFT[i][j].re(), 2) + Math.pow(complexAfterFFT[i][j].im(), 2));
            }
        }

        writeMessage("Done Mag");
    }

    private void makeFilterBank() {
        // initiate array
        cbin = new int[numMelFilter + 2];
        double[] cbinTemp = new double[numMelFilter + 2];

        // create range frequency based on mel-scale
        double lowMel = hzToMel(lowerFilterFrequency);
        double highMel = hzToMel(higherFilterFrequency);

        // initiate 1st and last filter bank
        cbinTemp[0] = lowMel;
        cbinTemp[cbin.length - 1] = highMel;

        // create filter bank in mel-scale
        for (int i = 1; i < numMelFilter + 1; i++) {
            cbinTemp[i] = lowMel + ((highMel - lowMel) / (numMelFilter + 2)) * i;
        }

        // convert back to frequency
        for (int i = 0; i <= numMelFilter + 1; i++) {
            cbin[i] = (int) Math.floor(samplePerFrame * melToHz(cbinTemp[i]) / sampleRate);
            //cbinTemp[i] = melToHz(cbinTemp[i]);
        }
    }

    private void melFilter() {
        tempMelFilter = new double[noOfFrames][numMelFilter];
        //double[] temp = new double[numMelFilter + 2];
        // Loop through frames
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 1; j <= numMelFilter - 2; j++) {
// VERSION CODE 3
                int startFreqId = cbin[j - 1];
                int centerFreqId = cbin[j];
                int endFreqId = cbin[j + 1];

                for (int freq = startFreqId; freq < centerFreqId; freq++) {
                    int magnitudeScale = centerFreqId - startFreqId;
                    tempMelFilter[i][j] += magSpectrum[i][freq] * (freq - startFreqId) / magnitudeScale;
                }

                for (int freq = centerFreqId; freq < endFreqId; freq++) {
                    int magnitudeScale = centerFreqId - endFreqId;
                    tempMelFilter[i][j] += magSpectrum[i][freq] * (freq - endFreqId) / magnitudeScale;
                }
            }

//            for (int j = 0; j < numMelFilter; j++) {
//                tempMelFilter[i][j] = temp[j + 1];
//            }
        }
    }

    private void dct() {
        ceptra = new double[noOfFrames][ceptralCoef];

        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 1; j <= ceptralCoef; j++) {
                for (int k = 1; k <= numMelFilter; k++) {
                    ceptra[i][j - 1] += tempMelFilter[i][k - 1] * Math.cos(Math.PI * (j - 1) / numMelFilter * (k - 0.5));
                }
            }
        }
        writeMessage("DCT Done");
    }

    private void energy() {
        energy = new double[framedAudioData.length];

        for (int i = 0; i < framedAudioData.length; i++) {
            double sum = 0;
            for (int j = 0; j < framedAudioData[i].length; j++) {
                sum += Math.pow(framedAudioData[i][j], 2);
            }

            if (Double.isNaN(Math.log(sum)) || Double.isInfinite(Math.log(sum))) {
                //throw new IllegalArgumentException();
                System.out.println("warning");
            }

            energy[i] = Math.log(sum);
        }
    }

    private double[][] delta(double[][] ceptra) {
        double[][] delta = new double[framedAudioData.length][ceptralCoef];

        for (int i = 0; i < framedAudioData.length; i++) {
            for (int j = 0; j < ceptralCoef; j++) {
                int indexi_x = i + 1;
                int indexi_y = i - 1;

                if (indexi_y < 0) {
                    indexi_y = 0;
                }
                if (indexi_x > framedAudioData.length - 1) {
                    indexi_x = framedAudioData.length - 1;
                }

                double num = ceptra[indexi_x][j] - ceptra[indexi_y][j];
                double result = num / 2;
                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    //throw new IllegalArgumentException();
                }

                delta[i][j] = result;
            }
        }

        return delta;
    }

    private double[] delta(double[] ceptra) {
        double[] delta = new double[framedAudioData.length];

        for (int i = 0; i < framedAudioData.length; i++) {
            int indexi_x = i + 1;
            int indexi_y = i - 1;

            if (indexi_y < 0) {
                indexi_y = 0;
            }
            if (indexi_x > framedAudioData.length - 1) {
                indexi_x = framedAudioData.length - 1;
            }

            double num = ceptra[indexi_x] - ceptra[indexi_y];
            double result = num / 2;
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new IllegalArgumentException();
            }
            delta[i] = result;

        }
        return delta;
    }

//    public double[][] getCeptra() {
//        return ceptra.clone();
//    }

    public void setCeptra(double[][] ceptra) {
        this.mfcc = ceptra;
    }

    public double[][] getCeptra() {
        if (mfcc == null) {
            mfcc = new double[framedAudioData.length][totalMfccFeature];

            for (int i = 0; i < framedAudioData.length; i++) {
                for (int j = 0; j < totalMfccFeature; j++) {
                    double data = 0;
                    if (j < 12) {
                        data = ceptra[i][j];
                    } else if (j >= ceptralCoef && j < (ceptralCoef * 2)) {
                        data = deltaCeptra[i][j - ceptralCoef];
                    } else if (j >= (ceptralCoef * 2) && j < (ceptralCoef * 3)) {
                        data = deltaDeltaCeptra[i][j - (ceptralCoef * 2)];
                    } else if (j == (ceptralCoef * 3)) {
                        data = energy[i];
                    } else if (j == (ceptralCoef * 3) + 1) {
                        data = deltaEnergy[i];
                    } else if (j == (ceptralCoef * 3) + 2) {
                        data = deltaDeltaEnergy[i];
                    }
                    mfcc[i][j] = data;
                }
            }
        }
        return mfcc;
    }

    private double hzToMel(double hz) {
        return 2595 * Math.log10(1 + hz / 700);
    }

    private double melToHz(double x) {
        return 700 * (Math.pow(10, x / 2595) - 1);
    }

    public String getWord() {
        return word;
    }

    public void setListener(IProcessListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        if (listener != null) {
            this.listener = null;
        }
    }

    public void writeMessage(String context) {
        if (listener != null) {
            listener.getMessage(new Date().toString(), context);
        }
    }
}
