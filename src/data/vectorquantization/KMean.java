package data.vectorquantization;

/**
 * Created by Fathurrohman on 22-Oct-15.
 */
public class KMean {

    private int numOfCluster = 3;

    private Point tempPoints[];

    public KMean() {

    }

    public KMean(double ceptra[][]) {
        //Iterate through number of frames
        for (int i = 0; i < ceptra.length; i++) {
            //Iterate through number of mfcc-coeficent
            for (int j = 0; j < ceptra[0].length; j++) {
                tempPoints[i] = new Point(ceptra[i]);
            }
        }
    }



}
