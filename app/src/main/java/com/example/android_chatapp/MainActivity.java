package com.example.android_chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;

    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.main_page_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("VK Chat");
        if(mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        //tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
          navigateToStartActivity();
        } else {
            mUserRef.child("online").setValue("true");
        }

    }

    private void navigateToStartActivity(){
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.btn_logout_item) {
            FirebaseAuth.getInstance().signOut();
            navigateToStartActivity();
        }
        if(item.getItemId() == R.id.btn_account_settings_item) {
            Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
        }
        if(item.getItemId() == R.id.btn_all_users_item) {
            Intent settingIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingIntent);
        }
        return true;

    }
}