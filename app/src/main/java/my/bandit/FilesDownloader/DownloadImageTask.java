package my.bandit.FilesDownloader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import my.bandit.Api.ApiHandler;
import my.bandit.Api.ResponseHandler;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class DownloadImageTask {
    private WeakReference<ImageView> imageViewRef;
    private WeakReference<Context> mContextRef;
    private String localDirectory;

    public DownloadImageTask(String localDirectory, Context mContext, ImageView imageView) {
        this.localDirectory = localDirectory;
        this.imageViewRef = new WeakReference<>(imageView);
        this.mContextRef = new WeakReference<>(mContext);
    }

    public void downloadFile(String remoteDirectory) {
        final ApiHandler apiHandler = ApiHandler.getInstance();
        Call<ResponseBody> call = apiHandler.getFilesApi().getFile(remoteDirectory);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!ResponseHandler.validateResponse(response))
                    return;
                new saveFile().execute(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.i("Download HTTP Server", "Download failed.");
            }
        });
    }


    private class saveFile extends AsyncTask<ResponseBody, Long, File> {

        @Override
        protected File doInBackground(ResponseBody... responseBodies) {
            ResponseBody body = responseBodies[0];
            File destinationFile = new File(localDirectory);
            try (InputStream inputStream = body.byteStream();
                 OutputStream outputStream = new FileOutputStream(destinationFile)) {
                Log.d("File Information", "File Size = " + body.contentLength());
                byte[] data = new byte[8192];
                int count;
                int progress = 0;
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    //Log.i(TAG, "Progress: " + progress + "/" + body.contentLength() + " >>>> " + (float) progress / body.contentLength() * 100);
                }
                outputStream.flush();
                Log.d(TAG, "File saved successfully!");
                return destinationFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            final ImageView imageView = imageViewRef.get();
            final Context mContext = mContextRef.get();
            if (imageView != null && mContext != null && file != null)
                Glide.with(mContext).load(file).into(imageView);
            else {
                if (mContext != null)
                    Toast.makeText(mContext, "Failed to download images.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
