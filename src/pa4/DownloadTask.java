package pa4;

import javafx.concurrent.Task;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/***
 * For downlaod file from URL
 * @author Jakkrathorn Srisawad
 */
public class DownloadTask extends Task<Long> {
    private URL url;
    private File outFile;
    private long start;
    private long size;

    public DownloadTask(URL url, File outFile, long start, long size) {
        this.url = url;
        this.outFile = outFile;
        this.size = size;
        this.start = start;
    }

    @Override
    public Long call() {
        long bytesRead = 0;
        InputStream in =null;
        RandomAccessFile writer = null;
        try {
            long length = 0;
            final int BUFFERSIZE = 16 * 1024;
            byte[] buffer = new byte[BUFFERSIZE];
            String range = null;
            URLConnection connection = url.openConnection();
            if(size > 0){
                range = String.format("bytes=%d-%d",start,start+size-1);
            }
            else {
                //size not given, so read from start byte to end of line
                range = String.format("bytes=%d-",start);
            }
            connection.setRequestProperty("Range",range);
            in = connection.getInputStream();
            //Create a random access file for synchronus output ("rwd" flags)
            writer = new RandomAccessFile(outFile, "rwd");
            //seek to location (in bytes) to start writing to
            writer.seek(start);
            length = connection.getContentLengthLong();
            do {
                int n = in.read(buffer);
                writer.write(buffer, 0, n);
                bytesRead += n;
                updateProgress(bytesRead, length);
                updateValue(bytesRead);
                if (n < 0) break;
            } while (bytesRead<size);
        } catch (MalformedURLException ex) {
            //URL constructor may throw this
            System.exit(1);
        } catch (IOException ioe) {
            //getContentLengthLong may throw IOException
            System.exit(1);
        }finally {
            try{
            in.close();
            writer.close();
        }catch (IOException ioe){

            }
        }
        return bytesRead;
    }
}
