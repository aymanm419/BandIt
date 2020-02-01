package my.bandit.FilesDownloader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;


public class DownloadImageTask extends AsyncTask<String, Void, File> {
    private WeakReference<ImageView> imageViewRef;
    private WeakReference<Context> mContextRef;
    public DownloadImageTask(Context mContext, ImageView imageView) {
        this.imageViewRef = new WeakReference<>(imageView);
        this.mContextRef = new WeakReference<>(mContext);
    }

    @Override
    protected File doInBackground(String... strings) {
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
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        final ImageView imageView = imageViewRef.get();
        final Context mContext = mContextRef.get();
        if (imageView != null && mContext != null)
            Glide.with(mContext).load(file).into(imageView);
    }
}
