package menu;

import audio.AudioProcessing;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tools.ArrayWriter;
import tools.WaveData;

import java.io.File;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class MainMenuController extends Application {
    //UI Variable
    Parent root;
    Stage primaryStage;
    @FXML
    LineChart<Number, Number> audioWaveChart;

    File previousFileAddress = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        this.primaryStage = primaryStage;
        primaryStage.setTitle("SPEECH RECOGNITION - Fathurrohman Elkusnandi");
        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.show();



    }

    public void topmenuFileOpenClicked() {

        // Open file dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Audio File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Wav Files", "*.wav")
        );
        // Set file dialog to previous direcrory
        if(previousFileAddress != null) {
            fileChooser.setInitialDirectory(previousFileAddress.getParentFile());
        }
        // Get file
        previousFileAddress = fileChooser.showOpenDialog(primaryStage);

        //Process data
        AudioProcessing audioProcessing = new AudioProcessing(previousFileAddress);

        //Load audio data to chart
        //ChartCreator.loadData(audioWaveChart,AudioExtractor.getAudioData(previousFileAddress));
        //ChartCreator.writeToTXT(AudioExtractor.getAudioData(previousFileAddress));

    }

    public static void main(String[] args) {
        launch(args);
    }
}
