package com.example.android_chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mListUsers;
    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Users List");
//        display the button back next to the title of toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mListUsers = findViewById(R.id.users_listUsers);
        mListUsers.setHasFixedSize(true);
        mListUsers.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(mUserDatabase, Users.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users users) {
                holder.setName(users.getName());
                holder.setStatus(users.getStatus());
                holder.setImage(users.getImage());
                final String selected_user_id = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",selected_user_id);
                        startActivity(profileIntent);
                    }
                });




            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                return new UsersViewHolder(view);
            }

        };
        mListUsers.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView displaynameView = mView.findViewById(R.id.users_single_name);
            displaynameView.setText(name);

        }

        public void setStatus(String status) {
            TextView userstatusView = mView.findViewById(R.id.users_single_status);
            userstatusView.setText(status);
        }

        public void setImage(String url_avatar){
            CircleImageView userAvatarView = mView.findViewById(R.id.users_single_avatar);
//                if user hadn't set avatar display default avatar
            if(!url_avatar.equals("default")){
                Picasso.get().load(url_avatar).placeholder(R.drawable.avatar).into(userAvatarView);
            }
        }

    }


}