package my.bandit.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AccessLevel;
import lombok.Getter;
import my.bandit.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    @Getter(AccessLevel.PUBLIC)
    private TextView SongTextView, AlbumTextView;
    @Getter
    private ImageView LikeImageView, UnlikeImageView, HeartImageView;
    @Getter
    private ProgressBar SongProgressBar;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        SongTextView = itemView.findViewById(R.id.PostSongName);
        AlbumTextView = itemView.findViewById(R.id.PostSingAlbum);
        LikeImageView = itemView.findViewById(R.id.PostLikeImage);
        UnlikeImageView = itemView.findViewById(R.id.PostUnlikeImage);
        HeartImageView = itemView.findViewById(R.id.PostHeartImage);
        SongProgressBar = itemView.findViewById(R.id.PostProgressBar);
    }
}
