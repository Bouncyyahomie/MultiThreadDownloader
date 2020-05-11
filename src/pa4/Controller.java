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

    private DownloadTask downloadTask;
    private String directory = "";
    //    private Thread runningThread = null; For just one thread
    private long length = 0;
    private URL url;
    private ExecutorService executor;
    private int threadsWalking;
    private long updateText;
    private long upValue;

    @FXML
    private Label urltdLabel;

    @FXML
    private TextField urlInputField;

    @FXML
    private Button downloadButton;

    @FXML
    private Button clearButton;

    @FXML
    private Label fileNameLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label threadLabel;

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button pauseButton;

    @FXML
    private Button resumeButton;

    @FXML
    private Label fileSize;

    @FXML
    private ProgressBar threadWalker01;

    @FXML
    private ProgressBar threadWalker02;

    @FXML
    private ProgressBar threadWalker03;

    @FXML
    private ProgressBar threadWalker04;

    @FXML
    private ProgressBar threadWalker05;

    @FXML
    private ProgressBar threadWalker06;

    private ProgressBar[] walker;

    /*
     * Initialize button in GUI
     * */
    @FXML
    public void initialize() {
        walker = new ProgressBar[]{threadWalker01, threadWalker02, threadWalker03,
                threadWalker04, threadWalker05, threadWalker06};
        downloadButton.setOnAction(new downloadHandler());
        clearButton.setOnAction(event -> {
            for (int i = 0; i < threadsWalking; i++) {
                walker[i].setVisible(false);
            }
            urlInputField.clear();
            progressBar.setVisible(false);
            fileNameLabel.setVisible(false);
            cancelButton.setVisible(false);
            pauseButton.setVisible(false);
            resumeButton.setVisible(false);
        });
        cancelButton.setOnAction(event -> {
            //TO DO set executor cancel
//            runningThread.interrupt();
            progressBar.setVisible(false);
            fileNameLabel.setVisible(false);
            cancelButton.setVisible(false);
            pauseButton.setVisible(false);
            resumeButton.setVisible(false);
        });
    }

    /*
     * For download with multi thread
     *
     * */
    public void multiTaskDownload() {
        long sizeOfKB = 4096; //The part size is multiples of 4KB
        long chunk = (long) Math.ceil((length / (sizeOfKB*5))); //Create chunk
        threadsWalking = 6; //Create worker
        //Size of each thread worker multiplied by 5 because use 5 threads to separate and the 6th threads do the fraction.
        long sizeOfEach = ((chunk / threadsWalking) * (sizeOfKB*5));
        executor = Executors.newFixedThreadPool(6); //Create executor for get number of thread
        DownloadTask[] tasks = new DownloadTask[threadsWalking];
        for (int i = 0; i < threadsWalking; i++) {
            walker[i].setVisible(true); //Show in ui
            //For thread 1-5
            tasks[i] = new DownloadTask(url,
                    createFile(directory + "\\" + getFileName(urlInputField.getText())),
                    sizeOfEach * i, sizeOfEach);
            //For last threads that has fraction
            if (i == threadsWalking - 1) {
                tasks[i] = new DownloadTask(url,
                        createFile(directory + "\\" + getFileName(urlInputField.getText())),
                        sizeOfEach * i, length - (sizeOfEach * i));
            }
            tasks[i].valueProperty().addListener(this::getBytes);
            executor.execute(tasks[i]); //For start each thread
            walker[i].progressProperty().bind(tasks[i].progressProperty()); //Show progress in ui
        }
        progressBar.progressProperty().bind(tasks[0].progressProperty().multiply(1.0 / 6.0).
                add(tasks[1].progressProperty().multiply(1.0 / 6.0)
                        .add(tasks[2].progressProperty().multiply(1.0 / 6.0)
                                .add(tasks[3].progressProperty().multiply(1.0 / 6.0)
                                        .add(tasks[4].progressProperty().multiply(0.2)    //)))));
                                                .add(tasks[5].progressProperty().multiply(1.0 / 6.0)))))));
    }

    private void getBytes(Observable observable, Long oldValue, long newValue) {
//        System.out.printf("old" + String.valueOf(oldValue) + "\n");
        if (oldValue == null) {
            upValue = newValue;
            updateText += upValue;
        } else {
            upValue = newValue - oldValue;
            updateText += upValue;
        }
        fileSize.setText(String.format("%d / %d", updateText, length));
    }

    public String setDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser(); //Will change to File chooser because is better
        //When didnt set initial directory
        if (!directory.isEmpty()) {
            directoryChooser.setInitialDirectory(new File(directory));
        }
        //POP-UP for choose directory
        Stage stage = (Stage) anchor.getScene().getWindow();
        File selectDirectory = directoryChooser.showDialog(stage);
        directory = selectDirectory.getAbsolutePath();
        return directory;
    }

    /*
     * Download part
     * */
    class downloadHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            urlInputField.setStyle("-fx-border-color:transparent");
            try {
                if (urlInputField.getText().isEmpty()) {
                    urlInputField.setText("Please input your url ");
                    urlInputField.setStyle("-fx-border-color: red");
                } else {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialFileName(getFileName(urlInputField.getText()));
                    //When didnt set initial directory
                    if (!directory.isEmpty()) {
                        fileChooser.setInitialDirectory(new File(directory));
                    }
                    //POP-UP for choose directory
                    Stage stage = (Stage) anchor.getScene().getWindow();
                    File selectDirectory = fileChooser.showOpenDialog(stage);
                    directory = selectDirectory.getAbsolutePath();
                    //Get url from input and change from string to URL
                    url = new URL(urlInputField.getText());
                    URLConnection connection = url.openConnection();
                    //Get length of file
                    length = connection.getContentLengthLong();
                    //Test download for one thread
//                    downloadTask = (new DownloadTask(url,
//                            createFile(directory + "\\" + getFileName(urlInputField.getText())), 0, length));
                    //Show on GUI
                    progressBar.setVisible(true);
                    fileNameLabel.setVisible(true);
                    cancelButton.setVisible(true);
                    pauseButton.setVisible(true);
//                    progressBar.progressProperty().bind(downloadTask.progressProperty());
                }
            } catch (IOException ioe) {

            } catch (NullPointerException npe) {

            }
            fileNameLabel.setText(getFileName(urlInputField.getText()));
            multiTaskDownload();
            //one thread Test
//            runningThread = new Thread(downloadTask);
//            runningThread.start();
        }
    }


}
