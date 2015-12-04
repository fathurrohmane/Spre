package data.pca;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import org.omg.PortableInterceptor.INACTIVE;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Fathurrohman on 25-Nov-15.
 * Basedon http://www.cs.otago.ac.nz/cosc453/student_tutorials/principal_components.pdf
 */
public class PCA {

    private double[][] data;
    private double[] means;

    private Matrix eigenVectors;
    private EigenvalueDecomposition eigenData;
    private double[] eigenValues;
    private SortedSet<PrincipleComponent> principleComponents;

    public PCA(double[][] dataSample) {
//initiation
            data = new double[dataSample.length][dataSample[0].length];
            means = new double[dataSample[0].length];
            data = dataSample.clone();
//calculate mean for each dimension
            means = calculateMean(data);
//Adjust data by subtract by its mean
            data = adjustedData(data,means);
//calculate covariance
            double[][] covariance = getCovariances(data, means);
            Matrix covarianceInMatrix = new Matrix(covariance);
                System.out.println("Matrix Covariance :");
                covarianceInMatrix.print(data[0].length,data[0].length);
            eigenData = covarianceInMatrix.eig();
//calculate eigen vector and value
            eigenValues = eigenData.getRealEigenvalues();
            eigenVectors = eigenData.getV();
                System.out.println("Eigen Vector :");
                eigenVectors.print(data[0].length,data[0].length);
                System.out.println("Eigen Value :");
                for (int i = 0; i < eigenValues.length; i++) {
                    System.out.println(eigenValues[i]);
                }
            double[][] vecs = eigenVectors.getArray();
            int numOfComponents = eigenVectors.getColumnDimension(); // same as num rows.
            principleComponents = new TreeSet<PrincipleComponent>();
//Sort by eigen value -> higher eigenvalue higer priority
        for (int i = 0; i < numOfComponents; i++) {
            double[] eigenVector = new double[numOfComponents];
            for (int j = 0; j < numOfComponents; j++) {
                eigenVector[j] = vecs[i][j];
            }
            principleComponents.add(new PrincipleComponent(eigenValues[i], eigenVector));
        }
//
        //Set result dimension -> dimentional reduction

    }

    public double[][] getPCAResult(int numberofdimension) {
//List of eigen vector sorted by eigenvalue
        List<PCA.PrincipleComponent> mainComponents = getDominantComponents(numberofdimension);
        Matrix features = PCA.getDominantComponentsMatrix(mainComponents);

//convert original data to Matrix
        Matrix originalDataSubtractedbyMean = new Matrix(data);

//fetureVectorTranspose * originalDataSubtractedbyMeanTranspose
        Matrix featureTranspose = features.transpose();
        Matrix originalDataAdjusted = originalDataSubtractedbyMean.transpose();
        Matrix result = featureTranspose.times(originalDataAdjusted);
//Result in array
        return result.transpose().getArray();
    }

    public double[][] adjustedData(double[][] data, double[] means) {
        double[][] output = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                output[i][j] = data[i][j] - means[j];
            }
        }
        return output;
    }

    private double[] calculateMean(double[][] data) {
        int numData = data.length;
        int n = data[0].length;

        double[] sum = new double[n];
        double[] mean = new double[n];

        //Sum all data
        for (int i = 0; i < numData; i++) {
            double[] vec = data[i];
            for (int j = 0; j < data[0].length; j++) {
                sum[j] += vec[j];
            }
        }

        //divide by its total
        for (int i = 0; i < sum.length; i++) {
            mean[i] = sum[i] / data.length;
        }
        return mean;
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

    private double[][] getCovariances(double[][] dataInput, double[] meansInput) {
        int numData = dataInput.length;
        int numDimension = dataInput[0].length;

        double[] mean = calculateMean(dataInput);

        double[][] cov = new double[numDimension][numDimension];

        for (int i = 0; i < numData; i++) {
            for (int j = i; j < numDimension; j++) {
                double result = calculateCovariance(dataInput, i, j, mean);
                cov[i][j] = result;
                cov[j][i] = result;
            }
        }

        //System.arraycopy(mean, 0 , meansInput, 0 , mean.length);

        return cov;
    }

    //Calculate covariance
    private double calculateCovariance(double[][] data, int colA, int colB, double[] means) {
        double covariance = 0.0;

        for (int i = 0; i < data.length; i++) {
            double a = data[i][colA] - means[colA];
            double b = data[i][colB] - means[colB];

            covariance += (a * b);
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

    public double[] getMeans() {
        return means;
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
