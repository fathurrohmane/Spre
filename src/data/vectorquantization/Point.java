package data.vectorquantization;

/**
 * Created by Fathurrohman on 22-Oct-15.
 */
public class Point {

    private double coordinates[];

    private int dimension;

    public Point() {

    }

    public Point(double coordinates[]) {
        this.coordinates = coordinates;
        dimension = coordinates.length;
    }

    public double getCoordinates(int i) {
        return coordinates[i];
    }

    public void setCoordinates(int i, double value) {
        coordinates[i] = value;
    }

    public int getDimension() {
        return dimension;
    }

}
