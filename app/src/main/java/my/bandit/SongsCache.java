package my.bandit;

import java.io.File;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import my.bandit.Model.Post;

public class SongsCache {
    private static SongsCache songsCache;

    private HashMap<String, File> cache;
    @Setter
    @Getter
    private Post lastPlayed;

    private SongsCache() {
        cache = new HashMap<>();
    }

    public static synchronized SongsCache getInstance() {
        if (songsCache == null)
            songsCache = new SongsCache();
        return songsCache;
    }

    public boolean isCached(String string) {
        return cache.containsKey(string);
    }

    public void cacheSong(String fileDir, File file) {
        if (isCached(fileDir))
            return;
        cache.put(fileDir, file);
    }

    public File getSong(String fileDir) {
        if (isCached(fileDir))
            return cache.get(fileDir);
        return null;
    }
}
