package test;

import data.vectorquantization.LBG.VectorQuantization;
import data.vectorquantization.mkonrad.cluster.GenLloyd;

import java.util.Vector;

/**
 * Created by Fathurrohman on 19-Nov-15.
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

        VectorQuantization vq = new VectorQuantization(sample,4);
        vq.print();


        GenLloyd gl = new GenLloyd(sample);
        gl.calcClusters(4);

        double[][] results = gl.getClusterPoints();
        for (double[] point : results) {
            System.out.print("Cluster : ");
            for (double po : point) {
                System.out.print(po + " ");
            }
            System.out.println();
        }
    }


}
