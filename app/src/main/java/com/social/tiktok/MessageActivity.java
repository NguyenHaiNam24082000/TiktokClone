package com.social.tiktok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.tiktok.Adapter.TopStatusAdapter;
import com.social.tiktok.Adapter.UserAdapter;
import com.social.tiktok.Models.Status;
import com.social.tiktok.Models.UserObject;
import com.social.tiktok.Models.UserStatus;
import com.social.tiktok.databinding.ActivityHomeBinding;
import com.social.tiktok.databinding.ActivityMessageBinding;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    FirebaseDatabase database;
    ArrayList<UserObject> userObjects;
    UserAdapter userAdapter;
    ProgressDialog dialog;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialogUploadImage;
    UserObject userObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        dialogUploadImage=new ProgressDialog(this);
        dialogUploadImage.setMessage("Uploading Image...");
        dialogUploadImage.setCancelable(false);
        userObjects=new ArrayList<>();
        userStatuses=new ArrayList<>();
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
        userAdapter=new UserAdapter(this,userObjects);
//        binding.recyclerViewMessage.setLayoutManager(new LinearLayoutManager(this));
        statusAdapter = new TopStatusAdapter(this,userStatuses);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(statusAdapter);
        binding.recyclerViewMessage.setAdapter(userAdapter);
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userObjects.clear();
                Log.d("Hello", "onDataChange: "+snapshot.getValue());
                for (DataSnapshot ds : snapshot.getChildren())
                {
//                    UserObject userObject=d.getValue(UserObject.class);
//                    userObjects.add(userObject);
                    String user_id = ds.getKey();
                    String user_name = ds.child("user_name").getValue(String.class);
                    String profileImage = ds.child("profileImage").getValue(String.class);
                    String phoneNumber = ds.child("phoneNumber").getValue(String.class);
                    String email = ds.child("email").getValue(String.class);
                    UserObject user=new UserObject(user_id,user_name,phoneNumber,email,profileImage);
                    if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        userObjects.add(user);
                }
                userAdapter.notifyDataSetChanged();
                dialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    userStatuses.clear();
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        UserStatus status=new UserStatus();
                        status.setName(ds.child("name").getValue(String.class));
                        status.setProfileImage(ds.child("profileImage").getValue(String.class));
                        status.setLastUpdated(ds.child("lastUpdated").getValue(Long.class));
                        ArrayList<Status> statuses=new ArrayList<>();
                        for(DataSnapshot stDataSnapshot: ds.child("statuses").getChildren())
                        {
                            Status sampleStatus = stDataSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }
                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.story:
                        Intent intent=new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,75);
                        //Intent intent=new Intent(MessageActivity.this,StoryActivity.class);
                        //startActivity(intent);
                        break;
                    case R.id.calls:
                            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                    .setRoom(FirebaseAuth.getInstance().getUid())
                                    .setWelcomePageEnabled(false)
                                    .build();
                            JitsiMeetActivity.launch(MessageActivity.this, options);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null)
        {
            if(data.getData()!=null)
            {
                dialogUploadImage.show();
                FirebaseStorage storage=FirebaseStorage.getInstance();
                Date date=new Date();
                StorageReference reference=storage.getReference().child("status").child(date.getTime()+"");
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus=new UserStatus();
                                    userStatus.setName(userObject.getUser_name());
                                    userStatus.setProfileImage(userObject.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String,Object> obj=new HashMap<>();

                                    obj.put("name",userStatus.getName());
                                    obj.put("profileImage",userStatus.getProfileImage());
                                    obj.put("lastUpdated",userStatus.getLastUpdated());

                                    String imageUrl=uri.toString();
                                    Status status=new Status(imageUrl,userStatus.getLastUpdated());


                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);
                                    dialogUploadImage.dismiss();

                                }
                            });
                        }

                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.groups:
                break;
            case R.id.invite:
                break;
            case R.id.settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_message,menu);
        return super.onCreateOptionsMenu(menu);
    }
}