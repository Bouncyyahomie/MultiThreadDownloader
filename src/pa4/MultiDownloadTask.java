package pa4;

import java.util.concurrent.Executors;

/*
* For download with multi thread task
* @author Jakkrathorn Srisawad
* */
public class MultiDownloadTask {

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
}
