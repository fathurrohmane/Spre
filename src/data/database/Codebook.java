package data.database;

import java.io.Serializable;

/**
 * Created by Fathurrohman on 17-Dec-15.
 * Class Model to save Codebook data to file via serializable
 */
public class Codebook implements Serializable {

    private int mDimensionSize;

    private double[][] mCluster;

    public Codebook() {
    }

    public Codebook(int mDimensionSize, double[][] mCluster) {
        this.mDimensionSize = mDimensionSize;
        this.mCluster = mCluster;
    }

    public int getDimensionSize() {
        return mDimensionSize;
    }

    public void setDimensionSize(int mDimensionSize) {
        this.mDimensionSize = mDimensionSize;
    }

    public double[][] getCluster() {
        return mCluster;
    }

    public void setCluster(double[][] mCluster) {
        this.mCluster = mCluster;
    }
}
