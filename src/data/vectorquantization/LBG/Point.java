package data.vectorquantization.LBG;

import tools.array.Array;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Fathurrohman on 22-Oct-15.
 */
public class Point {

    protected double coordinates[];
    protected int dimension;

    public Point() {

    }

    public Point(double[] coordinates) {
        setCoordinates(coordinates);
    }

    /**
     * Add more coordinate to Point
     *
     * @param coordinates new coordinate to add to current
     */
    public void add(double[] coordinates) {
        if (this.coordinates == null) {
            setCoordinates(coordinates);
        } else {
            // Create new array
            double[] newCoordinate = new double[this.coordinates.length + coordinates.length];
            // Copy current array to new coordinate variable
            System.arraycopy(this.coordinates, 0, newCoordinate, 0, coordinates.length);
            // Copy new array to new coordinate variable
            System.arraycopy(coordinates, 0, newCoordinate, newCoordinate.length - coordinates.length, coordinates.length);
            // Assign it
            dimension = newCoordinate.length;
            this.coordinates = newCoordinate;
        }
    }

    public double getCoordinate(int i) {
        return coordinates[i];
    }

    public void setCoordinate(int i, double value) {
        coordinates[i] = value;
        this.dimension = coordinates.length;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        if (this.coordinates != null) {
            this.coordinates = new double[coordinates.length];
            this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
            this.dimension = coordinates.length;
        } else {
            this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
            this.dimension = coordinates.length;
        }
    }

    public int getDimension() {
        return dimension;
    }

    public double calculateEuclideanDistance(Point point_2) {
        double output = 0.0;
        if (getDimension() == point_2.getDimension()) {
            for (int i = 0; i < getDimension(); i++) {
                output += Math.pow(getCoordinate(i) - point_2.getCoordinate(i), 2);
            }
        } else {
            throw new IllegalArgumentException("Not same type of Point (different dimension)");
        }

        output = Math.sqrt(output);
        return output;
    }

    public Cluster mul(double factor) {
        double output[] = new double[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            output[i] = getCoordinate(i) * factor;
        }

        return new Cluster(output);
    }

    public void printCoordinate() {
        for (int i = 0; i < getDimension(); i++) {
            System.out.print(" " + getCoordinate(i));
        }
        System.out.println();
    }

    /**
     * Normalizer array size
     *
     * @param input points
     * @Return int biggestDimension
     */
    public static int normalizeDimensionSize(List<Point> input) {
        int biggestDimension = Integer.MIN_VALUE;

        // Search biggest dimension
        for (Point point : input) {
            if (point.getDimension() > biggestDimension) {
                biggestDimension = point.getDimension();
            }
        }

        // create it
        for (Point point : input) {
            if (point.getDimension() < biggestDimension) {
                double[] increasedSizeArrayWithPaddingZero = new double[biggestDimension];
                Array.copy(point.coordinates, increasedSizeArrayWithPaddingZero);
                point.setCoordinates(increasedSizeArrayWithPaddingZero);
            }
        }

        return biggestDimension;
    }

}
