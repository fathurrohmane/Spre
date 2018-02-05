package menu;

import classification.Processor;
import data.database.DatabaseHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tools.MainMenuView;
import tools.MainView;
import tools.ui.DialogCreator;
import tools.Time;

import java.io.File;
import java.util.List;

/**
 * Created by Fathurrohman on 5/20/2015.
 */
public class MainMenuMenuController extends Application implements MainView {
    // UI Variable
    Parent root;
    Stage primaryStage;

    @FXML
    LineChart<Number, Number> audioWaveChart;
    /**
     * Testing UI Variable
     */
    @FXML
    TextArea text_area_testing;

    /**
     * Training UI Variable
     */
    @FXML
    TextArea text_area_training;
    @FXML
    Label label_trained_word;
    @FXML
    Label label_number_ceptra;
    @FXML
    Label label_number_filter;
    @FXML
    Label label_number_pca;
    @FXML
    Label label_number_clusters;
    @FXML
    Label label_number_states;
    @FXML
    TextField text_field_save_file_location;

    @FXML
    TextField text_field_training_file_location;

    // Class Variable
    private File previousFileAddress = null;
    private File soundFile = null;
    private File databaseFile = null;
    private Processor processor = null;

    private int dialogTestingOption;
    private int dialogTrainingOption;

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
        //Training training = new Training("LOL", 4, previousFileAddress);// FIXME: 18-Dec-15

        //Load audio data to chart
        //ChartCreator.loadData(audioWaveChart,AudioExtractor.getAudioData(previousFileAddress));
        //ChartCreator.writeToTXT(AudioExtractor.getAudioData(previousFileAddress));

    }

    public void openFileTestingClicked() {
        // Choose from dialog

        String respond = DialogCreator.showChoicesTestingDialog(primaryStage);

        switch (respond) {
            case DialogCreator.SINGLE_RESPONE:
                soundFile = DialogCreator.showDirectoryChooser("Open Audio File", primaryStage, previousFileAddress);
                dialogTestingOption = 0;
                break;
            case DialogCreator.FOLDER_RESPONE:
                soundFile = DialogCreator.showDirectoryChooser("Open Audio File", primaryStage, previousFileAddress);
                dialogTestingOption = 1;
                break;
            default:
                soundFile = null;
                break;
        }

        if (soundFile == null) {
            DialogCreator.showNormalDialog(primaryStage, "No File/Folder Selected");
            text_field_training_file_location.setText("");
        } else {
            // TODO: 23-Dec-15 write list training file to text area
            text_field_training_file_location.setText(soundFile.getPath());
        }
    }

    public void openFileTrainClicked() {
        // Choose from dialog
        String respond = DialogCreator.showChoicesTestingDialog(primaryStage);
        soundFile = null;

        if (respond.equals(DialogCreator.SINGLE_RESPONE)) {
            soundFile = DialogCreator.showDirectoryChooser("Choose directory of all the training sound files", primaryStage, previousFileAddress);
            dialogTrainingOption = 0;
        } else if (respond.equals(DialogCreator.FOLDER_RESPONE)) {
            soundFile = DialogCreator.showDirectoryChooser("Choose directory of all the training sound files", primaryStage, previousFileAddress);
            dialogTrainingOption = 1;
        }

        if (soundFile == null) {
            DialogCreator.showNormalDialog(primaryStage, "No File/Folder Selected");
            text_field_training_file_location.setText("");
        } else {
            // TODO: 23-Dec-15 write list training file to text area
            text_field_training_file_location.setText(soundFile.getPath());
        }
    }

    public void topmenuAboutClicked() {
        DialogCreator.showNormalDialog(primaryStage, "Created by Fathurrohman Elkusnandi - 2015");
    }

    /**
     * Function training button clicked
     */

    public void trainingButtonClicked() {
        if (soundFile == null) {
            DialogCreator.showNormalDialog(primaryStage, "No File/Folder Selected");
        } else {
            processor = new Processor(this);
            Thread backgroundThread = new Thread(() -> {
                switch (dialogTrainingOption) {
                    case 0:
                        processor.startTrainingWithoutPCA(soundFile);
                        break;
                    case 1:
                        processor.startTrainingWithPCA(24, soundFile);
                        break;
                }
                processor = null;
            });
            backgroundThread.start();
        }
    }

    public void openFileDatabaseClicked() {
        databaseFile = DialogCreator.showDirectoryChooser("Open Database directory contains subfolder codebook and Word Model Files", primaryStage, previousFileAddress);

    }

    public void runTheTest() {
        if (soundFile == null && databaseFile == null) {
            DialogCreator.showNormalDialog(primaryStage, "No File/Folder Selected");
        } else {
            if (soundFile.isFile()) {
                addTextTesting(soundFile.getName());// FIXME: 18-Dec-15
            } else if (soundFile.isDirectory()) {
                addTextTesting(soundFile.getName());
            }
        }

        processor = new Processor(this);
        Thread backgroundThread = new Thread(() -> {
            switch (dialogTestingOption) {
                case 0:
                    processor.startTestingWithoutPCA(soundFile, databaseFile);
                    break;
                case 1:
                    processor.startTestingWithPCA(24, soundFile, databaseFile);
                    break;
            }
            processor = null;
        });
        backgroundThread.start();

    }

    public void saveToButtonClicked() {
        databaseFile = DialogCreator.showDirectoryChooser("Choose directory of all the training sound files", primaryStage, previousFileAddress);
        DatabaseHandler.setDatabasePath(databaseFile);
        text_field_save_file_location.setText(databaseFile.getAbsolutePath());
    }

    private void addTextTesting(String text) {
        text_area_testing.appendText(Time.getTime() + text + "\n");
    }

    public void clearTestTextArea() {
        text_area_testing.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void writeToTextAreaConsole(int processType, String input) {
        Platform.runLater(() -> addTextTesting(input));
    }

    @Override
    public void writeToTextAreaTrainedWordList(List<String> words) {

    }

    @Override
    public void writeProgress(double progress) {

    }

    @Override
    public void writeToLabelRecognitionRate(double rate) {

    }
}
