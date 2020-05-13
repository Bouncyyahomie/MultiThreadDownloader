package FlashGet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/*
* Initialize JavaFX and set action
* @author Jakkrathorn Srisawd
* */
public class Controller {

    protected DownloadTask downloadTask;
    protected File directory = null;
    protected long length = 0;
    protected URL url;
    protected ExecutorService executor;
    protected int threadsWalking = 6; //Create worker
    protected long updateText;
    protected File selectDirectory;
    protected MultiDownloadTask mdt; //Initialize MultiDownloadTask

    @FXML
    //Get url from textFiled
    protected TextField urlInputField;

    @FXML
    //Download Button
    private Button downloadButton;

    @FXML
    //Clear Button
    private Button clearButton;

    @FXML
    //Show File name
    protected Label fileNameLabel;

    @FXML
    //Cancel Button
    protected Button cancelButton;

    @FXML
    //Main Progressbar
    protected ProgressBar progressBar;

    @FXML
    //Split pane with AnchorPane
    protected AnchorPane anchor;

    @FXML
    //Show file size
    protected Label fileSize;

    @FXML
    //ProgressBar for thread 1
    protected ProgressBar threadWalker01;

    @FXML
    //ProgressBar for thread 2
    protected ProgressBar threadWalker02;

    @FXML
    //ProgressBar for thread 3
    protected ProgressBar threadWalker03;

    @FXML
    //ProgressBar for thread 4
    protected ProgressBar threadWalker04;

    @FXML
    //ProgressBar for thread 5
    protected ProgressBar threadWalker05;

    @FXML
    //ProgressBar for thread 6
    protected ProgressBar threadWalker06;

    //Array of all thread progressbar
    public ProgressBar[] walker;

    /*
     * Initialize the JavaFX
     * */
    public void initialize() {
        //Initialize ProgressBar for threadWalker
        walker = new ProgressBar[]{threadWalker01, threadWalker02, threadWalker03,
                threadWalker04, threadWalker05, threadWalker06};
        //Initialize event of downloadButton
        downloadButton.setOnAction(new DownloadHandler());
        //Initialize event of clearButton
        clearButton.setOnAction(this::clearHandler);
        //Initialize event of cancelButton
        cancelButton.setOnAction(event -> {
            mdt.cancelTasks();
            progressBar.setVisible(false);
            fileNameLabel.setVisible(false);
            cancelButton.setVisible(false);
            fileSize.setVisible(false);
            for (int i = 0; i < threadsWalking; i++) {
                walker[i].setVisible(false);
            }
            mdt.clearTask();
        });
    }

    /*
    * For clear all field
    * */
    public void clearHandler(ActionEvent event) {
        try {
            Boolean clear = false;
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Confirm");
            alert.setTitle("Clear");
            alert.setHeaderText("Do you want to clear???");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) clear = true;
            if (clear = true) {
                for (int i = 0; i < threadsWalking; i++) {
                    walker[i].setVisible(false);
                }
                urlInputField.clear();
                progressBar.setVisible(false);
                fileNameLabel.setVisible(false);
                cancelButton.setVisible(false);
                mdt.clearTask();
                fileSize.setVisible(false);
            }
        } catch (NullPointerException npe) {
            Alert alertw = new Alert(AlertType.WARNING);
            alertw.setHeaderText("Can not clear because nothing to clear");
            alertw.setTitle("Error");
            alertw.showAndWait();
        }
    }


/**
 * Download Handler event
 */
class DownloadHandler implements EventHandler<ActionEvent> {

    public void handle(ActionEvent event) {
        //Get file namer
        GetFileName getter = new GetFileName();
        //Set text field with transparent
        urlInputField.setStyle("-fx-border-color:transparent");
        try {
            //Check if text field is empty will change text field to red and show message
            if (urlInputField.getText().isEmpty()) {
                urlInputField.setPromptText("Please input your url ");
                urlInputField.setStyle("-fx-border-color: red");
            } else {
                //Choose directory of File and st name.
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(getter.get(urlInputField.getText()));
                //When didnt set initial directory
                if (directory != null) {
                    fileChooser.setInitialDirectory((directory));
                }
                String fileType = urlInputField.getText().substring(urlInputField.getText().lastIndexOf("."));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*" + fileType));
                //POP-UP for choose directory
                Stage stage = (Stage) anchor.getScene().getWindow();
                selectDirectory = fileChooser.showSaveDialog(stage);
                directory = selectDirectory.getParentFile();
                //Get url from input and change from string to URL
                url = new URL(urlInputField.getText());
                URLConnection connection = url.openConnection();
                //Get length of file
                length = connection.getContentLengthLong();
                //Show on GUI
                progressBar.setVisible(true);
                fileNameLabel.setVisible(true);
                cancelButton.setVisible(true);
            }
        } catch (IOException ioe) {
            Alert alertioe = new Alert(AlertType.WARNING);
            alertioe.setHeaderText("Can not clear because nothing to clear");
            alertioe.setTitle("Error");
            alertioe.showAndWait();
        } catch (NullPointerException npe) {
            Alert alertnpe = new Alert(AlertType.WARNING);
            alertnpe.setHeaderText("Please choose file directory");
            alertnpe.setTitle("EROR:::Did not choose");
            alertnpe.showAndWait();
        }

        if (!urlInputField.getText().isEmpty()) {
            //Show file name in label
            fileNameLabel.setText(String.valueOf(getter.get(urlInputField.getText())));
            //Initialize MultiDownloadTask
            mdt = new MultiDownloadTask();
            //Start download with multithread
            mdt.multiTaskDownload(walker, progressBar, url, length, selectDirectory);
            //Get Label for show size of file
            mdt.labelGetter(fileSize);
        }
    }
}


}
