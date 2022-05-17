package com.nsh.goapps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nsh.goapps.R;
import com.nsh.goapps.model.User;
import com.nsh.goapps.response.ResponseLogin;
import com.nsh.goapps.rest.ApiClient;
import com.nsh.goapps.rest.ApiInterface;
import com.nsh.goapps.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.btnLogin)
    Button login;

    @BindView(R.id.etusername)
    EditText etUsername;

    @BindView(R.id.etpassword)
    EditText etPassword;

    @BindView(R.id.tvregister)
    TextView register_user;

    ApiInterface apiservice;
    SessionManager sessionManager;

    private static final String TAG = "LoginActivity";

    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        setContentView(R.layout.login_relative);
        ButterKnife.bind(this);

        apiservice = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        register_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_user();
            }
        });

    }

    private void register_user() {
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    private void loginUser() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        apiservice.login(username, password).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()){
                    User userLoggedIn = response.body().getUser();
                    sessionManager.createLoginSession(userLoggedIn);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                } else if (!response.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Username atau Password salah", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Log.e(TAG, "onFailure:"+t.getLocalizedMessage());
                Toast.makeText(LoginActivity.this, "Gagal Terhubung ke server", Toast.LENGTH_LONG).show();
            }
        });
    }
}
