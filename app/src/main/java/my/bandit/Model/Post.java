package my.bandit.Model;

import lombok.Getter;

public class Post {
    @Getter
    private Song song;
    @Getter
    private String pictureDir;
    private int postID;

    public Post(Song song, String pictureDir) {
        this.song = song;
        this.pictureDir = pictureDir;
    }
}
