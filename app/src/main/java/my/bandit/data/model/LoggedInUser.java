package my.bandit.data.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    @Getter@Setter
    private ArrayList<Integer> favourites;
    @Getter@Setter
    private ArrayList<Integer> liked;
    @Getter@Setter
    private ArrayList<Integer> disliked;

    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
