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


        VectorQuantization vq = new VectorQuantization("test",sample,4);// FIXME: 18-Dec-15
        //vq.print();

        GenLloyd g = new GenLloyd(sample2);
        g.calcClusters(4);

        LBG lbg = new LBG(sample1);
        lbg.calculateCluster(4);

        int[] observation = new int[9];
        int[] observation_1 = new int[9];

        System.out.println("Cluster= ");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                Cluster c = vq.getClusters().get(j);
                System.out.print(g.getClusterPoints()[i][j] +" X "+lbg.getCluster()[i][j]+" X"+c.getCoordinate(j));
                System.out.println();
            }
            System.out.println("Next");
        }

        System.out.println("Observation= ");
        for (int i = 0; i < 9; i++) {
            observation[i] = vq.getClosestCentroidIndex(sample[i]);
            observation_1[i] = lbg.getClosestClusterIndex(sample[i]);
            System.out.println(observation[i] +" X "+ observation_1[i]);
        }
    }

}
