package data.vectorquantization.LBG;

import data.database.Codebook;
import data.database.DatabaseHandler;

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
    private double mSplitFactor = 0.0005;
    private double mThreshold = 0.0005;
    private int mNumCurrentCluster;

    /**
     * Constructor to set data training to class
     *
     * @param dataSample assign data training
     */
    public LBG(double[][] dataSample) {
        this.mDataSample = dataSample;
        mDimensionSize = dataSample[0].length;
    }

    /**
     * Begin calculating Codebook
     *
     * @param maxCluster for number of observation symbol usually 256
     */
    public void calculateCluster(int maxCluster) {
        // Initialize first Codebook
        mCluster = new double[1][mDimensionSize];
        mNumCurrentCluster = 1;

        for (int i = 0; i < mDataSample.length; i++) {
            addPointValue(mCluster[0], mDataSample[i]);
        }
        multiplyPointValue(mCluster[0], 1.0 / (double) mDataSample.length);


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
            } while (mNumCurrentCluster < maxCluster);
        }
    }

    private int splitClusters() {
        // Split
        int newClusterSize = mNumCurrentCluster * 2;
        double[][] newCluster = new double[newClusterSize][mDimensionSize];

        for (int i = 0; i < mCluster[0].length; i += 2) {
            newCluster[i] = createNewCluster(mCluster[i], -1);
            newCluster[i + 1] = createNewCluster(mCluster[i], 1);
        }

        mCluster = newCluster;

        // Iteration
        double currentAverageDistortion = 0;

        do {
            currentAverageDistortion = mAverageDistortion;

            for (int i = 0; i < mDataSample.length; i++) {
                double minimumDistortion = Double.MAX_VALUE;
                for (int j = 0; j < mCluster.length; j++) {
                    double distance = euclideanDistance(mDataSample[i], mCluster[j]);
                    // Find closest cluster
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

                if (counter > 1) {
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

    private double[] createNewCluster(double[] dataCluster, int factor) {
        double[] newCluster = new double[mDimensionSize];
        addPointValue(newCluster, dataCluster);
        multiplyPointValue(newCluster, 1.0 + (double) factor * mSplitFactor);

        return newCluster;
    }

    public void addPointValue(double[] vector1, double[] vector2) {
        for (int i = 0; i < vector1.length; i++) {
            vector1[i] += vector2[i];
        }
    }

    public void multiplyPointValue(double[] vector1, double factor) {
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

        //return Math.sqrt(sum);
        return sum;
    }

    public double[][] getCluster() {
        return mCluster;
    }

    public int getClosestClusterIndex(double[] data) {
        double min = Double.MAX_VALUE;
        int result = 0;
        for (int i = 0; i < mCluster.length; i++) {
            double distance = euclideanDistance(data, mCluster[i]);
            if(min > distance) {
                min = distance;
                result = i;
            }
        }

        return result;
    }

//    public void loadFromDataBase() {
//        Codebook codebook = DatabaseHandler.loadCodeBook(name);
//        clusters = codebook.getClusters();
//    }
//
//    public void saveToDatabase() {
//        Codebook codebook = new Codebook();
//
//        codebook.setDimension(numberOfDimension);
//        codebook.setClusters(clusters);
//
//        DatabaseHandler.saveCodebook(name, codebook);
//    }
}
