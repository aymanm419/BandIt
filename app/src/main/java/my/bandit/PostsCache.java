package my.bandit;

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
    }

    public File getSong(String fileDir) {
        if (isCached(fileDir))
            return cache.get(fileDir);
        return null;
    }
}
