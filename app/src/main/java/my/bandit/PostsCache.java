package my.bandit;

import android.util.Log;

import java.io.File;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import my.bandit.Model.Post;

public class PostsCache {
    private static PostsCache postsCache;

    private HashMap<String, File> cache;
    @Setter
    @Getter
    private Post lastPlayed;

    private PostsCache() {
        cache = new HashMap<>();
    }

    public static synchronized PostsCache getInstance() {
        if (postsCache == null)
            postsCache = new PostsCache();
        return postsCache;
    }

    public boolean isCached(String string) {
        return cache.containsKey(string);
    }

    public void cacheSong(String fileDir, File file) {
        if (isCached(fileDir))
            return;
        cache.put(fileDir, file);
        Log.i("Cache Information", "Cached file with directory = " + fileDir);
    }

    public File getSong(String fileDir) {
        Log.i("Cache Request", "Asking for file at = " + fileDir);
        if (isCached(fileDir)) {
            Log.i("Cache Request", "Item requested feteched");
            return cache.get(fileDir);
        }
        Log.i("Cache Request", "Item was not found");
        return null;
    }
}
