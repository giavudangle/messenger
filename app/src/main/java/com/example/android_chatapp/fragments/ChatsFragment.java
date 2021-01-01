package com.example.android_chatapp.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android_chatapp.R;
import com.example.android_chatapp.activity.ChatActivity;
import com.example.android_chatapp.models.Chats;
import com.example.android_chatapp.models.Messages;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabase;
    private DatabaseReference messageDatabase;

    private String name;
    private String image;


    private FirebaseUser currentUser;
    private String mCurrentUser;

    private View mChatView;
    private RecyclerView recyclerView;

    public ChatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mChatView = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = (RecyclerView) mChatView.findViewById(R.id.fragment_chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser = currentUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrentUser);


        databaseReference.keepSynced(true);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.keepSynced(true);
        return mChatView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions
                .Builder<Chats>()
                .setQuery(databaseReference,Chats.class)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Chats,ChatViewHolder>(options){

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_single_layout, parent, false);
                return new ChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Chats model) {
                final String uid = getRef(position).getKey().toString();
                userDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        name = snapshot.child(uid).child("name").getValue().toString();
                        image = snapshot.child(uid).child("thumb_image").getValue().toString();

                        holder.setName(name);
                        holder.setImage(image);

                        FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUser).child(uid).orderByChild("time").limitToLast(1)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Messages last = snapshot.getValue(Messages.class);
                                            if(last.getMessage().contains("https")){
                                                holder.setLastestMessage("\uD83D\uDCF8");

                                            }else {
                                                holder.setLastestMessage(last.getMessage());
                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), android.R.anim.fade_in, android.R.anim.fade_out);

                                String mName = snapshot.child("name").getValue().toString();
                                String mImage = snapshot.child("thumb_image").getValue().toString();

                                chatIntent.putExtra("user_id", uid);
                                chatIntent.putExtra("current_id", mCurrentUser);
                                chatIntent.putExtra("user_name", mName);
                                chatIntent.putExtra("user_image", mImage);


                                startActivity(chatIntent,options.toBundle());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        View itemView;

        public ChatViewHolder(View ItemView) {
            super(ItemView);
            itemView = ItemView;
        }

        public void setName(final String name) {
            TextView friendName = itemView.findViewById(R.id.fragment_chat_name);
            friendName.setText(name);
        }

        public void setImage(final String image) {
            CircleImageView mImage = (CircleImageView) itemView.findViewById(R.id.fragment_chat_image);
            ImageView mImageSeen =(ImageView) itemView.findViewById(R.id.img_last_seen);
            //picasso image downloading and
            Picasso.get().load(image).placeholder(R.drawable.avatar).into(mImage);
            Picasso.get().load(image).into(mImageSeen);
        }
        public void setLastestMessage(final String mess){
            TextView mLastSeen = (TextView) itemView.findViewById(R.id.fragment_chat_last_message);
            mLastSeen.setText(mess);
        }
    }
}