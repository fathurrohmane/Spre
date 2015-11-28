package data.vectorquantization;

import java.util.Vector;

/**
 * Created by Fathurrohman on 03-Nov-15.
 */
public class Centroid extends Point {

    public Vector<Point> pointCollection = new Vector<Point>();

    public Centroid() {

    }

    public void addPoint(Point point) {
        pointCollection.add(point);
    }

    public Vector<Point> getPointCollection() {
        return pointCollection;
    }

}
