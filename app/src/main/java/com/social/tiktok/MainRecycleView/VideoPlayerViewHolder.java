package com.social.tiktok.MainRecycleView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.service.autofill.AutofillService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.social.tiktok.HomeActivity;
import com.social.tiktok.Models.MediaObject;
import com.social.tiktok.R;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {

    FrameLayout media_container;
    TextView title,user_id;
    ImageView thumbnail, volumeControl;
    ProgressBar progressBar;
    View parent;
    ImageView imageShare;
    RequestManager requestManager;
    ImageView imageLove;
    ImageView imageComment;
    CircleImageView imageUserProfile;
    ImageView imageFollowUser;
    static Context ctx;

    public VideoPlayerViewHolder(@NonNull View itemView, Context context)
    {
        super(itemView);
        ctx=context;
        parent=itemView;
        media_container=itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        title = itemView.findViewById(R.id.status);
        user_id=itemView.findViewById(R.id.textUser);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl=itemView.findViewById(R.id.volume_control);
        imageShare=(ImageView)itemView.findViewById(R.id.imageShare);
        imageLove=(ImageView)itemView.findViewById(R.id.imageLove);
        imageComment=(ImageView)itemView.findViewById(R.id.imageComment);
        imageUserProfile=(CircleImageView)itemView.findViewById(R.id.imageUsers);
        imageLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLove.setColorFilter(Color.RED);
            }
        });
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context,R.style.BottomSheetDialogTheme);
                View bottomSheetView= LayoutInflater.from(context.getApplicationContext()).inflate(
                        R.layout.layout_bottom_share,
                        itemView.findViewById(R.id.bottomShare)
                );
                bottomSheetView.findViewById(R.id.downloadBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
            }
        });
    }

    public void onBind(MediaObject mediaObject,RequestManager requestManager)
    {
        this.requestManager=requestManager;
        parent.setTag(this);
        title.setText(mediaObject.getDescription()+"\n"+mediaObject.getDate());
        user_id.setText("@"+mediaObject.getUser_name());
        FirebaseDatabase.getInstance().getReference().child("users").child(mediaObject.getUser_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("uri", "onDataChange: "+snapshot.child("profileImage").getValue(String.class));
                requestManager.load(snapshot.child("profileImage").getValue(String.class)).into(imageUserProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //this.requestManager.load(mediaObject.getThumbnail()).into(thumbnail);
    }
}
