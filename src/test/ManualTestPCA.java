package test;

import Jama.Matrix;
import data.pca.PCA;

import java.util.List;

/**
 * Created by Fathurrohman on 28-Nov-15.
 */
public class ManualTestPCA {

    public static void main(String[] args){

        double[][] matrix = new double[][]{
                {2.5, 2.4},
                {0.5, 0.7},
                {2.2, 2.9},
                {1.9, 2.2},
                {3.1, 3.0},
                {2.3, 2.7},
                {2, 1.6},
                {1, 1.1},
                {1.5, 1.6},
                {1.1, 0.9}
        };

        PCA pca = new PCA(matrix);
        int numberofdimension = 2;
        List<PCA.PrincipleComponent> mainComponents = pca.getDominantComponents(numberofdimension);

        Matrix features = PCA.getDominantComponentsMatrix(mainComponents);

        features.print(8,4);



    }

}
