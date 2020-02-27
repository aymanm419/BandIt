package my.bandit;

import android.util.Log;

import java.io.File;
import java.util.HashMap;

public class PostsCache {
    private static PostsCache postsCache;

    private HashMap<String, File> cache;

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

    public void cacheFile(String fileDir, File file) {
        if (isCached(fileDir))
            return;
        cache.put(fileDir, file);
        Log.i("Cache Information", "Cached file with directory = " + fileDir);
    }

    public File getFile(String fileDir) {
        Log.i("Cache Request", "Asking for file at = " + fileDir);
        if (isCached(fileDir)) {
            Log.i("Cache Request", "Item requested feteched");
            return cache.get(fileDir);
        }
        Log.i("Cache Request", "Item was not found");
        return null;
    }
}
