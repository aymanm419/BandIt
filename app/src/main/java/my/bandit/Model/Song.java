package my.bandit.Model;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

public class Song {
    @Getter
    @Setter
    private String songName, albumName;
    @Getter
    @Setter
    private File songFile;

    public Song(String songName, String albumName, File file) {
        this.songName = songName;
        this.albumName = albumName;
        this.songFile = file;
    }
}
