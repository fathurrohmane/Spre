package tools.ui;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.DialogAction;
import org.controlsfx.dialog.Dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fathurrohman on 18-Dec-15.
 * Class to handle dialog
 */
public class DialogCreator {

    public static final String SINGLE_RESPONE = "Open Single File?";
    public static final String FOLDER_RESPONE = "Open Folder for multiple files?";
    public static final String CANCEL_RESPONE = "Dialog.Actions.CANCEL";

    public static void showNormalDialog(Stage stage, String information) {
        Dialogs.create()
                .owner(stage)
                .title("Information")
                .masthead(null)
                .message(information)
                .showInformation();
    }

    public static String showChoicesTestingDialog(Stage stage) {

        List<DialogAction> links = new ArrayList<DialogAction>();
        links.add(new DialogAction(SINGLE_RESPONE));
        links.add(new DialogAction(FOLDER_RESPONE));


        Action response = Dialogs.create()
                .owner(stage)
                .title("Command Link Dialog")
                .masthead(null)
                .message("Choose Single File or Multiple File?")
                .showCommandLinks(links);

        return response.getText();
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

    public static File showDirectoryChooser(Stage primaryStage, File previousFileAddress) {
        // Open file dialog
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Audio File");

        // Set file dialog to previous direcrory
        if(previousFileAddress != null) {
            directoryChooser.setInitialDirectory(previousFileAddress.getParentFile());
        }
        // Get file
        previousFileAddress = directoryChooser.showDialog(primaryStage);

        return previousFileAddress;
    }

}
