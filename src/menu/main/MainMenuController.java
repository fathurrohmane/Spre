package menu.main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tools.IMainView;

import java.awt.*;

public class MainMenuController extends Application implements IMainView {

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

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize fxml
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        stage = primaryStage;

        // Initialize stage
        primaryStage.setTitle("SPEECH RECOGNITION - Fathurrohman Elkusnandi");
        primaryStage.setScene(new Scene(root, 1000, 550));
        primaryStage.show();
    }

    @Override
    public void writeLog(String context) {

    }
}
