package my.bandit.ViewAdapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import lombok.Setter;
import my.bandit.FilesDownloader.FTPClientCreator;
import my.bandit.FilesDownloader.FTPClientTask;
import my.bandit.Model.Post;
import my.bandit.R;
import my.bandit.ViewHolder.PostViewHolder;

public class PostsAdaper extends RecyclerView.Adapter<PostViewHolder> {
    @Setter
    List<Post> posts;
    Context mContext;

    public PostsAdaper(Context mContext, ArrayList<Post> Posts) {
        this.mContext = mContext;
        posts = Posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.getPostTextView().setText(posts.get(position).getSong().getSongName());
        holder.getPostAlbumTextView().setText(posts.get(position).getSong().getAlbumName());
        Callable<File> fileCallable = () -> {
            try {
                FTPClient ftpClient = FTPClientCreator.getConnection();
                FTPClientTask ftpClientTask = new FTPClientTask(ftpClient);
                File albumPicture = ftpClientTask.downloadFile(posts.get(position).getPictureDir(), mContext.getFilesDir() + "krutch.jpg");
                return albumPicture;
            } catch (ExecutionException | InterruptedException | FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        };
        FutureTask<File> fileFutureTask = new FutureTask<>(fileCallable);
        Thread thread = new Thread(fileFutureTask);
        thread.start();
        try {
            InputStream inputStream = new FileInputStream(fileFutureTask.get());
            Glide.with(mContext)
                    .load(BitmapFactory.decodeStream(inputStream))
                    .into(holder.getAlbumPicture());
        } catch (FileNotFoundException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.i("PostsAdapter", "Failed To Fetch Photo!!");
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
