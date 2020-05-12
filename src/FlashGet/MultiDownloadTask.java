package FlashGet;

import javafx.beans.Observable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;

/*
 * For download with multi thread task
 * @author Jakkrathorn Srisawad
 * */
public class MultiDownloadTask extends Controller {
    private Label label;
    private long getLength;
    public DownloadTask[] tasks = new DownloadTask[threadsWalking];


    public void multiTaskDownload(ProgressBar[] walker, ProgressBar progressBar, URL url, long length, File selectDirectory) {
        getLength = length;
        GetFileName getter = new GetFileName();
        long sizeOfKB = 4096; //The part size is multiples of 4KB
        long chunk = (long) Math.ceil((length / (sizeOfKB * 5))); //Create chunk
        //Size of each thread worker multiplied by 5 because use 5 threads to separate and the 6th threads do the fraction.
        long sizeOfEach = ((chunk / threadsWalking) * (sizeOfKB * 5));
        executor = Executors.newFixedThreadPool(6); //Create executor for get number of thread
        for (int i = 0; i < threadsWalking; i++) {
            walker[i].setVisible(true); //Show in ui
            //For thread 1-5
            tasks[i] = new DownloadTask(url, selectDirectory,
                    sizeOfEach * i, sizeOfEach);
            //For last threads that has fraction
            if (i == threadsWalking - 1) {
                tasks[i] = new DownloadTask(url,
                        selectDirectory,
                        sizeOfEach * i, length - (sizeOfEach * i));
            }
            //add progress tasks to listener
            tasks[i].valueProperty().addListener(this::getBytes);
            //execute tasks
            executor.execute(tasks[i]); //For start each thread
            //show progress in each progress bar
            walker[i].progressProperty().bind(tasks[i].progressProperty()); //Show progress in ui
        }
        //Set main progress bar will combind with all thread progressbar
        progressBar.progressProperty().bind(tasks[0].progressProperty().multiply(1.0 / 6.0).
                add(tasks[1].progressProperty().multiply(1.0 / 6.0)
                        .add(tasks[2].progressProperty().multiply(1.0 / 6.0)
                                .add(tasks[3].progressProperty().multiply(1.0 / 6.0)
                                        .add(tasks[4].progressProperty().multiply(0.2)    //)))));
                                                .add(tasks[5].progressProperty().multiply(1.0 / 6.0)))))));
        executor.shutdown();
    }

    /*
     * Get Label from controller
     * */
    public void labelGetter(Label getLabel) {
        label = getLabel;
    }

    /*
     * Method for Listener with observer
     *
     * */
    public void getBytes(Observable observable, Long oldValue, long newValue) {
        long upValue;
        if (oldValue == null) {
            upValue = newValue;
            updateText += upValue;
        } else {
            upValue = newValue - oldValue;
            updateText += upValue;
        }
        if (updateText == getLength) {
            label.setText("STATUS : FINISHED");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Done");
            alert.setHeaderText("Download Finished");
            alert.setContentText("your download is finished!!!!");
            alert.showAndWait();
        } else {
            if (getLength < 10e3) label.setText(String.format("%.2f Bytes / %.2f Bytes", updateText, getLength));
            else if (getLength <= 10e3) label.setText(String.format("%.2f KB / %.2f KB", updateText/10e3, getLength/10e3));
            else if (getLength <= 10e6)label.setText(String.format("%.2f MB / %.2f MB", updateText/10e6, getLength / 10e6));
            else label.setText(String.format("%.2f GB / %.2f GB", updateText/10e9, getLength / 10e9));
        }
    }

    /*
     * For cancel all tasks
     * */
    public void cancelTasks() {
        Boolean clear = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Confirm");
        alert.setTitle("Cancel");
        alert.setHeaderText("Do you want to cancel???");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) clear = true;
        if (clear = true) {
            label.setText("STATUS : CANCELLED");
            for (DownloadTask task : tasks) {
                task.cancel();
            }
        }
    }

    public void clearTask() {
        Arrays.fill(tasks, null);
        label.setText("STATUS : CLEAR ");
    }

}
