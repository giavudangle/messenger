package com.example.android_chatapp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_chatapp.R;
import com.example.android_chatapp.activity.ProfileActivity;
import com.example.android_chatapp.models.Accept;
import com.example.android_chatapp.models.Friends;
import com.example.android_chatapp.models.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

    private DatabaseReference friendRequestReference;
    private DatabaseReference userDatabase;
    private DatabaseReference mRootRef;
    private FirebaseUser currentUser;
    private String mCurrentUser;


    //Views
    private RecyclerView recyclerViewAccept;
    private View mRequestView;

    public RequestsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRequestView = inflater.inflate(R.layout.fragment_requests,container,false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        //RecyclerView

        recyclerViewAccept = (RecyclerView) mRequestView.findViewById(R.id.request_fragment_recycler_view_accept);
        recyclerViewAccept.setLayoutManager(layoutManager);
        recyclerViewAccept.setHasFixedSize(true);

        //Creating Database refrence
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser = currentUser.getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrentUser);
        friendRequestReference.keepSynced(true);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.keepSynced(true);

        return mRequestView;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Accept> options =
                new FirebaseRecyclerOptions.Builder<Accept>()
                        .setQuery(friendRequestReference,Accept.class).build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Accept,AcceptViewHolder>(options){


            @NonNull
            @Override
            public AcceptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_single_layout, parent, false);
                return new AcceptViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final AcceptViewHolder holder, int position, @NonNull Accept model) {
                String requestType = model.getRequest_type();


                    final String uid = getRef(position).getKey().toString();
                    userDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child(uid).child("name").getValue().toString();
                            String image = dataSnapshot.child(uid).child("thumb_image").getValue().toString();
                            String status = dataSnapshot.child(uid).child("status").getValue().toString();
                            holder.setStatus(status);
                            holder.setName(name);
                            holder.setImage(image);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }

                    });
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Button mBtnAccept = view.findViewById(R.id.request_fragment_button_accept);
                            Button mBtnDecline = view.findViewById(R.id.request_fragment_button_decline);


                            mBtnAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                    Map friendsMap = new HashMap();
                                    friendsMap.put("Friends/" + mCurrentUser+ "/" + uid + "/date", currentDate);
                                    friendsMap.put("Friends/" + uid + "/" + mCurrentUser + "/date", currentDate);


                                    friendsMap.put("Friend_req/" + mCurrentUser + "/" + uid, null);
                                    friendsMap.put("Friend_req/" + uid + "/" + mCurrentUser, null);

                                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if(error == null){
                                                Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });

                            mBtnDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map friendsMap = new HashMap();

                                    friendsMap.put("Friend_req/" + mCurrentUser + "/" + uid, null);
                                    friendsMap.put("Friend_req/" + uid + "/" + mCurrentUser, null);


                                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if(error == null){
                                                Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }



        };

        recyclerViewAccept.setAdapter(adapter);
        adapter.startListening();


    }

    public static class AcceptViewHolder extends RecyclerView.ViewHolder {
        View acceptView;
        Button acceptButton;
        Button declineButton;
        public AcceptViewHolder(View itemView) {
            super(itemView);
            acceptView = itemView;
            declineButton = acceptView.findViewById(R.id.request_fragment_button_decline);
            acceptButton = acceptView.findViewById(R.id.request_fragment_button_accept);
        }
        public void setButtons(String type) {
            declineButton.setVisibility(View.INVISIBLE);
            acceptButton.setText("Cancel");
        }
        public void setStatus(final String status) {
            TextView mStatus = acceptView.findViewById(R.id.request_fragment_status);
            mStatus.setText(status);
        }
        public void setName(final String name) {
            TextView acceptName = acceptView.findViewById(R.id.request_fragment_name);
            acceptName.setText(name);
        }
        public void setImage(final String image) {
            CircleImageView mImage = (CircleImageView) acceptView.findViewById(R.id.request_fragment_image);
            //picasso image downloading and
            Picasso.get().load(image).placeholder(R.drawable.avatar).into(mImage);
        }
    }
}