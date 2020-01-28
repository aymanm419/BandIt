package my.bandit.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.Getter;
import my.bandit.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    @Getter
    private TextView postTextView, postAlbumTextView;
    @Getter
    private ImageView LikeImageView, UnlikeImageView, HeartImageView, albumPicture;
    @Getter
    private ProgressBar SongProgressBar;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        postTextView = itemView.findViewById(R.id.PostSongName);
        postAlbumTextView = itemView.findViewById(R.id.PostSingAlbum);
        LikeImageView = itemView.findViewById(R.id.PostLikeImage);
        UnlikeImageView = itemView.findViewById(R.id.PostUnlikeImage);
        HeartImageView = itemView.findViewById(R.id.PostHeartImage);
        SongProgressBar = itemView.findViewById(R.id.PostProgressBar);
        albumPicture = itemView.findViewById(R.id.albumPicture);
    }
}
