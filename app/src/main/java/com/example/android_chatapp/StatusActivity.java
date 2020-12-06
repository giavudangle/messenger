package com.example.android_chatapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSaveBtn;

    // Firebase Members
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    // Progress

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // Get Intent from Settings Activity

        String status_value = getIntent().getStringExtra("status_value");

        // Get Support Action Bar for Status Activity
        mToolbar = (Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Mapping members
        mStatus = (TextInputLayout) findViewById(R.id.status_input);
        mSaveBtn = (Button) findViewById(R.id.status_save_btn);

        mStatus.getEditText().setText(status_value);
        // Firebase

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        // Progress
        mProgress = new ProgressDialog(this);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Progress Appear for user interface
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save changes");
                mProgress.show();
                String status = mStatus.getEditText().getText().toString();

                // Set event when complete
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgress.dismiss();
                        } else {
                            Toast.makeText(StatusActivity.this, "There was some error in saving changes.Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


    }
}