package pa4;

import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    protected DownloadTask downloadTask;
    protected File directory = null;
    //    private Thread runningThread = null; For just one thread
    protected long length = 0;
    protected URL url;
    protected ExecutorService executor;
    protected int threadsWalking = 6; //Create worker
    protected long updateText;
    protected long upValue;
    protected File selectDirectory;
    protected MultiDownloadTask mdt;

    @FXML
    protected TextField urlInputField;

    @FXML
    private Button downloadButton;

    @FXML
    private Button clearButton;

    @FXML
    protected Label fileNameLabel;

    @FXML
    protected Button cancelButton;

    @FXML
    protected ProgressBar progressBar;

    @FXML
    protected AnchorPane anchor;


    @FXML
    protected Label fileSize;

    @FXML
    protected ProgressBar threadWalker01;

    @FXML
    protected ProgressBar threadWalker02;

    @FXML
    protected ProgressBar threadWalker03;

    @FXML
    protected ProgressBar threadWalker04;

    @FXML
    protected ProgressBar threadWalker05;

    @FXML
    protected ProgressBar threadWalker06;

    public ProgressBar[] walker;

    /*
     * Initialize button in GUI
     * */
    @FXML
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
        clearButton.setOnAction(event -> {
            for (int i = 0; i < threadsWalking; i++) {
                walker[i].setVisible(false);
            }
            urlInputField.clear();
            progressBar.setVisible(false);
            fileNameLabel.setVisible(false);
            cancelButton.setVisible(false);
            mdt.clearTask();
            fileSize.setVisible(false);
        });
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
                    urlInputField.setText("Please input your url ");
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
                System.exit(1);
            } catch (NullPointerException npe) {
                System.out.println("Program is Exit");
                System.exit(1);
            }
            //Show file name in label
            fileNameLabel.setText(String.valueOf(getter.get(urlInputField.getText())));
            //Initialize MultiDownloadTask
            mdt = new MultiDownloadTask();
            //Start download with multithread
            mdt.multiTaskDownload(walker, progressBar, url, length, selectDirectory);
            mdt.labelGetter(fileSize);

        }
    }


}
