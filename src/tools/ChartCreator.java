package tools;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class ChartCreator {

    public static void loadData(LineChart lineChart, double[] audioData) {

        XYChart.Series series = new XYChart.Series<String, Number>();

        for (int i = 0; i < 500; i++) {
            series.getData().add(new XYChart.Data<String, Number>(i+"", audioData[i]));
        }
        lineChart.getData().add(series);

    }

    public static void writeToTXT(double[] audioData) {

        try {

            File file = new File("test.txt");

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <10; i++) {
                bw.write(sb.append(new BigDecimal(audioData[i]).toPlainString()).toString());
                bw.write(sb.append('\n').toString());
            }
            bw.close();

            if(!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
