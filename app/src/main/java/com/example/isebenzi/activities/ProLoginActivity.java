package com.example.isebenzi.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.isebenzi.R;
import com.example.isebenzi.business.handlers.AppHandlerNew;
import com.example.isebenzi.business.objects.SignInInput;
import com.example.isebenzi.business.objects.SignInResponse;
import com.example.isebenzi.utils.CommonMethods;
import com.example.isebenzi.utils.CommonObjects;

public class ProLoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private TextView tvRegister;
    private EditText etEmail;
    private EditText etPassword;
    private CheckBox cbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pro);
        CommonObjects.setContext(getApplicationContext());
        init();

    }

    private void init() {
        cbRememberMe = (CheckBox) findViewById(R.id.cbRememberMe);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        isLoginAlready();
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethods.callAnActivity(ProLoginActivity.this, ProRegisterActivity.class);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CommonMethods.callAnActivity(ProLoginActivity.this, ProviderDashboardActivity.class);
                if (CommonMethods.isNetworkAvailable(ProLoginActivity.this)) {
                    if (validate())
                        callServer(etEmail.getText().toString(), etPassword.getText().toString());
                }
            }
        });
    }

    private void isLoginAlready() {
        if (CommonMethods.getBooleanPreference(ProLoginActivity.this, "isebenzi", "ProisLogin", false)) {
            String email = CommonMethods.getStringPreference(ProLoginActivity.this, "isebenzi", "Proemail", "");
            String password = CommonMethods.getStringPreference(ProLoginActivity.this, "isebenzi", "Propassword", "");
            cbRememberMe.setChecked(true);
            if (CommonMethods.isNetworkAvailable(ProLoginActivity.this)) {
                callServer(email, password);
            }
        }
    }

    private void callServer(final String email, final String password) {
        CommonMethods.showProgressDialog(ProLoginActivity.this);
//        String token = FirebaseInstanceId.getInstance().getToken();
        AppHandlerNew.signIn(email, password,"","0", new AppHandlerNew.SignInListener() {
            @Override
            public void onSignIn(SignInResponse signInResponse) {
                switch (signInResponse.getMessage()) {
                    case "success":
                        if (cbRememberMe.isChecked()) {
                            CommonMethods.setStringPreference(ProLoginActivity.this, "isebenzi", "Proemail", email);
                            CommonMethods.setStringPreference(ProLoginActivity.this, "isebenzi", "Propassword", password);
                            CommonMethods.setBooleanPreference(ProLoginActivity.this, "isebenzi", "ProisLogin", true);
                        }
                        CommonObjects.setUser(signInResponse.getUser());
                        CommonMethods.callAnActivity(ProLoginActivity.this, ProviderDashboardActivity.class);
                        break;
                    case "failure":
                        Toast.makeText(ProLoginActivity.this, "Please enter a valid email and password", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(ProLoginActivity.this, "Please enter a valid email and password", Toast.LENGTH_SHORT).show();
                        break;
                }
                CommonMethods.hideProgressDialog();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty()) {
            etPassword.setError("enter a valid password");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }
}
