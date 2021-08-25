package com.social.tiktok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.tiktok.Models.MediaObject;
import com.social.tiktok.Models.Status;
import com.social.tiktok.Models.UserObject;

import java.util.Date;
import java.util.HashMap;

public class UploadVideoActivity extends AppCompatActivity {

    private UserObject userObject;
    private ProgressDialog dialogUploadVideo;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private Button buttonUploadVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        dialogUploadVideo=new ProgressDialog(this);
        dialogUploadVideo.setMessage("Uploading Video...");
        dialogUploadVideo.setCancelable(false);
        buttonUploadVideo=findViewById(R.id.buttonUploadVideo);
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String user_id = snapshot.getKey();
                        String user_name = snapshot.child("user_name").getValue(String.class);
                        String profileImage = snapshot.child("profileImage").getValue(String.class);
                        String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        userObject=new UserObject(user_id,user_name,phoneNumber,email,profileImage);
                        //userObject.setEmail(snapshot.getValue("");

//                        userObject=snapshot.getValue(UserObject.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                Uri videoUri=data.getData();
                VideoView videoView=findViewById(R.id.videoView);
                //MediaController mediaController=new MediaController(this);
                //mediaController.setAnchorView(videoView);
                //videoView.setMediaController(mediaController);
                videoView.setVideoURI(videoUri);
//                videoView.start();
                if(!videoView.isPlaying())
                {
                    videoView.start();
                }
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
                //videoView.requestFocus();
//                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        videoView.pause();
//                    }
//                });
                buttonUploadVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUploadVideo.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("media").child(date.getTime() + "");
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    MediaObject mediaObject = new MediaObject();
                                    mediaObject.setUser_name(userObject.getUser_name());
                                    //mediaObject.set(userObject.getProfileImage());
                                    mediaObject.setDate(""+date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();

                                    obj.put("name", userObject.getUser_name());
                                    obj.put("profileImage", userObject.getProfileImage());
                                    obj.put("lastUpdated", ""+date.getTime());

                                    String imageUrl = uri.toString();
                                    MediaObject mediaObj=new  MediaObject("hello", "XIn chào",""+ date.getTime(), FirebaseAuth.getInstance().getUid(), "", "", "", FirebaseAuth.getInstance().getUid(), uri+"", "");
                                    //Status status = new Status(imageUrl, userStatus.getLastUpdated());


                                    database.getReference()
                                            .child("videos")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("videos")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("media")
                                            .push()
                                            .setValue(mediaObj);
                                    dialogUploadVideo.dismiss();
                                    Intent intent=new Intent(UploadVideoActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                    }
                        });
                    }
                });

            }
        }
    }

    public void upload(View view) {
        Intent intent=new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Videos"),75);
    }
}