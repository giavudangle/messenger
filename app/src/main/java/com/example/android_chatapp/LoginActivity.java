package com.example.android_chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputEditText mLoginEmail;
    private TextInputEditText mLoginPassword;

    private FirebaseAuth mAuth;

    private Button mLogin_btn;

    private ProgressDialog mLoginProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        mAuth = FirebaseAuth.getInstance();

        mLoginProcess = new ProgressDialog(this);

        mLoginEmail = (TextInputEditText) findViewById(R.id.edt_login_email);
        mLoginPassword = (TextInputEditText) findViewById(R.id.edt_login_password);
        mLogin_btn = (Button) findViewById(R.id.btn_login);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getText().toString().trim();
                String password = mLoginPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mLoginProcess.setTitle("Logging In");
                    mLoginProcess.setMessage("Please wait while we check your credentials");
                    mLoginProcess.setCanceledOnTouchOutside(false);
                    mLoginProcess.show();
                    loginUser(email, password);
                }
            }
        });
    }


    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            mLoginProcess.hide();
                            // If log in fails, display a message to the user.
                            Log.w("Error", "LoginWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.Please check form again !!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}