package my.bandit.Model;

import android.graphics.Bitmap;

import lombok.Getter;

public class Post {
    @Getter
    private Song song;
    @Getter
    private Bitmap picture;
    private int postID;

    public Post(Song song, Bitmap picture) {
        this.song = song;
        this.picture = picture;
    }
}
