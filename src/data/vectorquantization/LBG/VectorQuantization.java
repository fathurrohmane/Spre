package data.vectorquantization.LBG;

import java.util.Vector;

/**
 * Created by Fathurrohman on 18-Nov-15.
 */
public class VectorQuantization {

    private static final double threshold = 0.001;

    //Split factor (0.01 <= SPLIT <= 0.05)
    private final double SPLIT = 0.005;


    private int maxCluster;
    private int currentTotalCluster;
    private int numberOfDimension;
    private int sampleSize;

    //all sample data
    private Vector<Point> dataPoint = new Vector<Point>();
    //all cluster data
    private Vector<Cluster> clusters = new Vector<Cluster>();

    private double distortionAverage;
    private int idClusterPerSample[];

    public VectorQuantization(double[][] data, int maxCluster) {
        this.maxCluster = maxCluster;
        this.numberOfDimension = data[0].length;
        this.sampleSize = data.length;
        this.idClusterPerSample = new int[sampleSize];
        setDataPoint(data);

        start();
        iteration();
    }

    //Set sample data to variable
    private void setDataPoint(double[][] data) {
        for (int i = 0; i < data.length; i++) {
            Point point = new Point(data[i]);
            dataPoint.add(point);
        }
        System.out.println("Sample data imported = "+ data.length);
        System.out.println("Dimension size = "+ numberOfDimension);
    }

    //Create initial cluster
    private void start() {
        //Create initial cluster and set its position
        Cluster cluster_1 = new Cluster(numberOfDimension);
        currentTotalCluster = 1;
        for (Point point : dataPoint) {
            cluster_1.addSelf(point);
        }
        cluster_1.divSelf(sampleSize);

        //Calculate average distortion
        for (int i = 0; i < dataPoint.size(); i++) {
            distortionAverage += dataPoint.get(i).calculateEuclidenDistance(cluster_1);
        }
        distortionAverage /= (dataPoint.size() * numberOfDimension);

        //add initial cluster to Array
        clusters.add(0,cluster_1);
    }

    private void iteration() {
        while (currentTotalCluster < maxCluster) {
            split();
            double dave_j_1 = 0.0;
            int j = 0;

            do {
                dave_j_1 = distortionAverage;

                //STEP1
                // find closest distance to centroid per sample data
                for (int i = 0; i < sampleSize; i++) {
                    double euclidenDistanceMin = Double.MAX_VALUE;

                    for (int k = 0; k < currentTotalCluster; k++) {
                        double eucldenDistance = dataPoint.get(i).calculateEuclidenDistance(clusters.get(k));

                        if (eucldenDistance < euclidenDistanceMin) {
                            euclidenDistanceMin = eucldenDistance;
                            idClusterPerSample[i] = k;
                        }
                    }
                }

                //STEP2
                //update codebook
                for (int i = 0; i < currentTotalCluster; i++) {
                    //Cluster cluster = new Cluster(clusters.get(i).getCoordinates());
                    Cluster cluster = new Cluster(numberOfDimension);
                    int numOf = 0;
                    for (int k = 0; k < sampleSize; k++) {
                        if (idClusterPerSample[k] == i) {
                            cluster.addSelf(dataPoint.get(k));
                            numOf++;
                        }
                    }
                    cluster.divSelf(numOf);
                    clusters.set(i, cluster);
                }

                //STEP3
                j++;
                //STEP4
                distortionAverage = 0.0;

                for (int i = 0; i < sampleSize; i++) {
                    distortionAverage += dataPoint.get(i).calculateEuclidenDistance(clusters.get(idClusterPerSample[i]));
                }
                distortionAverage /= (sampleSize * numberOfDimension);

            } while ((dave_j_1 - distortionAverage) / dave_j_1 > SPLIT);
        }
    }
    //Split data into
    private void split() {
        Vector<Cluster> oldCluster = (Vector<Cluster>) clusters.clone();
        clusters.clear();
        //Initialize new Clusters
        clusters = new Vector<Cluster>();
        for (int i = 0; i < (currentTotalCluster + 2); i++) {
            clusters.add(new Cluster(numberOfDimension));
        }
        for (int i = 0; i < currentTotalCluster; i++) {
            clusters.set(i, oldCluster.get(i).mul(1 + SPLIT));
            clusters.set((i + currentTotalCluster), oldCluster.get(i).mul(1 - SPLIT));
        }

//        for (int i = 0; i < currentTotalCluster; i++) {
//            System.out.println(clusters.get(i).getDimension());
//        }

        currentTotalCluster *= 2;
    }

    public void print() {
        System.out.println("Cluster : ");
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < numberOfDimension; j++) {
                System.out.print(clusters.get(i).getCoordinate(j) + " ");
            }
            System.out.println();
        }
    }
}
