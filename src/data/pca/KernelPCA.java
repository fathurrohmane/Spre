package data.pca;

/**
 * Created by Fathurrohman on 04-Aug-16.
 */
public class KernelPCA {

    private int numberOfFrames;
    private int numberOfFilters;
    private double[][] melfiter;


    public KernelPCA(double[][] data) {
        melfiter = data;
        numberOfFrames = data.length;
        numberOfFilters = data[0].length;
    }
}
