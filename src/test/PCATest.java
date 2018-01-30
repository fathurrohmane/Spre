package test;

import Jama.Matrix;
import data.pca.PCA;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class PCATest {

    @Test
    public void getPCAResult() {
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
        double[][] matrix2 = new double[][]{
                {7, 4, 3},
                {4, 1, 8},
                {6, 3, 5},
                {8, 6, 1},
                {8, 5, 7},
                {7, 2, 9},
                {5, 3, 3},
                {9, 5, 8},
                {7, 4, 5},
                {8, 2, 2}
        };

        double[][] matrix4 = new double[][]{
                {4, 6, 10},
                {3, 10, 13},
                {-2, -6, -8}
        };


        double[][] matrix3 = new double[][]{
                {3, 0, 1},
                {-4, 1, 2},
                {-6, 0, -2},
        };

        System.out.println("Data :");
        Matrix originalData = new Matrix(matrix);
        originalData.print(3, 3);

        PCA pca = new PCA(matrix);

        System.out.println("Result 2-D");
        Matrix result = new Matrix(pca.getPCAResult(2));
        result.print(2, 5);
    }
}