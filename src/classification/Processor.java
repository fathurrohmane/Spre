package classification;

import audio.AudioProcessing;
import audio.feature_extraction.MFCC;
import data.database.DatabaseHandler;
import data.model.SoundFileInfo;
import data.pca.PCA;
import data.vectorquantization.LBG.LBG;
import data.vectorquantization.LBG.Point;
import test.validator.hmm.HiddenMarkov;
import tools.MainView;
import tools.ProcessListener;

import java.io.File;
import java.util.*;

/**
 * Created by Fathurrohman on 08-Dec-15.
 * Class Training will handle all the process for speech recognition in Training part
 * The process is
 * 1. Get audio data -> AudioProcessing.java -> get audio data in double format
 * 2. Get MFCC feature -> MFCC.java -> get feature vector of audio data in double format
 * 3.
 */
public class Processor implements ProcessListener {

    private MainView mainMenuView;

    private Map<String, Long> timeStamp;

    public Processor(MainView mainMenuView) {
        this.mainMenuView = mainMenuView;
        timeStamp = new HashMap<>();
    }

    /**
     * Static method to create new codebook
     *
     * @param targetDimensionSize number of target dimension (39 -> x)
     * @param soundDirectories    file
     */
    public void startTrainingWithPCA(int targetDimensionSize, File soundDirectories) {
        // array list ceptra
        List<MFCC> acousticVectors = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

        SoundFileInfo soundFileInfo = DatabaseHandler.readFolder(soundDirectories.listFiles());

        // Time stamp program start
        printTimeStamp("start");
        writeLog(ProcessListener.BASIC, "Start Training With PCA Reduction!");
        mainMenuView.writeProgress(0);

        // MFCC
        assert soundFileInfo != null;
        createAcousticVectors(soundFileInfo, ceptra, acousticVectors);

        // Time stamp feature extraction done
        printTimeStamp("mfcc");
        writeLog(ProcessListener.BASIC, "Finish generating acoustic vector");

        // PCA
        double[][] mfccsAfterPCA = reduceDimensionWithPca(targetDimensionSize, ceptra);

        // Time stamp dimension reduction done
        printTimeStamp("pca");
        writeLog(ProcessListener.BASIC, "Finish reduce acoustic vector dimension");
        mainMenuView.writeProgress(0.45);

        int counter = 0;
        for (MFCC mfcc : acousticVectors
                ) {
            int numberOfFrame = mfcc.getCeptra().length;
            double[][] ceptras = new double[numberOfFrame][mfccsAfterPCA[0].length]; // number of frame and new dimension

            System.arraycopy(mfccsAfterPCA, counter, ceptras, 0, numberOfFrame);
            mfcc.setCeptra(ceptras);
            counter += numberOfFrame;
        }

        // Create codebook
        LBG lbg = new LBG(mfccsAfterPCA);
        lbg.setListener(ProcessListener.VQ, this);
        lbg.calculateCluster(LBG.MAX_CLUSTER);
        lbg.saveToDisk();
        lbg.removeListener();

        // Time stamp vector quantization done
        printTimeStamp("vq");
        writeLog(ProcessListener.BASIC, "Finish generating codeword");
        mainMenuView.writeProgress(0.5);

        createWordModel(soundFileInfo, acousticVectors, lbg, LBG.MAX_CLUSTER);

        // Time stamp classification done
        printTimeStamp("hmm");
        writeLog(ProcessListener.BASIC, "Training with PCA Reduction complete");
        mainMenuView.writeProgress(1);
        timeStamp.forEach((k, v) -> writeLog(ProcessListener.TIMESTAMP, k + " : " + v));
    }

