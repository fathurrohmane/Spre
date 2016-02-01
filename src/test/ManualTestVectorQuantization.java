package test;

import data.vectorquantization.LBG.Cluster;
import data.vectorquantization.LBG.LBG;
import data.vectorquantization.LBG.VectorQuantization;
import test.validator.mkonrad.GenLloyd;
import tools.Array;

/**
 * Created by Fathurrohman on 19-Nov-15.
 * This class for testing VectorQuantization class
 * Result tested using Markus Konrad VQ class
 */
public class ManualTestVectorQuantization {

    public static void main(String[] args) {
        double[][] sample = new double[9][2];

        sample[0][0] = -1.5;     sample[0][1] = -1.5;
        sample[1][0] = -1.5;     sample[1][1] = 2;
        sample[2][0] = -2;       sample[2][1] = -2;
        sample[3][0] = 1;        sample[3][1] = 1;
        sample[4][0] = 1.5;      sample[4][1] = 1.5;
        sample[5][0] = 1;        sample[5][1] = 2;
        sample[6][0] = 1;        sample[6][1] = -2;
        sample[7][0] = 1;        sample[7][1] = -3;
        sample[8][0] = 1;        sample[8][1] = -2.5;

        double[][] sample1 = new double[9][2];
        Array.copy2D(sample,sample1);
        double[][] sample2 = new double[9][2];
        Array.copy2D(sample,sample2);


        VectorQuantization vq = new VectorQuantization("test",sample,4);
        vq.print();

        GenLloyd g = new GenLloyd(sample2);
        g.calcClusters(4);

        LBG lbg = new LBG(sample1);
        lbg.calculateCluster(4);

        printCluster(lbg.getCluster());

        printCluster(g.getClusterPoints());
    }

    public static void printCluster(double[][] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
