package data.vectorquantization.LBG;

import java.util.Vector;

/**
 * Created by Fathurrohman on 18-Nov-15.
 */
public class Cluster extends Point {

    //All sample point in this cluster
    public Vector<Point> pointsCollection = new Vector<Point>();

    public Cluster(int dimension) {
        this.coordinates = new double[dimension];
        this.dimension = dimension;
    }

    public Cluster(double[] point) {
        super(point);
        //System.out.println("Cluster Created = ");
        //printCoordinate();
    }

    public void addPoint(Point point) {
        pointsCollection.add(point);
    }

    public Vector<Point> getPoints() {
        return pointsCollection;
    }

    public void addSelf(Point point) {
        for (int i = 0; i < getDimension(); i++) {
            setCoordinate(i, getCoordinate(i) + point.getCoordinate(i));
        }
    }

    public void divSelf(double divisor) {
        for (int i = 0; i < getDimension(); i++) {
            setCoordinate(i, getCoordinate(i) / divisor);
        }
    }
}