    public void startTestingWithPCA(int targetDimensionSize, File soundDirectories, File databaseDirectory) {
        // array list ceptra
        List<MFCC> acousticVectors = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

        SoundFileInfo soundFileInfo = DatabaseHandler.readFolder(soundDirectories.listFiles());

        // Time stamp program start
        printTimeStamp("start");
        writeLog(ProcessListener.BASIC, "Start Testing With PCA Reduction!");
        mainMenuView.writeProgress(0);

        // MFCC
        assert soundFileInfo != null;
        createAcousticVectors(soundFileInfo, ceptra, acousticVectors);

        // Time stamp feature extraction done
        printTimeStamp("mfcc");
        writeLog(ProcessListener.BASIC, "Finish generating acoustic vector");

        // PCA
        PCA pca = DatabaseHandler.loadPCA(databaseDirectory);
        double[][] mfccsAfterPCA = reduceDimensionWithPca(pca, targetDimensionSize, ceptra);

        // Time stamp dimension reduction done
        printTimeStamp("pca");
        writeLog(ProcessListener.BASIC, "Finish reduce acoustic vector dimension");
        mainMenuView.writeProgress(0.5);

        // Copy reduced mfcc to Acoustic vectors or array list of mfcc
        int counter = 0;
        for (MFCC mfcc : acousticVectors
                ) {
            int numberOfFrame = mfcc.getCeptra().length;
            double[][] ceptras = new double[numberOfFrame][mfccsAfterPCA[0].length]; // number of frame and new dimension

            System.arraycopy(mfccsAfterPCA, counter, ceptras, 0, numberOfFrame);
            counter += numberOfFrame;
            mfcc.setCeptra(ceptras);
        }

        // VQ
        LBG lbg = new LBG(databaseDirectory);

        // Time stamp Vector quantization(VQ)
        printTimeStamp("vq");
        writeLog(ProcessListener.BASIC, "Finish loading codeword from disk");
        mainMenuView.writeProgress(0.6);

        wordDetection(lbg, acousticVectors, databaseDirectory);

        // Time stamp Classification(HMM)
        printTimeStamp("hmm");
        writeLog(ProcessListener.BASIC, "Testing with PCA reduction complete");
        mainMenuView.writeProgress(1);
        timeStamp.forEach((k, v) -> writeLog(ProcessListener.TIMESTAMP, k + " : " + v));
    }

    /**
     * @param soundDirectories file
     */
    public void startTrainingWithoutPCA(File soundDirectories) {
        // array list ceptra
        List<MFCC> acousticVectors = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

        SoundFileInfo soundFileInfo = DatabaseHandler.readFolder(soundDirectories.listFiles());

        // Time stamp Start
        printTimeStamp("start");
        writeLog(ProcessListener.BASIC, "Start Training!");
        mainMenuView.writeProgress(0);

        // MFCC
        assert soundFileInfo != null;
        createAcousticVectors(soundFileInfo, ceptra, acousticVectors);

        // Time stamp Feature Extraction MFCC
        printTimeStamp("mfcc");
        writeLog(ProcessListener.BASIC, "Finish generating acoustic vector");

        // Create codebook
        LBG lbg = new LBG(ceptra);
        lbg.setListener(ProcessListener.VQ, this);
        lbg.calculateCluster(LBG.MAX_CLUSTER);
        lbg.saveToDisk();
        lbg.removeListener();

        // Time stamp Vector Quantization(VQ)
        printTimeStamp("vq");
        writeLog(ProcessListener.BASIC, "Finish generating codeword");
        mainMenuView.writeProgress(0.5);

        createWordModel(soundFileInfo, acousticVectors, lbg, LBG.MAX_CLUSTER);

        // Time stamp Classification
        printTimeStamp("hmm");
        writeLog(ProcessListener.BASIC, "Training complete");
        mainMenuView.writeProgress(1);
        timeStamp.forEach((k, v) -> writeLog(ProcessListener.TIMESTAMP, k + " : " + v));
    }

