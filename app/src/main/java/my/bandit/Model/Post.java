package my.bandit.Model;

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
}
