package data.pca;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Fathurrohman on 25-Nov-15.
 */
public class PCA {

    private double[][] data;
    private double[] means;

    private Matrix eigenVectors;
    private EigenvalueDecomposition eigenData;
    private double[] eigenValues;
    private SortedSet<PrincipleComponent> principleComponents;

    public PCA(double[][] data) {
        //initiation
            data = new double[data.length][data[0].length];
            means = new double[data[0].length];
        //calculate mean for each dimension
            calculateMean(data);
        //calculate covariance
            double[][] covariance = getCovariances(data, means);
            Matrix covarianceInMatrix = new Matrix(covariance);
            eigenData = covarianceInMatrix.eig();
        //calculate eigen vector and value
            eigenValues = eigenData.getRealEigenvalues();
            eigenVectors = eigenData.getV();
            double[][] vecs = eigenVectors.getArray();
            int numOfComponents = eigenVectors.getColumnDimension(); // same as num rows.
            principleComponents = new TreeSet<PrincipleComponent>();
        for (int i = 0; i < numOfComponents; i++) {
            double[] eigenVector = new double[numOfComponents];
            for (int j = 0; j < numOfComponents; j++) {
                eigenVector[j] = vecs[i][j];
            }
            principleComponents.add(new PrincipleComponent(eigenValues[i], eigenVector));
        }
    }

    private void calculateMean(double[][] data) {
        //Sum all data
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                means[j] += data[i][j];
            }
        }

        //divide by its total
        for (int i = 0; i < data[0].length; i++) {
            means[i] /= data.length;
        }
    }

    public static Matrix getDominantComponentsMatrix(List<PrincipleComponent> dom) {
        int nRows = dom.get(0).eigenVector.length;
        int nCols = dom.size();
        Matrix matrix = new Matrix(nRows, nCols);
        for (int col = 0; col < nCols; col++) {
            for (int row = 0; row < nRows; row++) {
                matrix.set(row, col, dom.get(col).eigenVector[row]);
            }
        }
        return matrix;
    }

    private double[][] getCovariances(double[][] data, double[] means) {
        int numData = data.length;
        int numDimension = data[0].length;

        double[][] cov = new double[numDimension][numDimension];

        for (int i = 0; i < numData; i++) {
            for (int j = i; j < numDimension; j++) {
                double result = calculateCovariance(data, i, j, means);
                cov[i][j] = result;
                cov[j][i] = result;
            }
        }

        return cov;
    }

    //Calculate covariance
    private double calculateCovariance(double[][] data, int colA, int colB, double[] means) {
        double covariance = 0.0;

        for (int i = 0; i < data.length; i++) {
            double a = data[i][colA] - means[colA];
            double b = data[i][colB] - means[colB];

            covariance += a * b;
        }

        covariance = (covariance / (data.length - 1));
        return covariance;
    }

    public List<PrincipleComponent> getDominantComponents(int n) {
        List<PrincipleComponent> ret = new ArrayList<PrincipleComponent>();
        int count = 0;
        for (PrincipleComponent pc : principleComponents) {
            ret.add(pc);
            count++;
            if (count >= n) {
                break;
            }
        }
        return ret;
    }

    public static class PrincipleComponent implements Comparable<PrincipleComponent> {
        public double eigenValue;
        public double[] eigenVector;

        public PrincipleComponent(double eigenValue, double[] eigenVector) {
            this.eigenValue = eigenValue;
            this.eigenVector = eigenVector;
        }

        public int compareTo(PrincipleComponent o) {
            int ret = 0;
            if (eigenValue > o.eigenValue) {
                ret = -1;
            } else if (eigenValue < o.eigenValue) {
                ret = 1;
            }
            return ret;
        }

//        public String toString() {
//            return "Principle Component, eigenvalue: " + Debug.num(eigenValue) + ", eigenvector: ["
//                    + Debug.num(eigenVector) + "]";
//        }
    }

}