    public void startTestingWithoutPCA(File soundDirectories, File databaseDirectory) {
        // array list ceptra
        List<MFCC> acousticVectors = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

        SoundFileInfo soundFileInfo = DatabaseHandler.readFolder(soundDirectories.listFiles());

        // Time stamp Start
        printTimeStamp("start");
        writeLog(ProcessListener.BASIC, "Start Testing");
        mainMenuView.writeProgress(0);

        // MFCC
        assert soundFileInfo != null;
        createAcousticVectors(soundFileInfo, ceptra, acousticVectors);

        // Time stamp Feature Extraction(MFCC)
        printTimeStamp("mfcc");
        writeLog(ProcessListener.BASIC, "Finish generating acoustic vector");
        mainMenuView.writeProgress(0.5);

        LBG lbg = new LBG(databaseDirectory);

        // Time stamp Vector Quantization(VQ)
        printTimeStamp("vq");
        writeLog(ProcessListener.BASIC, "Finish loading codeword from disk");
        mainMenuView.writeProgress(0.6);

        wordDetection(lbg, acousticVectors, databaseDirectory);

        // Time stamp Classification(HMM)
        printTimeStamp("hmm");
        writeLog(ProcessListener.BASIC, "Testing complete");
        mainMenuView.writeProgress(1);
        timeStamp.forEach((k, v) -> writeLog(ProcessListener.TIMESTAMP, k + " : " + v));
    }

