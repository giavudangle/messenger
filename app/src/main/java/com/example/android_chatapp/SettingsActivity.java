package com.example.android_chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    // Firebase declare members
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;


    // Android Layout Mapping
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private Button mStatusBtn;
    private Button mImageBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Widget - Refactor soon
        mDisplayImage = (CircleImageView) findViewById(R.id.circleImageView);
        mName = (TextView) findViewById(R.id.settings_displayName);
        mStatus = (TextView) findViewById(R.id.settings_status);
        mStatusBtn = (Button) findViewById(R.id.settings_status_btn);
        mImageBtn = (Button) findViewById(R.id.settings_image_btn);

        // Get current user from Firebase throught FirebaseAuth.getInstance
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Get uid from user instance
        String current_uid = mCurrentUser.getUid();

        // Get child Users with current UID
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        // Data from server
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Toast.makeText(SettingsActivity.this,snapshot.toString(),Toast.LENGTH_LONG).show();
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String tumb_image = snapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_value = mStatus.getText().toString();
                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);
            }
        });
    }
}