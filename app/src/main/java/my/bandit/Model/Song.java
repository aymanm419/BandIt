package my.bandit.Model;

import lombok.Getter;
import lombok.Setter;

public class Song {
    @Getter
    @Setter
    private String songName, bandName;
    @Getter
    @Setter
    private String songFileDir;

    public Song(String songName, String bandName, String songFileDir) {
        this.songName = songName;
        this.bandName = bandName;
        this.songFileDir = songFileDir;
    }
}
