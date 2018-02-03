package classification;

import audio.AudioProcessing;
import audio.feature_extraction.MFCC;
import data.database.DatabaseHandler;
import data.model.SoundFileInfo;
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
 * 1. Get audio data -> AudioProcessing.java -> get audio data in double format
 * 2. Get MFCC feature -> MFCC.java -> get feature vector of audio data in double format
 * 3.
 */
public class Processor implements IProcessListener {

    private IMainView mainView;

    private int numberOfEign = 24;
    private Map<String, Long> timeStamp;

    public Processor(IMainView mainView) {
        this.mainView = mainView;
        timeStamp = new HashMap<>();
    }

    /**
     * Static method to create new codebook
     *
     * @param cluster          number of cluster or observation symbol
     * @param soundDirectories file
     */
    public void startTrainingWithPCA(int cluster, File soundDirectories) {
        // array list ceptra
        List<MFCC> acousticVectors = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

        SoundFileInfo soundFileInfo = DatabaseHandler.readFolder(soundDirectories.listFiles());

        // Time stamp program start
        printTimeStamp("start");

        // Read all files
        assert soundFileInfo != null;
        for (String path :
                soundFileInfo.getFilePath()) {
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
            acousticVectors.add(mfcc);
            mfcc.removeListener();
        }

        // Time stamp feature extraction done
        printTimeStamp("mfcc");

        //PCA
        double[][] mfccsAfterPCA = reduceDimensionWithPca(numberOfEign, ceptra);

        // Time stamp dimension reduction done
        printTimeStamp("pca");

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
        lbg.setListener(this);
        lbg.calculateCluster(cluster);
        lbg.saveToDatabase();
        lbg.removeListener();

        // Time stamp vector quantization done
        printTimeStamp("vq");

        createWordModel(soundFileInfo, acousticVectors, lbg, cluster);

        // Time stamp classification done
        printTimeStamp("hmm");
        getMessage(new Date().toString(), "Complete");
        timeStamp.forEach((k, v) -> getMessage(new Date().toString(), k + " : " + v));
    }

    public void startTestingWithPCA(File soundDirectories, File databaseDirectory) {

        // array list ceptra
        List<MFCC> acousticVectors = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

        SoundFileInfo soundFileInfo = DatabaseHandler.readFolder(soundDirectories.listFiles());

        // Time stamp program start
        printTimeStamp("start");

        // Read all files
        assert soundFileInfo != null;
        for (String path :
                soundFileInfo.getFilePath()) {
            File file = new File(path);
            // Audio Processing
            AudioProcessing audioProcessing = new AudioProcessing(file);

            // MFCC
            MFCC mfcc = new MFCC(audioProcessing.getAudioData()
                    , audioProcessing.getAudioSampleRate()
                    , file.getName()
            );
            mfcc.setListener(this);

            // Type of MFCC
            mfcc.doMFCC();
            for (int i = 0; i < mfcc.getCeptra().length; i++) {
                ceptra.add(new Point(mfcc.getCeptra()[i]));
            }
            acousticVectors.add(mfcc);
            mfcc.removeListener();
        }

        // Time stamp feature extraction done
        printTimeStamp("mfcc");

        // PCA
        PCA pca = DatabaseHandler.loadPCA(databaseDirectory);
        double[][] mfccsAfterPCA = reduceDimensionWithPca(pca, numberOfEign, ceptra);

        // Time stamp dimension reduction done
        printTimeStamp("pca");

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

        wordDetection(lbg, acousticVectors, databaseDirectory);

        // Time stamp Classification(HMM)
        printTimeStamp("hmm");
        getMessage(new Date().toString(), "Complete");
        timeStamp.forEach((k, v) -> getMessage(new Date().toString(), k + " : " + v));
    }

