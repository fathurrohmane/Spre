package menu.main;

import classification.Processor;
import data.database.DatabaseHandler;
import javafx.application.Application;
import javafx.application.Platform;
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
import tools.MainView;
import tools.ProcessListener;
import tools.ui.DialogCreator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainMenuController extends Application implements MainView {

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
    public TextArea textAreaConsole;

    private Stage stage;
    private File previousFileAddress = null;
    private File soundFile = null;
    private File databaseFile = null;
    private Processor processor = null;
    private Executor executor;
    private SimpleDateFormat simpleDateFormat;

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
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

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
        if (soundFile == null || databaseFile == null) {
            DialogCreator.showNormalDialog(stage, "Missing database data or sound data!");
        } else {
            changeButtonAtRunningState(true);
            processor = new Processor(this);
            executor = new Executor();
            executor.start(checkBoxReduceDimension.isSelected(), false);
        }
    }

    public void beginTraining() {
        if (soundFile == null || databaseFile == null) {
            DialogCreator.showNormalDialog(stage, "Missing database data or sound data!");
        } else {
            changeButtonAtRunningState(true);
            processor = new Processor(this);
            executor = new Executor();
            executor.start(checkBoxReduceDimension.isSelected(), true);
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

    /**
     * Reset TextAreaConsole
     */
    public void resetProgram() {
        textAreaConsole.clear();
        textLabelRecognitionRate.setText(String.valueOf(0) + " %");
    }

    @Override
    public void writeToTextAreaConsole(int processType, String input) {
        Platform.runLater(() -> {
                    if (writeLogValidation(processType)) {
                        Date now = new Date();
                        textAreaConsole.appendText(simpleDateFormat.format(now) + " : " + input + "\n");
                    }
                }
        );
    }

    @Override
    public void writeToTextAreaTrainedWordList(List<String> words) {

    }

    @Override
    public void writeProgress(int progress) {

    }

    @Override
    public void writeToLabelRecognitionRate(double rate) {
        Platform.runLater(() -> textLabelRecognitionRate.setText(String.valueOf(rate) + " %"));
    }

    private boolean writeLogValidation(int processType) {
        switch (processType) {
            case ProcessListener.BASIC:
                return true;
            case ProcessListener.MFCC:
                return checkBoxShowMFCCLog.isSelected();
            case ProcessListener.PCA:
                return checkBoxShowPCALog.isSelected();
            case ProcessListener.VQ:
                return checkBoxShowVQLog.isSelected();
            case ProcessListener.HMM:
                return checkBoxShowHMMLog.isSelected();
            case ProcessListener.TIMESTAMP:
                return checkBoxShowCalculationTime.isSelected();
            default:
                throw new IllegalArgumentException("Invalid process type");
        }
    }

    /**
     * Execute code in different thread
     */
    private class Executor implements Runnable {
        private volatile Thread thread;
        private boolean isReduceDimension;
        private boolean isTraining;

        void start(boolean isReduceDimension, boolean isTraining) {
            this.isReduceDimension = isReduceDimension;
            this.isTraining = isTraining;

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
            if (isReduceDimension) {
                if (isTraining) {
                    processor.startTrainingWithPCA(Integer.valueOf(textFieldDimensionReductionNumber.getText()), soundFile);
                } else {
                    processor.startTestingWithPCA(Integer.valueOf(textFieldDimensionReductionNumber.getText()), soundFile, databaseFile);
                }
            } else {
                if (isTraining) {
                    processor.startTrainingWithoutPCA(soundFile);
                } else {
                    processor.startTestingWithoutPCA(soundFile, databaseFile);
                }
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
