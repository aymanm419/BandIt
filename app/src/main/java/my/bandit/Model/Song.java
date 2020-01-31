package my.bandit.Model;

import lombok.Getter;
import lombok.Setter;

public class Song {
    @Getter
    @Setter
    private String songName, albumName;
    @Getter
    @Setter
    private String songFileDir;

    public Song(String songName, String albumName, String songFileDir) {
        this.songName = songName;
        this.albumName = albumName;
        this.songFileDir = songFileDir;
    }
}
