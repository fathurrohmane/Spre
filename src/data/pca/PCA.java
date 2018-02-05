package data.pca;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import data.Process;
import data.database.DatabaseHandler;
import data.vectorquantization.LBG.Point;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Fathurrohman on 25-Nov-15.
 * Based on http://www.cs.otago.ac.nz/cosc453/student_tutorials/principal_components.pdf
 */
public class PCA extends Process implements Serializable {

    private double[][] data;
    private double[] means;

    private List<PrincipleComponent> principleComponents;

    public PCA(List<Point> dataSample) {
        double[][] dataArray = new double[dataSample.size()][dataSample.get(0).getDimension()];
        for (int i = 0; i < dataSample.size(); i++) {
            Point point = dataSample.get(i);
            dataArray[i] = point.getCoordinates();
        }
        this.data = dataArray;
        means = new double[dataSample.get(0).getDimension()];

        calculate();
    }

    public PCA(double[][] dataSample) {
        //initiation
        data = new double[dataSample.length][dataSample[0].length];
        means = new double[dataSample[0].length];
        data = dataSample.clone();

        writeLog("Calculating PCA");
        calculate();
        writeLog("PCA Done!");
    }

    private void calculate() {
        //calculate mean for each dimension
        means = calculateMean(data);
        //Adjust data by subtract by its mean
        data = adjustedData(data, means, false);
        //calculate mean for adjusted data
        means = calculateMean(data);

        //calculate covariance
        double[][] covariance = getCovariances(data, means);
        Matrix covarianceInMatrix = new Matrix(covariance);
        //System.out.println("Matrix Covariance :");
        // FIXME: 05/02/2018 write matrix to console
        covarianceInMatrix.print(data[0].length, 5);
        EigenvalueDecomposition eigenData = covarianceInMatrix.eig();

        //calculate eigen vector and value
        double[] eigenValues = eigenData.getRealEigenvalues();
        Matrix eigenVectors = eigenData.getV();
        //System.out.println("Eigen Vector :");
        eigenVectors.print(data[0].length, 5);
        //System.out.println("Eigen Value :");
        for (double eigenValue : eigenValues) {
            //System.out.println(eigenValue);
        }

        double[][] vecs = eigenVectors.getArray();
        int numOfComponents = eigenVectors.getColumnDimension(); // same as num rows.
        principleComponents = new ArrayList<>();

//Sort by eigen value -> higher eigenvalue higher priority
        for (int i = 0; i < numOfComponents; i++) {
            double[] eigenVector = new double[numOfComponents];
            System.arraycopy(vecs[i], 0, eigenVector, 0, numOfComponents);
            principleComponents.add(new PrincipleComponent(eigenValues[i], eigenVector));
        }

        Collections.sort(principleComponents);
    }

    //Set result dimension -> dimensional reduction
    public double[][] getPCAResult(int numberOfDimension) {
        //List of eigen vector sorted by eigenvalue
        List<PCA.PrincipleComponent> mainComponents = getDominantComponents(numberOfDimension);
        Matrix features = getDominantComponentsMatrix(mainComponents);
        //System.out.println("Feature Matrix");
        //features.print(2, 4);

        //convert original data to Matrix
        Matrix originalDataSubtractedByMean = new Matrix(data);

        //featureVectorTranspose * originalDataSubtractedbyMeanTranspose
        Matrix featureTranspose = features.transpose();
        //System.out.println("Feature Matrix Transposed");
        //featureTranspose.print(2, 4);
        Matrix originalDataAdjusted = originalDataSubtractedByMean.transpose();
        Matrix result = featureTranspose.times(originalDataAdjusted);

        //Result in array
        return result.transpose().getArray();
    }

    /**
     * Tranform input data with this PCA
     * @param input data to reduce
     * @param numberOfDimension number of dimension to keep
     * @return input data reduced
     */
    public double[][] getPCAResult(double[][] input, int numberOfDimension) {
        if (principleComponents != null) {
            //List of eigen vector sorted by eigenvalue
            List<PCA.PrincipleComponent> mainComponents = getDominantComponents(numberOfDimension);
            Matrix features = getDominantComponentsMatrix(mainComponents);
            //System.out.println("Feature Matrix");
            //features.print(2, 4);
            //Adjust data input
            double[][] adjustedInput = adjustedData(input, calculateMean(input), false);
            Matrix originalDataSubtractedByMean = new Matrix(adjustedInput);

            //featureVectorTranspose * originalDataSubtractedByMeanTranspose
            Matrix featureTranspose = features.transpose();
            //System.out.println("Feature Matrix Transposed");
            //featureTranspose.print(2, 4);
            Matrix originalDataAdjusted = originalDataSubtractedByMean.transpose();
            Matrix result = featureTranspose.times(originalDataAdjusted);

            //Result in array
            return result.transpose().getArray();
        } else {
            throw new IllegalArgumentException("Principal component is missing! Cant perform dimension reduction if its empty. Maybe its not loaded yet from file?");
        }

    }

