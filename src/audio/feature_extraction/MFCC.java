package audio.feature_extraction;

import data.Complex;
import data.FFT;
import data.vectorquantization.LBG.VectorQuantization;

/**
 * Created by Fathurrohman on 5/20/2015.
 *
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
    double higherFilterFrequency; //in hz
    int numMelFilter = 30;

    //Audio data
    double[] audioData;
    double[][] framedAudioData;
    //Audio data in complex format for FFT
//    ComplexVector[] complexVectors;
    Complex[][] complexes;

    //mag spectrume
    double[][] magSpectrum;

    //filterBank
    int[] cbin;

    //
    double[][] tempMelFilter;

    //Ceptra (MFCC Coeeficient)
    double[][] ceptra;

    // preEmphasis coefficient
    float preEmphasisCoefficient = 0.95f;

/**
 * @param audioData data in double format
 * @param sampleRate sample rate of audio
 */

    public MFCC(double[] audioData, int sampleRate) {
        this.audioData = new double[audioData.length];
        System.arraycopy(audioData, 0, this.audioData, 0, audioData.length);
        //this.audioData = audioData;
        this.sampleLength = audioData.length;
        this.sampleRate = sampleRate;
        higherFilterFrequency = sampleRate / 2;

        //Based on time
        //samplePerFrame = (int) Math.round(sampleRate * windowsSize);
        //samplePerFrameStep = (int) Math.round(sampleRate * stepSize );

        //Based on nearest power of 2 sample size
        samplePerFrame = 2048; // 2048 / 96000 = 0.021 s | 21ms (frame size)
        samplePerFrameStep = 1024; // 1024 / 96000 = 0.010 | 10ms (overlap frame size)


        noOfFrames = (int) (Math.ceil(sampleLength / samplePerFrame)) + 1; // overlap 50% (X2)

        preEmphasis();
        framing();
        windowing();
        fft();
        makeFilterBank();
        melFilter();
        dct();

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

        //complexVectors = new ComplexVector[framedAudioData.length];
        complexes = new Complex[framedAudioData.length][samplePerFrame];

        //Compute fft per frames
        //loop per frame
        //for (int i = 0; i < framedAudioData.length; i++) {
        //complexVectors[i] = new ComplexVector(framedAudioData[i]);
        //}

        Complex[][] complexAfterFFT = new Complex[framedAudioData.length][samplePerFrame];

        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
            complexes[i][j] = new Complex(framedAudioData[i][j],0.0);
            }
            complexAfterFFT[i] = FFT.fft(complexes[i]);
        }

        System.out.println("Done FFT");

        //calculate mag spectrume
        magSpectrum = new double[noOfFrames][framedAudioData[0].length];
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < framedAudioData[0].length; j++) {
          //      magSpectrum[i][j] = Math.sqrt(complexVectors[i].real[j] * complexVectors[i].real[j] + complexVectors[i].imaginary[j] * complexVectors[i].imaginary[j]);
                magSpectrum[i][j] = Math.sqrt(Math.pow(complexAfterFFT[i][j].re(),2) + Math.pow(complexAfterFFT[i][j].im(),2));
            }
        }

        System.out.println("Done Mag");
    }

    private void makeFilterBank() {
        //initiate array
        cbin = new int[numMelFilter + 2];
        double[] cbinTemp = new double[numMelFilter + 2];

        //create range frequency based on mel-scale
        double lowMel = hzToMel(lowerFilterFrequency);
        double highMel = hzToMel(higherFilterFrequency);

        //initiate 1st and last filter bank
        cbinTemp[0] = lowMel;
        cbinTemp[cbin.length - 1] = highMel;

        //create filter bank in mel-scale
        for (int i = 1; i <numMelFilter + 1; i++) {
            cbinTemp[i] = lowMel + ((highMel - lowMel) / (numMelFilter + 2)) * i;
        }

        //convert back to frequency
        for (int i = 0; i <= numMelFilter + 1; i++) {
            cbin[i] = (int) Math.floor(samplePerFrame*melToHz(cbinTemp[i]) / sampleRate);
            //cbinTemp[i] = melToHz(cbinTemp[i]);
        }

        System.out.println("LOL");

    }

    private void melFilter() {
        tempMelFilter = new double[noOfFrames][numMelFilter];
        //double[] temp = new double[numMelFilter + 2];
        //Loop through frames
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 1; j <= numMelFilter - 2; j++) {
// VERSION CODE 3
                int startFreqId = cbin[j - 1];
                int centerFreqId = cbin[j];
                int endFreqId = cbin[j + 1];

                for (int freq = startFreqId; freq < centerFreqId; freq++) {
                    int magnitudeScale = centerFreqId - startFreqId;
                    tempMelFilter[i][j] += magSpectrum[i][freq]*(freq - startFreqId) / magnitudeScale;
                }

                for (int freq = centerFreqId; freq < endFreqId; freq++) {
                    int magnitudeScale = centerFreqId - endFreqId;
                    tempMelFilter[i][j] += magSpectrum[i][freq]*(freq - endFreqId) / magnitudeScale;
                }

            }

//            for (int j = 0; j < numMelFilter; j++) {
//                tempMelFilter[i][j] = temp[j + 1];
//            }
//            System.out.println("LOL");
        }
    }

    private void dct()
    {
        int numCeptra = 12;
        ceptra = new double[noOfFrames][numCeptra];

        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 1; j <= numCeptra; j++) {
                for (int k = 1; k <= numMelFilter ; k++) {
                    ceptra[i][j - 1] += tempMelFilter[i][k - 1] * Math.cos(Math.PI * (j - 1) / numMelFilter * (k - 0.5));
                }
            }
        }
        System.out.println("DCT Done");
        System.out.println("MFCC Done");
        System.out.println("Try Clustering");

//        GenLloyd gl = new GenLloyd(ceptra);
//
//        gl.calcClusters(4);

//        double[][] results = gl.getClusterPoints();
//        for (double[] point : results) {
//            System.out.print("Cluster : ");
//            for (double po : point) {
//                System.out.print(po + " ");
//            }
//            System.out.println();
//        }

    }

    public double[][] getCeptra() {
        return ceptra.clone();
    }

    private double hzToMel(double hz) {
        return 2595 * Math.log10(1 + hz / 700);
    }

    private double melToHz(double x) {
        return 700 * (Math.pow(10, x / 2595) - 1);
    }

}
