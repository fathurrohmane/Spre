package menu.main;

import classification.Processor;
import classification.hmm.HiddenMarkov;
import data.database.DatabaseHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
    public ProgressBar progressBar;
    public Label textLabelMfccProcessTime;
    public Label textLabelPcaProcessTime;
    public Label textLabelVqProcessTime;
    public Label textLabelHmmProcessTime;
    public TextField textFieldNumberOfState;
    public TextField textFieldNumberOfCentroid;

    private Stage stage;
    private File previousFileAddress = null;
    private File soundDirectory = null;
    private File databaseDirectory = null;
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
        primaryStage.setScene(new Scene(root, 1028, 750));
        primaryStage.setMinWidth(1028);
        primaryStage.setMinHeight(750);
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
        textFieldNumberOfCentroid.focusedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue) {
                        textFieldNumberOfCentroidValidator();
                    }
                }
        );
        textFieldNumberOfState.focusedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue) {
                        textFieldNumberOfStateValidator();
                    }
                }
        );
    }

    /**
     * Handling logic for openSoundDirectoryButton
     */
    public void openSoundFileDirectory() {
        // Open dialog and get the sound directory
        File selectedDirectory = DialogCreator.showDirectoryChooser("Choose Sounds Directory", stage, previousFileAddress);

        // If canceled show error dialog
        if (selectedDirectory == null) {
            DialogCreator.showNormalDialog(stage, "No Folder is Selected");
        } else {
            textFieldSoundDirectory.setText(selectedDirectory.getPath());
            soundDirectory = new File(selectedDirectory.getAbsolutePath());
            previousFileAddress = new File(selectedDirectory.getAbsolutePath());
        }
    }

    /**
     * Handling logic for openDatabaseDirectoryButton
     */
    public void openDatabaseDirectory() {
        // Open dialog and get the sound directory
        File selectedDirectory = DialogCreator.showDirectoryChooser("Choose Sounds Directory", stage, previousFileAddress);

        // If canceled show error dialog
        if (selectedDirectory == null) {
            DialogCreator.showNormalDialog(stage, "No Folder is Selected");
        } else {
            textFieldDatabaseDirectory.setText(selectedDirectory.getPath());
            textLabelDatabaseLocation.setText(selectedDirectory.getName());
            databaseDirectory = new File(selectedDirectory.getAbsolutePath());
            previousFileAddress = new File(selectedDirectory.getAbsolutePath());
            DatabaseHandler.setDatabasePath(databaseDirectory);
            List<HiddenMarkov> wordModels = DatabaseHandler.loadAllWordModelToHMMs(databaseDirectory);
            textAreaTrainedWordList.clear();
            for (int i = 0; i < wordModels.size(); i++) {
                textAreaTrainedWordList.appendText((i + 1) + ". " + wordModels.get(i).getWord() + "\n");
            }

            // Auto parse folder name to checkbox and text field pca dimension reduction
            // check if it has "pca" word in it
            String path = databaseDirectory.getName();
            if (path.contains("pca")) {
                checkBoxReduceDimension.setSelected(true);
                String[] folderName = path.split("pca");
                if (folderName.length != 0) {
                    if (folderName[folderName.length - 1] != null) {
                        try {
                            textFieldDimensionReductionNumber.setText(Integer.valueOf(folderName[folderName.length - 1]).toString());
                        } catch (NumberFormatException e) {
                            DialogCreator.showNormalDialog(stage, "Can't detect PCA dimension!");
                            textFieldDimensionReductionNumber.setText("1");
                        }
                    }
                }
            } else {
                checkBoxReduceDimension.setSelected(false);
            }
        }
    }

    public void beginTesting() {
        if (soundDirectory == null || databaseDirectory == null) {
            DialogCreator.showNormalDialog(stage, "Missing database data or sound data!");
        } else {
            resetBeforeRunning();
            changeButtonAtRunningState(true);
            processor = new Processor(this);
            executor = new Executor();
            executor.start(checkBoxReduceDimension.isSelected(), false);
        }
    }

    public void beginTraining() {
        if (soundDirectory == null || databaseDirectory == null) {
            DialogCreator.showNormalDialog(stage, "Missing database data or sound data!");
        } else {
            resetBeforeRunning();
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
     * Validator for textFieldNumberOfCentroid
     */
    private void textFieldNumberOfCentroidValidator() {
        if (!textFieldNumberOfCentroid.getText().isEmpty()) {
            try {
                int number = Integer.valueOf(textFieldNumberOfCentroid.getText());
                if (!((number & (number - 1)) == 0)) {
                    DialogCreator.showNormalDialog(stage, "Invalid input! Centroid must be the positive power of two!");
                    textFieldNumberOfCentroid.setText("256");
                }
            } catch (NumberFormatException e) {
                DialogCreator.showNormalDialog(stage, "Invalid input! Centroid must be number and its the positive power of two!");
                textFieldNumberOfCentroid.setText("256");
            }
        } else {
            textFieldNumberOfCentroid.setText("256");
        }
    }

    /**
     * Validator for textFieldNumberOfState
     */
    private void textFieldNumberOfStateValidator() {
        if (!textFieldNumberOfState.getText().isEmpty()) {
            try {
                int number = Integer.valueOf(textFieldNumberOfState.getText());
                if (number <= 0) {
                    DialogCreator.showNormalDialog(stage, "Invalid input! The number of state must be greater than 1!");
                }
            } catch (NumberFormatException e) {
                DialogCreator.showNormalDialog(stage, "Invalid input! The number of state must be greater than 1!");
                textFieldNumberOfState.setText("8");
            }
        } else {
            textFieldNumberOfState.setText("8");
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
        textLabelDatabaseLocation.setText("\"No Database is selected\"");
        textLabelRecognitionRate.setText(String.valueOf(0) + " %");
        progressBar.setProgress(0);
        textLabelMfccProcessTime.setText("- ms");
        textLabelPcaProcessTime.setText("- ms");
        textLabelVqProcessTime.setText("- ms");
        textLabelHmmProcessTime.setText("- ms");
        textAreaTrainedWordList.clear();
    }

    /**
     * Reset things before start Training or Testing
     */
    public void resetBeforeRunning() {
        textLabelRecognitionRate.setText(String.valueOf(0) + " %");
        progressBar.setProgress(0);
        textLabelMfccProcessTime.setText("- ms");
        textLabelPcaProcessTime.setText("- ms");
        textLabelVqProcessTime.setText("- ms");
        textLabelHmmProcessTime.setText("- ms");
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
    public void writeProgress(double progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void writeToLabelRecognitionRate(double rate) {
        Platform.runLater(() -> textLabelRecognitionRate.setText(String.valueOf(rate) + " %"));
    }

    @Override
    public void writeToLabelProcessTime(int processType, long time) {
        switch (processType) {
            case ProcessListener.MFCC:
                Platform.runLater(() -> textLabelMfccProcessTime.setText(time + " ms"));
                break;
            case ProcessListener.PCA:
                Platform.runLater(() -> textLabelPcaProcessTime.setText(time + " ms"));
                break;
            case ProcessListener.VQ:
                Platform.runLater(() -> textLabelVqProcessTime.setText(time + " ms"));
                break;
            case ProcessListener.HMM:
                Platform.runLater(() -> textLabelHmmProcessTime.setText(time + " ms"));
                break;
            default:
                throw new IllegalArgumentException("Invalid process type");
        }
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
                    processor.startTrainingWithPCA(
                            Integer.valueOf(textFieldDimensionReductionNumber.getText()),
                            soundDirectory,
                            Integer.valueOf(textFieldNumberOfCentroid.getText()),
                            Integer.valueOf(textFieldNumberOfState.getText())
                    );
                } else {
                    processor.startTestingWithPCA(Integer.valueOf(textFieldDimensionReductionNumber.getText()), soundDirectory, databaseDirectory);
                }
            } else {
                if (isTraining) {
                    processor.startTrainingWithoutPCA(
                            soundDirectory,
                            Integer.valueOf(textFieldNumberOfCentroid.getText()),
                            Integer.valueOf(textFieldNumberOfState.getText())
                    );
                } else {
                    processor.startTestingWithoutPCA(soundDirectory, databaseDirectory);
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
