package my.bandit.ui.Login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutionException;

import my.bandit.Data.LoginDataSource;
import my.bandit.Data.LoginRepository;
import my.bandit.Data.Result;
import my.bandit.Data.model.LoggedInUser;
import my.bandit.R;
import my.bandit.Repository.AccountRegister;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());

    public LoginViewModel() {
    }

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            Log.i("Login", "Successful login.");
            loginResult.setValue(new LoginResult(true));
        } else {
            Log.i("Login", "Unsuccessful login.");
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public Result<LoggedInUser> register(String username, String password) {
        Result<LoggedInUser> result;
        try {
            result = new AccountRegister().execute(username, password).get();
        } catch (ExecutionException | InterruptedException e) {
            return new Result.Error(new Exception("Failed to connect"));
        }

        if (result instanceof Result.Success) {
            Log.i("Register", "Successful register.");
        } else {
            Log.i("Register", "Unsuccessful register.");
        }
        return result;
    }

    public boolean validateData(String username, String password) {
        loginResult.setValue(new LoginResult(false));
        return !isUsernameInvalid(username) && !isPasswordInvalid(password);
    }

    public void loginDataChanged(String username, String password) {
        if (isUsernameInvalid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (isPasswordInvalid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUsernameInvalid(String username) {
        return username == null;
    }

    // A placeholder password validation check
    private boolean isPasswordInvalid(String password) {
        return password == null;
    }
}
