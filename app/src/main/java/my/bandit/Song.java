package my.bandit;

import android.graphics.Bitmap;

import lombok.Getter;
import lombok.Setter;

public class Song {
    @Getter
    @Setter
    private String SongName, AlbumName;
    @Getter
    @Setter
    private Bitmap AlbumImage;

    public Song(String songName, String albumName, Bitmap albumImage) {
        SongName = songName;
        AlbumName = albumName;
        AlbumImage = albumImage;
    }
}
