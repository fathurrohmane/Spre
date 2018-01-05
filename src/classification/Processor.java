package classification;

import audio.AudioProcessing;
import audio.feature_extraction.MFCC;
import data.database.DatabaseHandler;
import data.pca.PCA;
import data.vectorquantization.LBG.LBG;
import data.vectorquantization.LBG.Point;
import test.validator.hmm.HiddenMarkov;
import tools.IMainView;
import tools.IProcessListener;

import java.io.File;
import java.util.*;

/**
 * Created by Fathurrohman on 08-Dec-15.
 * Class Training will handle all the process for speech recognition in Training part
 * The process is
 * 1. Get audio data -> AudioProcessing.java -> get audiodata in double format
 * 2. Get MFCC feature -> MFCC.java -> get feature vector of audiodata in double format
 * 3.
 */
public class Processor implements IProcessListener {

    private static List<String> filePath;
    private static List<Integer> filePathSeparator;
    private static List<String> wordsList;
    private static List<Integer> numberofSoundFilePerWord;

    private IMainView mainView;

    public Processor(IMainView mainView) {
        this.mainView = mainView;
    }

    /**
     * Static method to create new codebook
     *
     * @param word             name of word that spoken in sound file
     * @param cluster          number of cluster or observation symbol
     * @param soundDirectories file
     */
    public void startTraining(String word, int cluster, File soundDirectories) {
        // Array list file
        filePath = new ArrayList<>();
        filePathSeparator = new ArrayList<>();
        wordsList = new ArrayList<>();
        numberofSoundFilePerWord = new ArrayList<>();

        // array list ceptra
        List<MFCC> accousticVectors = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

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
                    , path
            );
            mfcc.setListener(this);
            mfcc.doMFCC();

            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }

            accousticVectors.add(mfcc);
            mfcc.removeListener();
        }

        PCA pca = new PCA(ceptra);
//        double[][] mfccsAfterPCA = pca.getPCAResult(33);
        double[][] mfccsAfterPCA = pca.getPCABackWithout(33);

        int counter = 0;
        for (MFCC mfcc : accousticVectors
                ) {
            int numberOfFrame = mfcc.getCeptra().length;
            double[][] ceptras = new double[numberOfFrame][mfccsAfterPCA[0].length]; // number of frame and new dimension

            for (int i = 0; i < numberOfFrame; i++) {
                ceptras[i] = mfccsAfterPCA[i + counter];
            }
            counter+= numberOfFrame;
            mfcc.setCeptra(ceptras);
        }

        // Create codebook
        LBG lbg = new LBG(mfccsAfterPCA);
        lbg.setListener(this);
        lbg.calculateCluster(cluster);
        lbg.saveToDatabase();
        lbg.removeListener();

        // Create word model
        counter = 0;
        for (int i = 0; i < wordsList.size(); i++) {
            // Create Observation Sequence
            int[][] observation = new int[numberofSoundFilePerWord.get(i)][];

            // The observation variable goes to 0 - n every loop
            // But the mfccs keeps counting
            int temp = counter;
            for (int j = temp; j < (temp + (numberofSoundFilePerWord.get(i))); j++) {
                observation[j - temp] = lbg.getObservationSequence(accousticVectors.get(j));
                counter++;
            }

            // Create HMM
            test.validator.hmm.HiddenMarkov hiddenMarkov = new test.validator.hmm.HiddenMarkov(8, cluster); // FIXME: 30-Dec-15
            hiddenMarkov.setListener(this);
            hiddenMarkov.setTrainSeq(observation);
            hiddenMarkov.train();
            hiddenMarkov.save(wordsList.get(i));
            hiddenMarkov.removeListener();
            getMessage(new Date().toString(), "Done HMM");
        }


    }

    public void startTestingMultiple(File soundDirectories, File databaseDirectory) {
        // Array list file
        filePath = new ArrayList<>();
        filePathSeparator = new ArrayList<>();
        wordsList = new ArrayList<>();
        numberofSoundFilePerWord = new ArrayList<>();

        // array list ceptra
        List<MFCC> accousticVectors = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

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
            mfcc.setListener(this);
            // Type of MFCC
            mfcc.doMFCC();

            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }
            accousticVectors.add(mfcc);
            mfcc.removeListener();
        }

        LBG lbg = new LBG(databaseDirectory);

        PCA pca = new PCA(ceptra);
        double[][] mfccsAfterPCA = pca.getPCABackWithout(33);
