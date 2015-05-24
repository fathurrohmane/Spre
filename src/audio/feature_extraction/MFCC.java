package audio.feature_extraction;

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
    int windowsSize = 25; // in ms

    //Audio data
    double[] audioData;
    double[][] framedAudioData;

    // preEmphasis coefficient
    float preEmphasisCoefficient = 0.95f;



    public MFCC(double[] audioData,int sampleLength, int sampleRate) {
        this.audioData = audioData;
        this.sampleLength = sampleLength;
        this.sampleRate = sampleRate;

        float time = sampleLength /(float) sampleRate;
        noOfFrames =(int) Math.ceil(time*1000 /(float) windowsSize) + 1;
        samplePerFrame =(int) Math.floor(sampleLength / noOfFrames);
    }

    private double[] preEmphasis(double[] audioData) {
        double[] outputAudiodata = new double[audioData.length];

        for (int i = 1; i < audioData.length; i++) {
            outputAudiodata[i] = (audioData[i] - preEmphasisCoefficient * audioData[i - 1]);
        }

        return outputAudiodata;
    }

    private void framing() {
        System.out.println("Sample Length : "+sampleLength);
        System.out.println("Time : "+sampleLength /(float) sampleRate);
        System.out.println("No of Frames : "+noOfFrames);
        System.out.println("Sample per Frame : "+samplePerFrame);

        framedAudioData = new double[noOfFrames][samplePerFrame];
        int counter = 0;
        for (int i = 0; i < noOfFrames; i++) {
            int indexFrame = (i * noOfFrames);
            for (int j = 0; j < samplePerFrame; j++) {
                if(counter<sampleLength) {
                    framedAudioData[i][j] = audioData[indexFrame + j];
                    counter++;
                }
            }
        }
    }

    private void windowing() {
        //
        double[] hammingWindow = new double[sampleLength + 1];

        for (int i = 1; i <= samplePerFrame; i++) {
            hammingWindow[i] = (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
        }

        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
                
            }
        }

    }

}
