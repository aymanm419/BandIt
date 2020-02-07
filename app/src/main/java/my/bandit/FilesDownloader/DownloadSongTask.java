package my.bandit.FilesDownloader;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import my.bandit.PostsCache;

public class DownloadSongTask {

    public DownloadSongTask() {

    }

    public File downloadFile(String... strings) throws IOException {
        PostsCache postsCache = PostsCache.getInstance();
        if (postsCache.isCached(strings[0]))
            return postsCache.getSong(strings[0]);
        try {
            FTPClient client = FtpClient.getInstance().getConnection();
            final String localDirectory = strings[1];
            final String remoteDirectory = strings[0];
            File downloadFile = new File(localDirectory);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            Log.d("FTPClientTask", "Initiated Downloading.");
            boolean success = client.retrieveFile(remoteDirectory, outputStream);
            if (success)
                Log.d("FTPClientTask", "Downloaded successfully!");
            else
                Log.d("FTPClientTask", "Download Failed!");
            outputStream.close();
            FtpClient.getInstance().releaseConnection(client);
            return downloadFile;
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        throw new IOException("File Couldn't be downloaded!");
    }
}
