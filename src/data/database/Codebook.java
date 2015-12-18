package data.database;

import data.vectorquantization.LBG.Cluster;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by Fathurrohman on 17-Dec-15.
 */
public class Codebook implements Serializable {

    private int dimension;

    private Vector<Cluster> clusters = new Vector<Cluster>();

    public Codebook() {
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public Vector<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(Vector<Cluster> clusters) {
        this.clusters = clusters;
    }
}
