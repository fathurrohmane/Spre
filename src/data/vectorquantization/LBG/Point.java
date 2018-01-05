package data.vectorquantization.LBG;

import java.util.Arrays;

/**
 * Created by Fathurrohman on 22-Oct-15.
 *
 */
public class Point {

    protected double coordinates[];

    protected int dimension;

    public Point() {

    }

    public Point(double coordinates[]) {
        setCoordinates(coordinates);
    }

    public double getCoordinate(int i) {
        return coordinates[i];
    }

    public void setCoordinate(int i, double value) {
        coordinates[i] = value;
        this.dimension = coordinates.length;
    }

    public double[] getCoordinates() { return coordinates; }

    public void setCoordinates(double[] coordinates) {
        if(this.coordinates != null) {
            this.coordinates = new double[coordinates.length];
            this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
            this.dimension = coordinates.length;
        } else {
            this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
            this.dimension = coordinates.length;
        }
    }

    public int getDimension() {return dimension; }

    public double calculateEuclidenDistance(Point point_2) {
        double output = 0.0;
        if(getDimension() == point_2.getDimension()) {
            for (int i = 0; i < getDimension(); i++) {
                output += Math.pow(getCoordinate(i) - point_2.getCoordinate(i),2);
            }
        }else {
            System.out.println("Not same type of Point (different dimension)");
        }

        output = Math.sqrt(output);
        return output;
    }

    public Cluster mul(double factor) {
        double output[] = new double[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            output[i] = getCoordinate(i)*factor;
        }

        return new Cluster(output);
    }

    public void printCoordinate() {
        for (int i = 0; i < getDimension(); i++) {
            System.out.print(" " + getCoordinate(i));
        }
        System.out.println();
    }

}
