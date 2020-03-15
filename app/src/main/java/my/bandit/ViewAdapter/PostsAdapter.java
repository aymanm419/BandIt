package my.bandit.ViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import my.bandit.Api.Api;
import my.bandit.Model.Post;
import my.bandit.R;
import my.bandit.ViewHolder.PostViewHolder;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {
    @Setter
    private List<Post> posts;
    private Context mContext;
    private PostClick postClick;

    public PostsAdapter(Context mContext, ArrayList<Post> posts, PostClick postClick) {
        this.mContext = mContext;
        this.posts = posts;
        this.postClick = postClick;
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
        holder.getPostAlbumTextView().setText(currentPost.getSong().getBandName());
        final ImageView imageView = holder.getAlbumPicture();
        Glide.with(mContext).load(Api.getImageSource(currentPost.getPictureDir())).into(imageView);
        holder.getConstraintLayout().setOnClickListener(v -> {
            postClick.onClick(currentPost, position);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}