    private void createAcousticVectors(SoundFileInfo soundFileInfo, List<Point> ceptra, List<MFCC> acousticVectors) {
        int progressCounter = 0;
        assert soundFileInfo != null;
        for (String path :
                soundFileInfo.getFilePath()) {
            writeLog(ProcessListener.BASIC, "Reading " + path);
            File file = new File(path);
            // Audio Processing
            AudioProcessing audioProcessing = new AudioProcessing(file);

            // MFCC
            MFCC mfcc = new MFCC(audioProcessing.getAudioData()
                    , audioProcessing.getAudioSampleRate()
                    , path
            );
            mfcc.setListener(ProcessListener.MFCC, this);
            mfcc.doMFCC();

            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }
            acousticVectors.add(mfcc);
            mfcc.removeListener();
            mainMenuView.writeProgress(0 + (0.4 * ((double) progressCounter++ / soundFileInfo.getFilePath().size())));
        }
    }

    private void wordDetection(LBG lbg, List<MFCC> mfccs, File databaseDirectory) {
        List<HiddenMarkov> wordModels = DatabaseHandler.loadAllWordModelToHMMs(databaseDirectory);

        int correctAnswer = 0;
        for (int i = 0; i < mfccs.size(); i++) { // number of speech
            // Create Observation Sequence
            String result = "?";
            int[] observation = lbg.getObservationSequence(mfccs.get(i));
            double maxProbability = Double.NEGATIVE_INFINITY;
            for (HiddenMarkov hmm :
                    wordModels) {
                double probability = hmm.viterbi(observation);
                if (maxProbability < probability) {
                    maxProbability = probability;
                    result = hmm.getWord();
                }
            }
            if (mfccs.get(i).getWord().startsWith(result) || mfccs.get(i).getWord().contains(result)) {
                correctAnswer++;
            }
            writeLog(ProcessListener.BASIC, "Word = " + mfccs.get(i).getWord() + " Result : " + result);
            mainMenuView.writeProgress(0.6 + (0.4 * ((double) i / mfccs.size())));
        }
        double result = (double) correctAnswer / mfccs.size();
        writeLog(ProcessListener.BASIC, "Recognition rate = " + (result * 100) + " %");
        mainMenuView.writeToLabelRecognitionRate(result * 100);
    }

    private void createWordModel(SoundFileInfo soundFileInfo, List<MFCC> acousticVectors, LBG lbg, int cluster) {
        // Create word model
        int counter = 0;
        for (int i = 0; i < soundFileInfo.getWordLists().size(); i++) {
            // Create Observation Sequence
            int[][] observation = new int[soundFileInfo.getWordLists().get(i).getTotal()][];

            // The observation variable goes to 0 - n every loop
            // But the mfccs keeps counting
            int temp = counter;
            for (int j = temp; j < (temp + soundFileInfo.getWordLists().get(i).getTotal()); j++) {
                observation[j - temp] = lbg.getObservationSequence(acousticVectors.get(j));
                counter++;
            }

            // Create HMM
            test.validator.hmm.HiddenMarkov hiddenMarkov = new test.validator.hmm.HiddenMarkov(8, cluster);
            hiddenMarkov.setListener(ProcessListener.HMM, this);
            hiddenMarkov.setTrainSeq(observation);
            hiddenMarkov.train();
            hiddenMarkov.save(soundFileInfo.getWordLists().get(i).getWord());
            hiddenMarkov.removeListener();
            writeLog(ProcessListener.BASIC, "Finish generating " + soundFileInfo.getWordLists().get(i).getWord() + " word model.");
            mainMenuView.writeProgress(0.5 + (0.5 * ((double) i / soundFileInfo.getWordLists().size())));
        }
    }

    /**
     * Reduce data dimension with PCA
     *
     * @param dimension       number of dimension to keep
     * @param acousticVectors list of all acoustic vectors
     * @return Reduced dimension of acoustic vectors
     */
    private double[][] reduceDimensionWithPca(int dimension, List<Point> acousticVectors) {
        writeLog(ProcessListener.BASIC, "Calculating PCA");
        double[][] totalCeptra = new double[acousticVectors.size()][acousticVectors.get(0).getDimension()];
        int counter = 0;
        for (Point point : acousticVectors) {
            totalCeptra[counter] = point.getCoordinates().clone();
            counter++;
        }
        // Create pca and calculate
        PCA pca = new PCA(totalCeptra);
        // Save to disk
        pca.saveToDisk();

        double[][] mfccReduced = pca.getPCAResult(dimension).clone();
        writeLog(ProcessListener.BASIC, "Done Creating PCA");
        return mfccReduced;
    }

    /**
     * Reduce data dimension with PCA
     *
     * @param pca             PCA that have been trained / loaded from disk
     * @param dimension       number of dimension to keep
     * @param acousticVectors list of all acoustic vectors
     * @return Reduced dimension of acoustic vectors
     */
    private double[][] reduceDimensionWithPca(PCA pca, int dimension, List<Point> acousticVectors) {
        if (pca != null) {
            writeLog(ProcessListener.BASIC, "Calculating PCA");
            double[][] totalCeptra = new double[acousticVectors.size()][acousticVectors.get(0).getDimension()];
            int counter = 0;
            for (Point point : acousticVectors) {
                totalCeptra[counter] = point.getCoordinates().clone();
                counter++;
            }

            double[][] mfccReduced = pca.getPCAResult(totalCeptra, dimension).clone();
            writeLog(ProcessListener.BASIC, "Done Creating PCA");
            return mfccReduced;
        } else {
            throw new IllegalArgumentException("PCA is null!");
        }

    }

    /**
     * Output timestamp to text area
     *
     * @param type type of process
     */
    private void printTimeStamp(String type) {
        String start = "Start (ms) : ";
        String mfcc = "Feature extraction(MFCC) done (ms) : ";
        String pca = "Dimension reduction(PCA) done (ms) : ";
        String vq = "Clustering(VQ) done (ms) : ";
        String hmm = "Classification(HMM) done (ms) : ";

        long currentMillisecond = System.currentTimeMillis();

        switch (type) {
            case "start":
                writeLog(ProcessListener.TIMESTAMP, start + currentMillisecond);
                timeStamp.put("start", currentMillisecond);
                break;
            case "mfcc":
                writeLog(ProcessListener.TIMESTAMP, mfcc + currentMillisecond);
                timeStamp.put("mfcc", currentMillisecond);
                break;
            case "pca":
                writeLog(ProcessListener.TIMESTAMP, pca + currentMillisecond);
                timeStamp.put("pca", currentMillisecond);
                break;
            case "vq":
                writeLog(ProcessListener.TIMESTAMP, vq + currentMillisecond);
                timeStamp.put("vq", currentMillisecond);
                break;
            case "hmm":
                writeLog(ProcessListener.TIMESTAMP, hmm + currentMillisecond);
                timeStamp.put("hmm", currentMillisecond);
                break;
            default:
                throw new IllegalArgumentException("Illegal input");
        }
    }

    @Override
    public void writeLog(int processType, String context) {
        mainMenuView.writeToTextAreaConsole(processType, context);
    }
}
