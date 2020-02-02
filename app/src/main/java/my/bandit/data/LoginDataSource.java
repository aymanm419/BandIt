package my.bandit.data;

import my.bandit.Repository.AccountLoader;
import my.bandit.Repository.AccountVerifier;
import my.bandit.data.model.LoggedInUser;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        String[] args = {username, password};
        try {
            if (new AccountVerifier().execute(args).get()) {
                LoggedInUser fakeUser = new AccountLoader().execute(username).get();
                return new Result.Success<>(fakeUser);
            } else {
                return new Result.Error(new IOException("Error logging in"));
            }
        } catch (ExecutionException | InterruptedException e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
