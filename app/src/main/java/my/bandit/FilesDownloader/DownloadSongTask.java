package my.bandit.FilesDownloader;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import my.bandit.Api.ApiHandler;
import my.bandit.Api.ResponseHandler;
import my.bandit.PostsCache;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadSongTask {
    private String localDirectory;

    public DownloadSongTask(String localDirectory) {
        this.localDirectory = localDirectory;

    }

    private File saveToDisk(ResponseBody responseBody) {
        File destinationFile = new File(localDirectory);
        try (InputStream inputStream = responseBody.byteStream();
             OutputStream outputStream = new FileOutputStream(destinationFile)) {
            Log.i("File Information", "File Size = " + responseBody.contentLength());
            byte[] data = new byte[8192];
            int count;
            int progress = 0;
            while ((count = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, count);
                progress += count;
                //Log.i("Download Information", "Progress: " + progress + "/" + responseBody.contentLength() + " >>>> " + (float) progress / responseBody.contentLength() * 100);
            }
            outputStream.flush();
            Log.i("Download Information", "File saved successfully!");
            return destinationFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public File downloadFile(String... strings) throws IOException {
        final String remoteDirectory = strings[0];
        final ApiHandler apiHandler = ApiHandler.getInstance();
        PostsCache postsCache = PostsCache.getInstance();
        if (postsCache.isCached(remoteDirectory))
            return postsCache.getSong(remoteDirectory);
        Call<ResponseBody> call = apiHandler.getDataApi().getImage(remoteDirectory);
        Response<ResponseBody> response = call.execute();
        if (ResponseHandler.validateResponse(response)) {
            File file = saveToDisk(response.body());
            postsCache.cacheSong(remoteDirectory, file);
            return file;
        }
        return null;
    }
}
