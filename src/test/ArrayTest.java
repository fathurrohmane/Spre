package test;

import org.junit.Assert;
import org.junit.Test;
import tools.array.Array;

import static org.junit.Assert.*;

public class ArrayTest {

    @Test
    public void addArray() {
        double[][] one = new double[][]{
                {1, 2},
                {3, 4},
        };

        double[][] two = new double[][]{
                {5, 6},
                {7, 8},
                {9, 10},
                {11, 12},
                {13, 14},
                {15, 16},
                {15, 16}
        };

        double[][] three = new double[][]{
                {17, 18}
        };

        double[][] four = new double[][]{
                {19, 20}
        };

        double[][] correctAnswer = new double[][]{
                {1, 2},
                {3, 4},
                {5, 6},
                {7, 8},
                {9, 10},
                {11, 12},
                {13, 14},
                {15, 16},
                {15, 16},
                {17, 18},
                {19, 20}
        };


        double[][] result = new double[0][2];
        result = Array.addArray(result, one);
        result = Array.addArray(result, two);
        result = Array.addArray(result, three);
        result = Array.addArray(result, four);

        for (int i = 0; i < correctAnswer.length; i++) {
            for (int j = 0; j < correctAnswer[i].length; j++) {
                Assert.assertEquals(result[i][j], correctAnswer[i][j], 0.1);
            }
        }
        Array.print("Test Add Array", result);
    }

    @Test
    public void addToSideArray() {
        double[] result = new double[0];
        double[] one = new double[]{1, 2};
        double[] two = new double[]{3, 4, 5};
        double[] three = new double[]{6, 7, 8};
        double[] correctResult = new double[]{1, 2, 3, 4, 5, 6, 7, 8};

        result = Array.addToSideArray(result, one);
        result = Array.addToSideArray(result, two);
        result = Array.addToSideArray(result, three);

        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(result[i], correctResult[i], 0.1);
        }
    }

    @Test
    public void normalize() {
        double[][] one = new double[3][];
        one[0] = new double[]{0, 1};
        one[1] = new double[]{2, 3, 4};
        one[2] = new double[]{5};

        double[][] correctAnswer = new double[3][];
        correctAnswer[0] = new double[]{0, 1, 0};
        correctAnswer[1] = new double[]{2, 3, 4};
        correctAnswer[2] = new double[]{5, 0, 0};

        double[][] result = Array.normalize(one);

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                Assert.assertEquals(correctAnswer[i][j], one[i][j], 0.1);
            }
        }
    }

    @Test
    public void copy2D() {
    }

    @Test
    public void copy2D1() {
    }
}