package my.bandit.ui.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import my.bandit.R;
import my.bandit.data.LoginDataSource;
import my.bandit.data.LoginRepository;
import my.bandit.data.Result;
import my.bandit.data.model.LoggedInUser;

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

    public boolean validateData(String username, String password) {
        if (isUsernameInvalid(username) || isPasswordInvalid(password))
            return false;
        else
            return true;
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
        if (username == null) {
            return true;
        }
        if (username.length() >= 4 && username.length() < 14) {
            return false;
        } else {
            return true;
        }
    }

    // A placeholder password validation check
    private boolean isPasswordInvalid(String password) {
        return password == null || password.trim().length() <= 5;
    }
}
