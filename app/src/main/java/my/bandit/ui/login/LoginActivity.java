package my.bandit.ui.login;

import android.app.Activity;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import my.bandit.MainActivity;
import my.bandit.R;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMe;

    private String username, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        attemptRemember();
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        rememberMe = findViewById(R.id.Remember);
        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess()) {
                updateUiWithUser();
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful

        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
    }

    private void LogUser(String username, String password) {
        if (!loginViewModel.validateData(username, password)) {
            Toast.makeText(getApplicationContext(), "Invalid username/password", Toast.LENGTH_LONG).show();
            return;
        }
        loginViewModel.login(username, password);
    }

    public void registerFunction(View view) {
        Log.d("Register", "Button pressed");
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        Toast.makeText(getApplicationContext(), loginViewModel.register(username, password).toString(), Toast.LENGTH_LONG).show();
    }

    public void LoginFunction(View view) {
        Log.i("Login", "Button pressed");
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        LogUser(username, password);
        if (rememberMe.isChecked() && loginViewModel.getLoginResult().getValue().getSuccess()) {
            Log.i("Login", "Saving login data");
            SharedPreferences savedData = this.getPreferences(Context.MODE_PRIVATE);
            savedData.edit().putString("Username", username).apply();
            savedData.edit().putString("Password", password).apply();
        }
    }

    private void attemptRemember() {
        SharedPreferences savedData = this.getPreferences(Context.MODE_PRIVATE);
        Log.i("Login", "Attempting to get saved data");
        username = savedData.getString("Username", "");
        password = savedData.getString("Password", "");
        LogUser(username, password);
    }

    private void updateUiWithUser() {

        Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);
        Toast.makeText(getApplicationContext(), "Welcome, " + username, Toast.LENGTH_LONG).show();
        startActivity(mainAct);
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