//        double[][] mfccsAfterPCA = pca.get(33);

        int counter = 0;
        for (MFCC mfcc : accousticVectors
                ) {
            int numberOfFrame = mfcc.getCeptra().length;
            double[][] ceptras = new double[numberOfFrame][mfccsAfterPCA[0].length]; // number of frame and new dimension

            for (int i = 0; i < numberOfFrame; i++) {
                ceptras[i] = mfccsAfterPCA[i + counter];
            }
            counter+= numberOfFrame;
            mfcc.setCeptra(ceptras);
        }

        ArrayList<HiddenMarkov> wordModels = DatabaseHandler.loadAllWordModelToHMMs(databaseDirectory);

        int totalData = accousticVectors.size();
        int rightAnswer = 0;

        for (int i = 0; i < accousticVectors.size(); i++) { // number of speech
            // Create Observation Sequence
            String result = "?";
            int[] observation = lbg.getObservationSequence(accousticVectors.get(i));
            double maxProbability = Double.NEGATIVE_INFINITY;
            for (HiddenMarkov hmm :
                    wordModels) {
                double probability = hmm.viterbi(observation);
                if (maxProbability < probability) {
                    maxProbability = probability;
                    result = hmm.getWord();
                }
            }
            if (accousticVectors.get(i).getWord().startsWith(result) || accousticVectors.get(i).getWord().contains(result)) {
                rightAnswer++;
            }
            System.out.println("Word = " + accousticVectors.get(i).getWord() + " Result : " + result);
        }
        double result = (double) rightAnswer / totalData;
        System.out.println("Recognition rate = " + (result * 100) + " %");
    }

    /**
     * Static method to create new codebook
     *
     * @param word             name of word that spoken in sound file
     * @param cluster          number of cluster or observation symbol
     * @param soundDirectories file
     */
    public void startTrainingWithoutPCA(String word, int cluster, File soundDirectories) {
        // Array list file
        filePath = new ArrayList<String>();
        filePathSeparator = new ArrayList<Integer>();
        wordsList = new ArrayList<String>();
        numberofSoundFilePerWord = new ArrayList<>();

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
            mfcc.setListener(this);
            mfcc.doMFCC();

            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }
            mfccs.add(mfcc);
            mfcc.removeListener();
        }
        // Create codebook
        LBG lbg = new LBG(ceptra);
        lbg.setListener(this);
        lbg.calculateCluster(cluster);
        lbg.saveToDatabase();
        lbg.removeListener();

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
            getMessage(new Date().toString(), "Done HMM");
        }


    }

    public void startTestingWithoutPCA(File soundDirectories, File databaseDirectory) {
        // Array list file
        filePath = new ArrayList<String>();
        filePathSeparator = new ArrayList<Integer>();
        wordsList = new ArrayList<String>();
        numberofSoundFilePerWord = new ArrayList<>();

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
            mfcc.setListener(this);
            // Type of MFCC
            mfcc.doMFCC();

            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }
            mfccs.add(mfcc);
            mfcc.removeListener();
        }

        LBG lbg = new LBG(databaseDirectory);

        ArrayList<HiddenMarkov> wordModels = DatabaseHandler.loadAllWordModelToHMMs(databaseDirectory);

        int totalData = mfccs.size();
        int rightAnswer = 0;

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
            if (mfccs.get(i).getWord().startsWith(result) || mfccs.get(i).getWord().contains(result)) {
                rightAnswer++;
            }
            System.out.println("Word = " + mfccs.get(i).getWord() + " Result : " + result);
            getMessage(new Date().toString(),"Word = "+mfccs.get(i).getWord()+" Result : " + result);
        }
        double result = (double) rightAnswer / totalData;
        getMessage(new Date().toString(),"Recognition rate = " + (result * 100) + " %");
    }

    private static void readFolder(File[] files) {
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
            numberofSoundFilePerWord.add(counter);
            filePathSeparator.add(counter);
        }
    }

    private static String getWordName(String file) {
        String[] input = file.split("/");

        return input[input.length - 1 - 1];
    }

    @Override
    public void getMessage(String time, String context) {
        mainView.writeLog(context);
    }
}
