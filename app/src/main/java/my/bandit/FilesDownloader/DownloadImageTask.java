package my.bandit.FilesDownloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        Glide.with(mContext).asBitmap().load(file).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Glide.with(mContext)
                        .load(resource)
                        .into(imageView);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }
}
