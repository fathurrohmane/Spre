package data.vectorquantization;

import java.io.Serializable;

/**
 * Created by Fathurrohman on 12-Nov-15.
 */
public class CodeBookDatabase implements Serializable {

    private int dimension;

    private Centroid[] centroids;

    public CodeBookDatabase() {

    }

    public Centroid[] getCentroids() {
        return centroids;
    }

    public void setCentroids(Centroid[] centroids) {
        this.centroids = centroids;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
}
