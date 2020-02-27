package my.bandit.FilesDownloader;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import my.bandit.Api.Api;
import my.bandit.Api.ApiResponse;
import my.bandit.PostsCache;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadFileTask {
    private static DownloadFileTask downloadFileTask;

    private DownloadFileTask() {
    }

    public static DownloadFileTask getInstance() {
        if (downloadFileTask == null)
            downloadFileTask = new DownloadFileTask();
        return downloadFileTask;
    }

    public void downloadFile(String remoteDirectory, String localDirectory, OnFileDownload onFileDownload) {
        DownloadFile downloadFile = new DownloadFile(onFileDownload);
        downloadFile.execute(remoteDirectory, localDirectory);
    }

    private static class DownloadFile extends AsyncTask<String, Void, File> {
        private OnFileDownload onFileDownload;

        public DownloadFile(OnFileDownload onFileDownload) {
            this.onFileDownload = onFileDownload;
        }

        private File saveToDisk(String localDirectory, ResponseBody responseBody) {
            File destinationFile = new File(localDirectory);
            try (InputStream inputStream = responseBody.byteStream();
                 OutputStream outputStream = new FileOutputStream(destinationFile)) {
                Log.d("File Information", "File Size = " + responseBody.contentLength());
                byte[] data = new byte[8192];
                int count;
                while ((count = inputStream.read(data)) != -1)
                    outputStream.write(data, 0, count);
                outputStream.flush();
                Log.d("Download Information", "File saved successfully!");
                return destinationFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected File doInBackground(String... strings) {
            final String remoteDirectory = strings[0];
            final String localDirectory = strings[1];
            PostsCache postsCache = PostsCache.getInstance();
            if (postsCache.isCached(remoteDirectory))
                return postsCache.getFile(remoteDirectory);
            final Api api = Api.getInstance();
            Call<ResponseBody> call = api.getFilesApi().getFile(remoteDirectory);
            try {
                Response<ResponseBody> response = call.execute();
                if (ApiResponse.validateResponse(response)) {
                    File file = saveToDisk(localDirectory, response.body());
                    postsCache.cacheFile(remoteDirectory, file);
                    return file;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file != null)
                onFileDownload.onSuccess(file);
            else
                onFileDownload.onFailure();
        }
    }

}
