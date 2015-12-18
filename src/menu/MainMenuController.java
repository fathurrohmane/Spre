package menu;

import audio.AudioProcessing;
import classification.Training;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import tools.DialogCreator;
import tools.Time;

import java.io.File;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class MainMenuController extends Application {
    // UI Variable
    Parent root;
    Stage primaryStage;
    @FXML
    LineChart<Number, Number> audioWaveChart;
    @FXML
    TextArea textAreaTesting;

    // Class Variable
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

        previousFileAddress = DialogCreator.showFileChooser(primaryStage, previousFileAddress);

        // Begin Training data
        Training training = new Training("LOL", 4, previousFileAddress);// FIXME: 18-Dec-15

        //Load audio data to chart
        //ChartCreator.loadData(audioWaveChart,AudioExtractor.getAudioData(previousFileAddress));
        //ChartCreator.writeToTXT(AudioExtractor.getAudioData(previousFileAddress));

    }

    public void openFileTestingClicked() {
        // Choose from dialog

        String respone = DialogCreator.showChoicesTestingDialog(primaryStage);
        File soundFile = null;

        if (respone.equals(DialogCreator.SINGLE_RESPONE)) {
            soundFile = DialogCreator.showFileChooser(primaryStage, previousFileAddress);
        } else if (respone.equals(DialogCreator.FOLDER_RESPONE)) {
            soundFile = DialogCreator.showDirectoryChooser(primaryStage, previousFileAddress);
        }

        if (soundFile == null) {
            DialogCreator.showNormalDialog(primaryStage, "No File/Folder Selected");
        } else {
            if (soundFile.isFile()) {
                addText(soundFile.getName());// FIXME: 18-Dec-15
            } else if (soundFile.isDirectory()) {
                addText(soundFile.getName());
            }
        }
    }

    private void addText(String text) {
        textAreaTesting.appendText(Time.getTime() + text + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
