package my.bandit.ViewAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import my.bandit.R;
import my.bandit.Song;
import my.bandit.ViewHolder.PostViewHolder;

public class PostsAdaper extends RecyclerView.Adapter<PostViewHolder> {
    @Setter
    List<Song> Songs;

    public PostsAdaper(ArrayList<Song> Posts) {
        Songs = Posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.getSongTextView().setText(Songs.get(position).getSongName());
        holder.getAlbumTextView().setText(Songs.get(position).getAlbumName());
    }

    @Override
    public int getItemCount() {
        return Songs.size();
    }
}
