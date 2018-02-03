package data.vectorquantization.LBG;

import audio.feature_extraction.MFCC;
import data.database.Codebook;
import data.database.DatabaseHandler;
import tools.IProcessListener;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Fathurrohman on 29-Dec-15.
 * Linde-Buzo-Gray / Generealised Lloyd Algorithm for Vector Quantization
 * http://www.iiste.org/Journals/index.php/CEIS/article/viewFile/2174/2186
 * Step :
 * 1. Initialize 1st Codebook
 * 2. Double size the Codebook by splitting
 * 3. Find Nearest Neighboard
 * 4. Find Average Distortion
 * 5. Centroid Update
 * 6. if (current - avg distortion) / avg distortion > threshold go to step 3
 * 7. if current cluster = max cluster end else back to step 2
 */
public class LBG {

    private double[][] mDataSample;
    private double[][] mCluster;

    private int[] mIdCluster;
    private int mDimensionSize;

    private double mAverageDistortion = 0.0;
    private double mSplitFactor = 0.005;
    private double mThreshold = 0.005;
    private int mNumCurrentCluster;

    private IProcessListener listener;

    /**
     * Constructor to set data training to class
     *
     * @param dataSample assign data training
     */
    public LBG(double[][] dataSample) {
        this.mDataSample = dataSample;
        mDimensionSize = dataSample[0].length;
    }

    public LBG(File directoryDatabase) {
        loadFromDataBase(directoryDatabase);
    }

    public LBG(List<MFCC> dataSample, String empty) {
        mDimensionSize = dataSample.get(0).getCeptra().length;
        double[][] dataArray = new double[dataSample.size()][mDimensionSize];

        this.setDataSample(dataArray);
    }

    public LBG(List<Point> dataSample) {
        double[][] dataArray = new double[dataSample.size()][dataSample.get(0).getDimension()];
        mDimensionSize = dataSample.get(0).getDimension();
        for (int i = 0; i < dataSample.size(); i++) {
            Point point = dataSample.get(i);
            dataArray[i] = point.getCoordinates();

        }
        this.setDataSample(dataArray);
    }

    /**
     * Begin calculating Codebook
     *
     * @param maxCluster for number of observation symbol usually 256
     */
    public void calculateCluster(int maxCluster) {
        writeMessage("Clustering .....");
        // Initialize cluster
        mCluster = new double[1][mDimensionSize];
        mCluster[0] = initializeCluster();
        mNumCurrentCluster = 1;

        if (maxCluster > 1) {
            mAverageDistortion = 0;
            for (double[] data : mDataSample
                    ) {
                mAverageDistortion += euclideanDistance(data, mCluster[0]);
            }

            mAverageDistortion /= (double) (mDataSample.length * mDimensionSize);

            mIdCluster = new int[mDataSample.length];

            do {
                mNumCurrentCluster = splitClusters();
                writeMessage("Number of Cluster = " + mNumCurrentCluster);
            } while (mNumCurrentCluster < maxCluster);
        }
    }

    private int splitClusters() {
        // Split
        int newClusterSize = mNumCurrentCluster * 2;
        double[][] newCluster = new double[newClusterSize][mDimensionSize];

        int id = 0;
        for (double[] clusterData :
                mCluster) {
            newCluster[id] = createNewCluster(clusterData, -1);
            newCluster[id + 1] = createNewCluster(clusterData, 1);
            id += 2;
        }

        mCluster = newCluster;

        // Iteration
        double currentAverageDistortion = 0;

        do {
            currentAverageDistortion = mAverageDistortion;

            // Find closest cluster
            for (int i = 0; i < mDataSample.length; i++) {
                double minimumDistortion = Double.MAX_VALUE;
                for (int j = 0; j < mCluster.length; j++) {
                    double distance = euclideanDistance(mDataSample[i], mCluster[j]);
                    if (minimumDistortion > distance) {
                        minimumDistortion = distance;
                        mIdCluster[i] = j;
                    }
                }
            }

            // Update Codebook
            for (int i = 0; i < mCluster.length; i++) {
                double[] newSingleCluster = new double[mDimensionSize];
                int counter = 0;
                for (int j = 0; j < mDataSample.length; j++) {
                    if (mIdCluster[j] == i) {
                        addPointValue(newSingleCluster, mDataSample[j]);
                        counter++;
                    }
                }

                if (counter > 0) {
                    multiplyPointValue(newSingleCluster, 1.0 / (double) counter);
                    mCluster[i] = newSingleCluster;
                }
            }

            // Update average Distortion
            mAverageDistortion = 0.0;
            for (int i = 0; i < mDataSample.length; i++) {
                mAverageDistortion += euclideanDistance(mDataSample[i], mCluster[mIdCluster[i]]);
            }

            mAverageDistortion /= (double) mDataSample.length * mDimensionSize;

        } while (((currentAverageDistortion - mAverageDistortion) / currentAverageDistortion) > mThreshold);

        return mCluster.length;
    }

