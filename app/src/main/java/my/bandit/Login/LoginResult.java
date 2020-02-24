package my.bandit.Login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private boolean success = false;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable boolean success) {
        this.success = success;
    }

    @Nullable
    boolean getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