    /**
     * Static method to create new codebook
     *
     * @param cluster          number of cluster or observation symbol
     * @param soundDirectories file
     */
    public void startTrainingWithoutPCA(int cluster, File soundDirectories) {

        // array list ceptra
        List<MFCC> mfccs = new ArrayList<>();
        List<Point> ceptra = new ArrayList<>();

        SoundFileInfo soundFileInfo = DatabaseHandler.readFolder(soundDirectories.listFiles());

        // Time stamp Start
        printTimeStamp("start");

        // Read all files
        assert soundFileInfo != null;
        for (String path :
                soundFileInfo.getFilePath()) {
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

        // Time stamp Feature Extraction MFCC
        printTimeStamp("mfcc");

        // Create codebook
        LBG lbg = new LBG(ceptra);
        lbg.setListener(this);
        lbg.calculateCluster(cluster);
        lbg.saveToDatabase();
        lbg.removeListener();

        // Time stamp Vector Quantization(VQ)
        printTimeStamp("vq");

        createWordModel(soundFileInfo, mfccs, lbg, cluster);

        // Time stamp Classification
        printTimeStamp("hmm");
        getMessage(new Date().toString(), "Complete");
        timeStamp.forEach((k, v) -> getMessage(new Date().toString(), k + " : " + v));
    }

    public void startTestingWithoutPCA(File soundDirectories, File databaseDirectory) {

        // array list ceptra
        List<MFCC> mfccs = new ArrayList<>();

        SoundFileInfo soundFileInfo = DatabaseHandler.readFolder(soundDirectories.listFiles());

        // Time stamp Start
        printTimeStamp("start");

        // Read all files
        assert soundFileInfo != null;
        for (String path :
                soundFileInfo.getFilePath()) {
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
            mfccs.add(mfcc);
            mfcc.removeListener();
        }

        // Time stamp Feature Extraction(MFCC)
        printTimeStamp("mfcc");

        LBG lbg = new LBG(databaseDirectory);

        // Time stamp Vector Quantization(VQ)
        printTimeStamp("vq");

        wordDetection(lbg, mfccs, databaseDirectory);

        // Time stamp Classification(HMM)
        printTimeStamp("hmm");
        getMessage(new Date().toString(), "Complete");
        timeStamp.forEach((k, v) -> getMessage(new Date().toString(), k + " : " + v));
    }

    private void wordDetection(LBG lbg, List<MFCC> mfccs, File databaseDirectory) {
        List<HiddenMarkov> wordModels = DatabaseHandler.loadAllWordModelToHMMs(databaseDirectory);

        int rightAnswer = 0;

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
                rightAnswer++;
            }
            getMessage(new Date().toString(), "Word = " + mfccs.get(i).getWord() + " Result : " + result);
        }
        double result = (double) rightAnswer / mfccs.size();
        getMessage(new Date().toString(), "Recognition rate = " + (result * 100) + " %");
    }

    private void createWordModel(SoundFileInfo soundFileInfo, List<MFCC> accousticVectors, LBG lbg, int cluster) {
        // Create word model
        int counter = 0;
        for (int i = 0; i < soundFileInfo.getWordLists().size(); i++) {
            // Create Observation Sequence
            int[][] observation = new int[soundFileInfo.getWordLists().get(i).getTotal()][];

            // The observation variable goes to 0 - n every loop
            // But the mfccs keeps counting
            int temp = counter;
            for (int j = temp; j < (temp + soundFileInfo.getWordLists().get(i).getTotal()); j++) {
                observation[j - temp] = lbg.getObservationSequence(accousticVectors.get(j));
                counter++;
            }

            // Create HMM
            test.validator.hmm.HiddenMarkov hiddenMarkov = new test.validator.hmm.HiddenMarkov(8, cluster); // FIXME: 30-Dec-15
            hiddenMarkov.setListener(this);
            hiddenMarkov.setTrainSeq(observation);
            hiddenMarkov.train();
            hiddenMarkov.save(soundFileInfo.getWordLists().get(i).getWord());
            hiddenMarkov.removeListener();
            getMessage(new Date().toString(), "Done HMM");
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
        getMessage(new Date().toString(), "Calculating PCA");
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
        getMessage(new Date().toString(), "Done PCA");
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
            getMessage(new Date().toString(), "Calculating PCA");
            double[][] totalCeptra = new double[acousticVectors.size()][acousticVectors.get(0).getDimension()];
            int counter = 0;
            for (Point point : acousticVectors) {
                totalCeptra[counter] = point.getCoordinates().clone();
                counter++;
            }

            double[][] mfccReduced = pca.getPCAResult(totalCeptra, dimension).clone();
            getMessage(new Date().toString(), "Done PCA");
            return mfccReduced;
        } else {
            throw new IllegalArgumentException("PCA is null!");
        }

    }

    /**
     * Output timestamp to text area
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
                getMessage(new Date().toString(), start + currentMillisecond);
                timeStamp.put("start", currentMillisecond);
                break;
            case "mfcc":
                getMessage(new Date().toString(), mfcc + currentMillisecond);
                timeStamp.put("mfcc", currentMillisecond);
                break;
            case "pca":
                getMessage(new Date().toString(), pca + currentMillisecond);
                timeStamp.put("pca", currentMillisecond);
                break;
            case "vq":
                getMessage(new Date().toString(), vq + currentMillisecond);
                timeStamp.put("vq", currentMillisecond);
                break;
            case "hmm":
                getMessage(new Date().toString(), hmm + currentMillisecond);
                timeStamp.put("hmm", currentMillisecond);
                break;
            default:
                throw new IllegalArgumentException("Illegal input");
        }
    }


    @Override
    public void getMessage(String time, String context) {
        mainView.writeLog(context);
    }
}