    private double[] initializeCluster() {
        double[] firstCluster = new double[mDimensionSize];
        // Initialize first Codebook

        for (int i = 0; i < mDataSample.length; i++) {
            addPointValue(firstCluster, mDataSample[i]);
        }
        multiplyPointValue(firstCluster, 1.0 / (double) mDataSample.length);

        return firstCluster;
    }

    private double[] createNewCluster(double[] dataCluster, int factor) {
        double[] newCluster = new double[mDimensionSize];
        addPointValue(newCluster, dataCluster);
        multiplyPointValue(newCluster, 1.0 + (double) factor * mSplitFactor);

        return newCluster;
    }

    private void addPointValue(double[] vector1, double[] vector2) {
        for (int i = 0; i < vector1.length; i++) {
            vector1[i] += vector2[i];
        }
    }

    private void multiplyPointValue(double[] vector1, double factor) {
        for (int i = 0; i < vector1.length; i++) {
            vector1[i] *= factor;
        }
    }

    private double euclideanDistance(double[] vector1, double[] vector2) {
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            double d = vector1[i] - vector2[i];
            sum += (Math.pow(d, 2.0));
        }

        return Math.sqrt(sum);
    }

    private void setDataSample(double[][] mDataSample) {
        this.mDataSample = mDataSample;
    }

    public double[][] getCluster() {
        return mCluster;
    }

    public int getClosestClusterIndex(double[] data) {
        double min = Double.MAX_VALUE;
        int result = 0;
        for (int i = 0; i < mCluster.length; i++) {
            double distance = euclideanDistance(data, mCluster[i]);
            if (min > distance) {
                min = distance;
                result = i;
            }
        }

        return result;
    }

    /**
     * @param mfcc
     * @return observation sequence
     */
    public int[] getObservationSequence(MFCC mfcc) {
        int[] result = new int[mfcc.getCeptra().length];
        int counter = 0;
        for (double[] point :
                mfcc.getCeptra()) {

            result[counter] = getClosestClusterIndex(point);
            counter++;
        }
        return result;
    }

    /**
     * @param mfcc
     * @return observation sequence
     */
    public int[] getObservationSequence(double[][] mfcc) {
        int[] result = new int[mfcc.length];
        int counter = 0;

        for (int i = 0; i < mfcc[counter].length; i++) {
            result[counter] = getClosestClusterIndex(mfcc[counter]);
            counter++;
        }

        return result;
    }

//    /**
//     *
//     * @param mfcc
//     * @return observation sequence
//     */
//    public int[] getObservationSequence(double[] mfcc) {
//        int[] result = new int[mfcc.length];
//        int counter = 0;
//        for (double[] point :
//                mfcc.getCeptra()) {
//
//            result[counter] = getClosestClusterIndex(point);
//            counter++;
//        }
//        return result;
//    }

    public void loadFromDataBase(File directoryDatabase) {
        Codebook codebook = DatabaseHandler.loadCodeBook(directoryDatabase);
        mCluster = codebook.getCluster();
        mDimensionSize = codebook.getDimensionSize();
    }

    public void saveToDatabase() {
        Codebook codebook = new Codebook();

        codebook.setDimensionSize(mDimensionSize);
        codebook.setCluster(mCluster.clone());

        DatabaseHandler.saveCodebook(codebook);
        writeMessage("Codebook saved.");
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
