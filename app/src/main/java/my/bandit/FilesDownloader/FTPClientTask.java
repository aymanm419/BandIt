package my.bandit.FilesDownloader;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FTPClientTask {
    private FTPClient client;

    public FTPClientTask(FTPClient client) {
        this.client = client;
    }

    public File downloadFile(String remoteDirectory, String localDirectory) throws IOException {
        File downloadFile = new File(localDirectory);
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
        Log.i("FTPClientTask", "Initiated Downloading.");
        boolean success = client.retrieveFile(remoteDirectory, outputStream);
        if (success)
            Log.i("FTPClientTask", "Downloaded successfully!");
        else
            Log.i("FTPClientTask", "Download Failed!");
        outputStream.close();
        return downloadFile;
    }

    public File uploadFile(String remoteDirectory, String localDirectory) throws IOException {
        File uploadedFile = new File(localDirectory);
        InputStream inputStream = new BufferedInputStream(new FileInputStream(uploadedFile));
        Log.i("FTPClientTask", "Initiated Uploading.");
        boolean success = client.storeFile(remoteDirectory, inputStream);
        if (success)
            Log.i("FTPClientTask", "Upload successfully!");
        else
            Log.i("FTPClientTask", "Upload Failed!");
        inputStream.close();
        return uploadedFile;
    }
}
