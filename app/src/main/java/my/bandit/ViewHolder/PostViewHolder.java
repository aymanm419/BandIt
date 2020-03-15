package my.bandit.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;
import my.bandit.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    @Getter
    private TextView postTextView, postAlbumTextView;
    @Getter
    private CircleImageView albumPicture;
    @Getter
    private ConstraintLayout constraintLayout;
    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        postTextView = itemView.findViewById(R.id.PostSongName);
        postAlbumTextView = itemView.findViewById(R.id.PostSingAlbum);
        albumPicture = itemView.findViewById(R.id.PostAlbumPicture);
        constraintLayout = itemView.findViewById(R.id.frameLayout);
    }
}
