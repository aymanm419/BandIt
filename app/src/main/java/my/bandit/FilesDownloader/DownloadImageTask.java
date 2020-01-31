package my.bandit.FilesDownloader;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;


public class DownloadImageTask extends AsyncTask<String, Void, File> {
    private ImageView imageView;
    private Context mContext;

    public DownloadImageTask(Context mContext, ImageView imageView) {
        this.imageView = imageView;
        this.mContext = mContext;
    }

    @Override
    protected File doInBackground(String... strings) {
        try {
            FTPClient client = FtpClient.getInstance().getConnection();
            final String localDirectory = strings[1];
            final String remoteDirectory = strings[0];
            File downloadFile = new File(localDirectory);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            Log.i("FTPClientTask", "Initiated Downloading.");
            boolean success = client.retrieveFile(remoteDirectory, outputStream);
            if (success)
                Log.i("FTPClientTask", "Downloaded successfully!");
            else
                Log.i("FTPClientTask", "Download Failed!");
            outputStream.close();
            FtpClient.getInstance().releaseConnection(client);
            return downloadFile;
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        try {
            InputStream inputStream = new FileInputStream(file);
            Glide.with(mContext)
                    .load(BitmapFactory.decodeStream(inputStream))
                    .into(imageView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}