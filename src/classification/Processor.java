package classification;

import audio.AudioProcessing;
import audio.feature_extraction.MFCC;
import data.database.DatabaseHandler;
import data.vectorquantization.LBG.LBG;
import data.vectorquantization.LBG.Point;
import test.validator.hmm.HiddenMarkov;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fathurrohman on 08-Dec-15.
 * Class Training will handle all the process for speech recognition in Training part
 * The process is
 * 1. Get audio data -> AudioProcessing.java -> get audiodata in double format
 * 2. Get MFCC feature -> MFCC.java -> get feature vector of audiodata in double format
 * 3.
 */
public class Processor {

    private static List<String> filePath;
    private static List<Integer> filePathSeparator;
    private static List<String> wordsList;

    /**
     * Static method to create new codebook
     *
     * @param word             name of word that spoken in sound file
     * @param cluster          number of cluster or observation symbol
     * @param soundDirectories file
     */
    public static void startTraining(String word, int cluster, File soundDirectories) {
        // Array list file
        filePath = new ArrayList<String>();
        filePathSeparator = new ArrayList<Integer>();
        wordsList = new ArrayList<String>();

        // array list ceptra
        List<MFCC> mfccs = new ArrayList<MFCC>();
        List<Point> ceptra = new ArrayList<Point>();

        readFolder(soundDirectories.listFiles());

        // Read all files
        for (String path :
                filePath) {
            File file = new File(path);
            // Audio Processing
            AudioProcessing audioProcessing = new AudioProcessing(file);

            // MFCC
            MFCC mfcc = new MFCC(audioProcessing.getAudioData()
                    , audioProcessing.getAudioSampleRate()
                    , "" // empty string because label not required here (all words inserted to codebook)
            );
            // Type of MFCC
            mfcc.doMFCC();

            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }
            mfccs.add(mfcc);
        }
        // Create codebook
        LBG lbg = new LBG(ceptra);
        lbg.calculateCluster(cluster);
        lbg.saveToDatabase(word);

        // Create word model
        int counter = 0;
        for (int i = 0; i < wordsList.size(); i++) {
            // Create Observation Sequence
            int[][] observation = new int[mfccs.size() / wordsList.size()][];

            int temp = counter;
            for (int j = temp; j < (temp + (mfccs.size() / wordsList.size())); j++) {
                observation[j-temp] = lbg.getObservationSequence(mfccs.get(j));
                counter++;
            }

            // Create HMM
            test.validator.hmm.HiddenMarkov hiddenMarkov = new test.validator.hmm.HiddenMarkov(8, cluster); // FIXME: 30-Dec-15
            hiddenMarkov.setTrainSeq(observation);
            hiddenMarkov.train();
            hiddenMarkov.save(wordsList.get(i));
            System.out.println("Done HMM");
        }


    }

    public static void startTestingMultiple(File soundDirectories) {
        // Array list file
        filePath = new ArrayList<String>();
        filePathSeparator = new ArrayList<Integer>();
        wordsList = new ArrayList<String>();

        // array list ceptra
        List<MFCC> mfccs = new ArrayList<MFCC>();
        List<Point> ceptra = new ArrayList<Point>();

        readFolder(soundDirectories.listFiles());

        // Read all files
        for (String path :
                filePath) {
            File file = new File(path);
            // Audio Processing
            AudioProcessing audioProcessing = new AudioProcessing(file);

            // MFCC
            MFCC mfcc = new MFCC(audioProcessing.getAudioData()
                    , audioProcessing.getAudioSampleRate()
                    , file.getName() // TODO: 01-Feb-16 is file.getName() is enough?
            );

            // Type of MFCC
            mfcc.doMFCC();

            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }
            mfccs.add(mfcc);
        }

        LBG lbg = new LBG("tes");

        ArrayList<HiddenMarkov> wordModels = DatabaseHandler.loadAllWordModelToHMMs();


        for (int i = 0; i < mfccs.size(); i++) { // number of speech
            // Create Observation Sequence
            String result = "?";
            int[] observation = lbg.getObservationSequence(mfccs.get(i));
            double maxProbability = Double.NEGATIVE_INFINITY;
            for (HiddenMarkov hmm:
                 wordModels) {
                double probability = hmm.viterbi(observation);
                if (maxProbability < probability) {
                    maxProbability = probability;
                    result = hmm.getWord();
                }
            }
            System.out.println("Word = "+mfccs.get(i).getWord()+" Result : " + result);
        }
    }

    public static void readFolder(File[] files) {
        int counter = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory :" + file.getName());
                wordsList.add(file.getName());
                readFolder(file.listFiles());
            } else {
                counter++;
                System.out.println("File " + counter + " :" + file.getAbsolutePath());
                filePath.add(file.getAbsolutePath());
            }
        }
        if (counter != 0) {
            filePathSeparator.add(counter);
        }
    }

    private static String getWordName(String file) {
        String[] input = file.split("/");

        return input[input.length - 1 - 1];
    }

}
