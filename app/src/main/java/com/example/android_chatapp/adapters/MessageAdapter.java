package com.example.android_chatapp.adapters;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_chatapp.R;
import com.example.android_chatapp.activity.ChatActivity;
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

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    String mCurrentUser;
    Context mContext;
    ProgressDialog mProgressDialog;
    private String mListFile;

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;


    FirebaseUser fuser;

    public MessageAdapter(Context context,List<Messages> mMessageList) {

        this.mMessageList = mMessageList;
        mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_single_layout_right_panel, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_single_layout_left_panel, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public TextView messageTime;
        public TextView seen;

        public ImageView messageImage;



        public MessageViewHolder(View view) {
            super(view);
            messageTime = (TextView) view.findViewById(R.id.time_text_layout);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            seen=(TextView) view.findViewById(R.id.seen);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, final int i) {

        final Messages c = mMessageList.get(i);

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
                if(getItemViewType(i) == MSG_TYPE_LEFT){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    viewHolder.displayName.setText(name);
                    Picasso.get().load(image)
                            .into(viewHolder.profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(c==null){
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.displayName.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.messageTime.setVisibility(View.GONE);
        }

        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.GONE);

            if(getItemViewType(i) == MSG_TYPE_RIGHT){
                viewHolder.messageTime.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.GONE);
                if(i != mMessageList.size()-1){
                    viewHolder.seen.setVisibility(View.GONE);
                }
            } else {
                String timeAgo = GetTimeAgo.getTimeAgo(c.getTime(),mContext);
                viewHolder.messageTime.setText(timeAgo);
            }

        }
        else if(message_type.equals("word")) {
            viewHolder.messageText.setText("\uD83D\uDCCE");
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.messageTime.setVisibility(View.GONE);
        }
        else if(message_type.equals("image")) {
            if(getItemViewType(i) == MSG_TYPE_RIGHT){
                viewHolder.messageTime.setVisibility(View.GONE);
                Picasso.get().load(c.getMessage()).into(viewHolder.messageImage);
                viewHolder.messageText.setVisibility(View.GONE);
                if(i != mMessageList.size()-1){
                    viewHolder.seen.setVisibility(View.GONE);
                }
            } else {
                String timeAgo = GetTimeAgo.getTimeAgo(c.getTime(),mContext);
                viewHolder.messageTime.setText(timeAgo);

                Picasso.get().load(c.getMessage()).into(viewHolder.messageImage);
                viewHolder.messageText.setVisibility(View.GONE);
            }

        }

        if(i == mMessageList.size()-1){
            if (c.getFrom().equals(mCurrentUser) ){
                if(c.isSeen()){
                    viewHolder.seen.setVisibility(View.VISIBLE);
                    viewHolder.seen.setText("Seen");
                }
                else
                    viewHolder.seen.setText("Delivered");
            }
        }else {
            viewHolder.seen.setVisibility(View.GONE);
        }




        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

               if(viewHolder.messageText.getText().toString().equals("\uD83D\uDCCE")){
                   new AlertDialog.Builder(mContext)
                           .setMessage("Are you sure you want to download word file ?")
                           .setCancelable(false)
                           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   String DIR_NAME = "Downloads";
                                   String fileName ="Word";
                                   String downloadUrlOfImage = mMessageList.get(i).getMessage();

                                   DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                                   Uri downloadUri = Uri.parse(downloadUrlOfImage);
                                   DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                                   request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                           .setAllowedOverRoaming(false)
                                           .setTitle(fileName)
                                           .setMimeType("doc")
                                           .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                           .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                   File.separator + DIR_NAME + File.separator + fileName);
                                   dm.enqueue(request);
                                   Toast.makeText(mContext,"Download file successfully",Toast.LENGTH_SHORT).show();
                               }
                           })
                           .setNegativeButton("No", null)
                           .show();




               }
               else if (viewHolder.messageImage.getHeight()!=-1){


                    new AlertDialog.Builder(mContext)
                           .setMessage("Are you sure you want to download image ?")
                           .setCancelable(false)
                           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   String DIR_NAME = "Downloads";
                                   String filename = "image.jpg";
                                   String downloadUrlOfImage = mMessageList.get(i).getMessage();
                                   File direct =
                                           new File(Environment
                                                   .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                                   .getAbsolutePath() + "/" + DIR_NAME + "/");


                                   if (!direct.exists()) {
                                       direct.mkdir();
                                       Log.d("image down", "dir created for first time");
                                   }


                                   DownloadManager dm = (DownloadManager) view.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                   Uri downloadUri = Uri.parse(downloadUrlOfImage);
                                   DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                                   request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                           .setAllowedOverRoaming(false)
                                           .setTitle(filename)
                                           .setMimeType("image/jpeg")
                                           .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                           .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                                                   File.separator + DIR_NAME + File.separator + filename);

                                   dm.enqueue(request);
                                   Toast.makeText(mContext,"Download IMAGE successfully",Toast.LENGTH_SHORT).show();
                               }
                           })
                           .setNegativeButton("No", null)
                           .show();


               }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessageList.get(position).getFrom().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }
}
