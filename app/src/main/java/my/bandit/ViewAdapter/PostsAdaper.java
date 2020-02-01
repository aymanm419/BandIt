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
    private List<Post> posts;
    private Context mContext;

    public PostsAdaper(Context mContext, ArrayList<Post> posts) {
        this.mContext = mContext;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        final Post currentPost = posts.get(position);
        holder.getPostTextView().setText(currentPost.getSong().getSongName());
        holder.getPostAlbumTextView().setText(currentPost.getSong().getAlbumName());
        DownloadImageTask downloadImageTask = new DownloadImageTask(mContext, holder.getAlbumPicture());
        downloadImageTask.execute(currentPost.getPictureDir(), mContext.getFilesDir() + currentPost.getSong().getAlbumName());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
