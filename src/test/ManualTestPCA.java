package test;

import Jama.Matrix;
import data.database.DatabaseHandler;
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

        System.out.println("Data :");
        Matrix originalData = new Matrix(matrix);
        originalData.print(8,2);

        PCA pca = new PCA(matrix);

        System.out.println("Result 2-D");
        Matrix result = new Matrix(pca.getPCAResult(2));
            result.print(8,8);

        System.out.println("Result 1-D");
        Matrix result2 = new Matrix(pca.getPCAResult(1));
            result2.print(8,8);

    }


}
