package data.database;

import data.vectorquantization.LBG.Cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Fathurrohman on 17-Dec-15.
 */
public class Codebook implements Serializable {

    private int dimension;

    private List<Cluster> clusters = new ArrayList<Cluster>();

    public Codebook() {
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
    }
}
