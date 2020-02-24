package my.bandit.Model;

import androidx.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;

public class Post {
    @Getter
    private Song song;
    @Getter
    private String pictureDir;
    @Getter @Setter
    private int postID;
    public Post(Song song, String pictureDir) {
        this.song = song;
        this.pictureDir = pictureDir;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (!obj.getClass().equals(this.getClass()))
            return false;
        if (!(obj instanceof Post))
            return false;
        return this.getPostID() == ((Post) obj).getPostID();
    }

    @Override
    public int hashCode() {
        return this.getPostID();
    }
}
