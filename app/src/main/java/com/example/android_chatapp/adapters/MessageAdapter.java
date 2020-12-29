package com.example.android_chatapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_chatapp.R;
import com.example.android_chatapp.models.Messages;
import com.example.android_chatapp.utils.GetTimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    String mCurrentUser;
    Context mContext;


    public MessageAdapter(Context context,List<Messages> mMessageList) {

        this.mMessageList = mMessageList;
        mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public TextView messageTime;

        public ImageView messageImage;


        public MessageViewHolder(View view) {
            super(view);
            messageTime = (TextView) view.findViewById(R.id.time_text_layout);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {
        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser = currentUser.getUid();


        if(c.getFrom().equals(mCurrentUser)){
            viewHolder.messageText.setBackgroundResource(R.drawable.message_received_background_layout);
            viewHolder.messageText.setTextColor(Color.WHITE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.messageText.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_LEFT);
            viewHolder.messageText.setLayoutParams(params);
        }
        if(!from_user.equals(mCurrentUser)) {
            viewHolder.messageText.setBackgroundResource(R.drawable.message_received_background_layout_client);
            viewHolder.messageText.setTextColor(Color.WHITE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.messageText.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_RIGHT);
            viewHolder.messageText.setLayoutParams(params);
        }

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name);

                Picasso.get().load(image)
                       .into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.GONE);
        } else {
            Picasso.get().load(c.getMessage()).into(viewHolder.messageImage);
            viewHolder.messageText.setVisibility(View.GONE);

        }


        String timeAgo = GetTimeAgo.getTimeAgo(c.getTime(),mContext);

        viewHolder.messageTime.setText(timeAgo);


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
