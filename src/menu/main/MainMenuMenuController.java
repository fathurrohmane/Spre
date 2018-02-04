package menu.main;

import classification.Processor;
import data.database.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tools.MainMenuView;
import tools.ui.DialogCreator;

import java.io.File;

public class MainMenuMenuController extends Application implements MainMenuView {

    public CheckBox checkBoxReduceDimension;
    public TextField textFieldDimensionReductionNumber;
    public Button buttonReset;
    public Button buttonStopProcess;
    public Button buttonTesting;
    public Button buttonTraining;
    public Label textLabelDatabaseLocation;
    public TextArea textAreaTrainedWordList;
    public CheckBox checkBoxShowMFCCLog;
    public CheckBox checkBoxShowPCALog;
    public CheckBox checkBoxShowVQLog;
    public CheckBox checkBoxShowHMMLog;
    public CheckBox checkBoxShowCalculationTime;
    public Label textLabelRecognitionRate;
    public TextField textFieldSoundDirectory;
    public TextField textFieldDatabaseDirectory;
    public Button buttonOpenSoundDirectory;
    public Button buttonOpenDatabaseDirectory;

    private Stage stage;
    private File previousFileAddress = null;
    private File soundFile = null;
    private File databaseFile = null;
    private Processor processor = null;
    private Executor executor;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize fxml
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        stage = primaryStage;

        // Initialize stage
        primaryStage.setTitle("SPEECH RECOGNITION - Fathurrohman Elkusnandi");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }

    /**
     * Initialize widget
     */
    public void initialize() {
        textFieldDimensionReductionNumber.focusedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue) {
                        textFieldDimensionReductionValidator();
                    }
                }
        );
    }

    /**
     * Handling logic for openSoundDirectoryButton
     */
    public void openSoundFileDirectory() {
        // Open dialog and get the sound directory
        soundFile = DialogCreator.showDirectoryChooser("Choose Sounds Directory", stage, previousFileAddress);

        // If canceled show error dialog
        if (soundFile == null) {
            DialogCreator.showNormalDialog(stage, "No Folder is Selected");
            textFieldSoundDirectory.clear();
        } else {
            textFieldSoundDirectory.setText(soundFile.getPath());
        }
    }

    /**
     * Handling logic for openDatabaseDirectoryButton
     */
    public void openDatabaseDirectory() {
        // Open dialog and get the sound directory
        databaseFile = DialogCreator.showDirectoryChooser("Choose Sounds Directory", stage, previousFileAddress);

        // If canceled show error dialog
        if (databaseFile == null) {
            DialogCreator.showNormalDialog(stage, "No Folder is Selected");
            textFieldDatabaseDirectory.clear();
        } else {
            textFieldDatabaseDirectory.setText(databaseFile.getPath());
            DatabaseHandler.setDatabasePath(databaseFile);

        }
    }

    public void beginTesting() {
//        if (soundFile == null && databaseFile == null) {
//            DialogCreator.showNormalDialog(primaryStage, "No File/Folder Selected");
//        } else {
//            if (soundFile.isFile()) {
//                addTextTesting(soundFile.getName());// FIXME: 18-Dec-15
//            } else if (soundFile.isDirectory()) {
//                addTextTesting(soundFile.getName());
//            }
//        }
//
//        processor = new Processor(this);
//        Thread backgroundThread = new Thread(() -> {
//            switch (dialogTestingOption) {
//                case 0:
//                    processor.startTestingWithoutPCA(soundFile, databaseFile);
//                    break;
//                case 1:
//                    processor.startTestingWithPCA(soundFile, databaseFile);
//                    break;
//            }
//            processor = null;
//        });
//        backgroundThread.start();
    }

    public void beginTraining() {
        if (soundFile == null || databaseFile == null) {
            DialogCreator.showNormalDialog(stage, "Missing database data or sound data!");
        } else {
            changeButtonAtRunningState(true);
            processor = new Processor(this);
            executor = new Executor();
            executor.start();
        }
    }

    /**
     * Validator for textFieldDimension
     */
    private void textFieldDimensionReductionValidator() {
        if (!textFieldDimensionReductionNumber.getText().isEmpty()) {
            try {
                int number = Integer.valueOf(textFieldDimensionReductionNumber.getText());
            } catch (NumberFormatException e) {
                DialogCreator.showNormalDialog(stage, "Invalid input! Input only number like 1, 24, 39 etc");
                textFieldDimensionReductionNumber.setText("1");
            }
        } else {
            textFieldDimensionReductionNumber.setText("1");
        }

    }

    /**
     * Set all button to disable when training or testing is running
     *
     * @param isRunning state of the program. Set to true if its running
     */
    private void changeButtonAtRunningState(boolean isRunning) {
        buttonTraining.setDisable(isRunning);
        buttonTesting.setDisable(isRunning);
        checkBoxReduceDimension.setDisable(isRunning);
        textFieldDimensionReductionNumber.setDisable(isRunning);
        checkBoxShowMFCCLog.setDisable(isRunning);
        checkBoxShowPCALog.setDisable(isRunning);
        checkBoxShowVQLog.setDisable(isRunning);
        checkBoxShowHMMLog.setDisable(isRunning);
        buttonReset.setDisable(isRunning);
        checkBoxShowCalculationTime.setDisable(isRunning);
        buttonOpenSoundDirectory.setDisable(isRunning);
        buttonOpenDatabaseDirectory.setDisable(isRunning);
    }

    /**
     * Stop training or testing
     */
    public void stopProgram() {
        executor.stop();
    }

    @Override
    public void writeLog(String context) {

    }

    /**
     * Execute code in different thread
     */
    private class Executor implements Runnable {
        private volatile Thread thread;

        void start() {
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            Thread thisThread = Thread.currentThread();
            while (thread == thisThread) {
                process();
            }
        }

        private void process() {
            if (checkBoxReduceDimension.isSelected()) {
                processor.startTrainingWithPCA(Integer.valueOf(textFieldDimensionReductionNumber.getText()), soundFile);
            } else {
                processor.startTrainingWithoutPCA(soundFile);
            }
            processor = null;
            changeButtonAtRunningState(false);
        }

        void stop() {
            thread = null;
            notify();
        }
    }
}
