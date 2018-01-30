package test;

import data.vectorquantization.LBG.Point;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PointTest {

    private double[][] observation;
    private double[] result;
    private List<Point> pointList;

    @org.junit.Before
    public void setUp() throws Exception {
        observation = new double[2][5];
        observation[0][0] = 0;
        observation[0][1] = 1;
        observation[0][2] = 1;
        observation[0][3] = 0;
        observation[0][4] = 2;

        observation[1][0] = 1;
        observation[1][1] = 1;
        observation[1][2] = 0;
        observation[1][3] = 1;
        observation[1][4] = 1;

        result = new double[]{0, 1, 1, 0, 2, 1, 1, 0, 1, 1};

        pointList = new ArrayList<>();
        double[] result1 = new double[]{0, 1};
        pointList.add(new Point(result));
        pointList.add(new Point(result1));
    }

    @org.junit.Test
    public void add() {
        Point p = new Point();
        p.add(observation[0]);
        p.add(observation[1]);

        Assert.assertArrayEquals(p.getCoordinates(), result, 0);
    }

    @Test
    public void normalizeDimensionSize() {
        int biggestDimension = Point.normalizeDimensionSize(pointList);

        for (Point point : pointList) {
            Assert.assertEquals(point.getDimension(), biggestDimension);
        }

    }
}