    //Set result dimension -> dimensional reduction
    public double[][] getPercentagePCAResult(int minPercentage) {
        double currentEigenValue = 0;
        double totalEigenValue = 0;
        int numberOfDimension = 0;
        // calculate total of eigen value
        for (PrincipleComponent p : principleComponents
                ) {
            totalEigenValue += p.eigenValue;
        }
        // calculate percentage
        Iterator<PrincipleComponent> iterator = principleComponents.iterator();
        do {
            PrincipleComponent principleComponent = iterator.next();
            currentEigenValue += principleComponent.eigenValue;
            numberOfDimension++;
        } while ((((currentEigenValue / totalEigenValue) * 100) < minPercentage) && iterator.hasNext());

        //List of eigen vector sorted by eigenvalue
        List<PCA.PrincipleComponent> mainComponents = getDominantComponents(numberOfDimension);
        Matrix features = getDominantComponentsMatrix(mainComponents);

        //convert original data to Matrix
        Matrix originalDataSubtractedByMean = new Matrix(data);

        //featureVectorTranspose * originalDataSubtractedByMeanTranspose
        Matrix featureTranspose = features.transpose();
        Matrix originalDataAdjusted = originalDataSubtractedByMean.transpose();
        Matrix result = featureTranspose.times(originalDataAdjusted);

        //Result in array
        return result.transpose().getArray();
    }

    /**
     * Perform addition or subtraction from a data and its mean
     *
     * @param data     input
     * @param means    its means
     * @param addition if true (data + means) if false (data - means)
     * @return data that has been adjusted with its mean
     * CHECKED!
     */
    private double[][] adjustedData(double[][] data, double[] means, boolean addition) {
        double[][] output = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (addition) {
                    output[i][j] = data[i][j] + means[j];
                } else {
                    output[i][j] = data[i][j] - means[j];
                }
            }
        }
        return output;
    }

    /**
     * Calculate means of the data
     * @param data data input
     * @return its mean
     * CHECKED!
     */
    private double[] calculateMean(double[][] data) {
        int numData = data.length;
        int n = data[0].length;

        double[] sum = new double[n];
        double[] mean = new double[n];

        //Sum all data
        for (double[] vec : data) {
            for (int j = 0; j < data[0].length; j++) {
                sum[j] += vec[j];
            }
        }

        //divide by its total
        for (int i = 0; i < sum.length; i++) {
            mean[i] = sum[i] / numData;
        }
        return mean;
    }

    private Matrix getDominantComponentsMatrix(List<PrincipleComponent> dom) {
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

        double[][] cov = new double[numDimension][numDimension];

        for (int i = 0; i < numData; i++) {
            for (int j = i; j < numDimension; j++) {
                double result = calculateCovariance(dataInput, i, j, meansInput);
                cov[i][j] = result;
                cov[j][i] = result;
            }
        }
        return cov;
    }

    //Calculate covariance
    private double calculateCovariance(double[][] data, int colA, int colB, double[] means) {
        double covariance = 0.0;

        for (double[] aData : data) {
            double a = aData[colA] - means[colA];
            double b = aData[colB] - means[colB];

            covariance += (a * b);
        }

        covariance = (covariance / (data.length - 1));
        return covariance;
    }

    private List<PrincipleComponent> getDominantComponents(int n) {
        List<PrincipleComponent> ret = new ArrayList<>();
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

    public double[][] getOriginalDataBack() {
        //List of eigen vector sorted by eigenvalue
        List<PCA.PrincipleComponent> mainComponents = getDominantComponents(data[0].length);
        Matrix features = getDominantComponentsMatrix(mainComponents);
        Matrix finalData = new Matrix(getPCAResult(data[0].length));
        Matrix result = features.inverse().times(finalData);

        return adjustedData(result.getArray(), means, true);
    }

    public void saveToDisk() {
        DatabaseHandler.savePCA(this);
    }

    public class PrincipleComponent implements Comparable<PrincipleComponent>, Serializable {
        private double eigenValue;
        private double[] eigenVector;

        PrincipleComponent(double eigenValue, double[] eigenVector) {
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

        public String toString() {
            return "Principle Component, eigenvalue: " + eigenValue+ ", eigen vector: ["
                    + Arrays.toString(eigenVector) + "]";
        }
    }

}
