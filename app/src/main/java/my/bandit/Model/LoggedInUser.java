package my.bandit.Model;

import java.util.ArrayList;

import lombok.Data;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Data
public class LoggedInUser {

    private int userId;
    private String displayName;
    private ArrayList<Post> favourites;
    private ArrayList<Post> liked;
    private ArrayList<Post> disliked;

    public LoggedInUser(int userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public int getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
