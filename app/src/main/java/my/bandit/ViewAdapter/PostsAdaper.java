package my.bandit.ViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import my.bandit.FilesDownloader.DownloadImageTask;
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
        DownloadImageTask downloadImageTask = new DownloadImageTask(mContext, holder.getAlbumPicture());
        downloadImageTask.execute(posts.get(position).getPictureDir(), mContext.getFilesDir() + "/krutch.jpg");
        // DownloadSongTask downloadSongTask = new DownloadSongTask();
        //downloadSongTask.execute(posts.get(position).getSong().getSongFileDir(),mContext.getFilesDir() + "/" +
        //  posts.get(position).getSong().getSongName() + ".mp3");
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
