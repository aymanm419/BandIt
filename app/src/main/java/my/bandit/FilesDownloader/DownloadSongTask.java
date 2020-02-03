package my.bandit.FilesDownloader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import my.bandit.Service.MusicService;
import my.bandit.SongsCache;

public class DownloadSongTask extends AsyncTask<String, Void, File> {
    private WeakReference<Context> mContext;

    public DownloadSongTask(Context mContext) {
        this.mContext = new WeakReference<>(mContext);
    }

    @Override
    protected File doInBackground(String... strings) {
        SongsCache songsCache = SongsCache.getInstance();
        if (songsCache.isCached(strings[1]))
            return songsCache.getSong(strings[1]);
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
        Context context = mContext.get();
        if (context != null && file != null) {
            SongsCache songsCache = SongsCache.getInstance();
            songsCache.cacheSong(file.getAbsolutePath(), file);
            Intent intent = new Intent(context, MusicService.class);
            intent.putExtra("ACTION_CMD", MusicService.MUSIC_START);
            intent.putExtra("SONG", file);
            context.startService(intent);
        }


    }
}
