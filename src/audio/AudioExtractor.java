package audio;

import unused.WavFile;
import unused.WavFileException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class AudioExtractor {

    //set audio data
    private static double[] audioData;

    public static double[] getAudioData(File file) {

        try {
            //Open file
            WavFile wavFile = WavFile.openWavFile(file);

            //set audio data
            audioData = new double[(int)wavFile.getNumFrames()];

            //Display audio info
            wavFile.display();

            //System.out.println("time ="+(wavFile.getNumFrames() +":"+ wavFile.getSampleRate()+""+wavFile.getNumChannels()));

            //
            int framesRead;
            int currentFrame = 0;

            double[][] buffer = new double[2][100];

            do {
                //Read frames into buffer
                framesRead = wavFile.readFrames(buffer,100);
                //System.out.println(framesRead);
                for (int i = 0; i < framesRead; i++) {
                    audioData[currentFrame+i] = buffer[0][i];
                    if(buffer[0][i] == 0) {
                        System.out.println(currentFrame+i);
                    }
                }
                currentFrame+=framesRead;
            } while (framesRead != 0);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }

        return audioData;

    }

}
