package tools.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * Created by Fathurrohman on 18-Dec-15.
 * Class to handle dialog
 */
public class DialogCreator {

    public static final String SINGLE_RESPONE = "Train without PCA?";
    public static final String FOLDER_RESPONE = "Train with PCA?";
    public static final String CANCEL_RESPONE = "Dialog.Actions.CANCEL";

    public static void showNormalDialog(Stage stage, String information) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information ");
        alert.setHeaderText(null);
        alert.setContentText(information);

        alert.showAndWait();
    }

    public static String showChoicesTestingDialog(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog with Custom Actions");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne = new ButtonType(SINGLE_RESPONE);
        ButtonType buttonTypeTwo = new ButtonType(FOLDER_RESPONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeOne){
                return SINGLE_RESPONE;
            } else if (result.get() == buttonTypeTwo) {
                return FOLDER_RESPONE;
            } else {
                return "error";
            }
        } else {
            return "error";
        }
}

    public static File showFileChooser(Stage primaryStage, File previousFileAddress) {
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

        return previousFileAddress;
    }

    public static File showDirectoryChooser(String message, Stage primaryStage, File previousFileAddress) {
        // Open file dialog
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(message);

        // Set file dialog to previous direcrory
        if(previousFileAddress != null) {
            directoryChooser.setInitialDirectory(previousFileAddress.getParentFile());
        }
        // Get file
        previousFileAddress = directoryChooser.showDialog(primaryStage);

        return previousFileAddress;
    }

}
