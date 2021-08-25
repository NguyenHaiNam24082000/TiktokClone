package com.social.tiktok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
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
import com.social.tiktok.Adapter.MessagesAdapter;
import com.social.tiktok.Models.Message;
import com.social.tiktok.Models.UserObject;
import com.social.tiktok.databinding.ActivityChatBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.log.JitsiMeetDefaultLogHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    URL serverURL;
    String senderRoom, recvRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderUid;
    String receiverUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        messages=new ArrayList<>();
        dialog=new ProgressDialog(this);
        dialog.setMessage("Uploading image...");



        String name=getIntent().getStringExtra("name");
        receiverUid=getIntent().getStringExtra("uid");
        senderUid= FirebaseAuth.getInstance().getUid();

        senderRoom=senderUid+receiverUid;
        recvRoom = receiverUid + senderUid;
        adapter=new MessagesAdapter(this,messages,senderRoom,recvRoom);
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();


        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerChat.setAdapter(adapter);
        database.getReference().child("users").child(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.textChatName.setText(snapshot.getValue(UserObject.class).getUser_name());
                Glide.with(ChatActivity.this).load(snapshot.getValue(UserObject.class).getProfileImage()).into(binding.imageAvatarRecv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    Log.d("tag",ds+"");
                    Message message=ds.getValue(Message.class);
                    message.setMessageId(ds.getKey());
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,25);
            }
        });
        binding.videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getUid().equals(senderUid)) {
                    String messageTxt="video call start in "+senderRoom;
                    Date date=new Date();
                    Message message=new Message(messageTxt,senderUid,date.getTime());
                    binding.messageBox.setText("");
                    String randomKey=database.getReference().push().getKey();
                    HashMap<String, Object> lastMsgObj=new HashMap<>();
                    lastMsgObj.put("lastMsg","video call");
                    lastMsgObj.put("lastMsgTime",date.getTime());
                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                    database.getReference().child("chats").child(recvRoom).updateChildren(lastMsgObj);
                    database.getReference().child("chats").child(senderRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats").child(recvRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                            HashMap<String, Object> lastMsgObj=new HashMap<>();
                            lastMsgObj.put("lastMsg",message.getMessage());
                            lastMsgObj.put("lastMsgTime",date.getTime());
                            database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                            database.getReference().child("chats").child(recvRoom).updateChildren(lastMsgObj);

                        }
                    });
                }
            }
        });
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt=binding.messageBox.getText().toString();
                Date date=new Date();
                Message message=new Message(messageTxt,senderUid,date.getTime());

                binding.messageBox.setText("");
                String randomKey=database.getReference().push().getKey();
                HashMap<String, Object> lastMsgObj=new HashMap<>();
                lastMsgObj.put("lastMsg",message.getMessage());
                lastMsgObj.put("lastMsgTime",date.getTime());
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(recvRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats").child(senderRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats").child(recvRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                        HashMap<String, Object> lastMsgObj=new HashMap<>();
                        lastMsgObj.put("lastMsg",message.getMessage());
                        lastMsgObj.put("lastMsgTime",date.getTime());
                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                        database.getReference().child("chats").child(recvRoom).updateChildren(lastMsgObj);

                    }
                });
            }

        });

//        getSupportActionBar().show();
//        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==25)
        {
            if(data!=null)
            {
                if(data.getData()!=null)
                {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference=storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful())
                            {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath=uri.toString();
                                        String messageTxt=binding.messageBox.getText().toString();
                                        Date date=new Date();
                                        Message message=new Message(messageTxt,senderUid,date.getTime());
                                        message.setMessage("Photo");
                                        message.setImageUrl(filePath);
                                        binding.messageBox.setText("");
                                        String randomKey=database.getReference().push().getKey();
                                        HashMap<String, Object> lastMsgObj=new HashMap<>();
                                        lastMsgObj.put("lastMsg",message.getMessage());
                                        lastMsgObj.put("lastMsgTime",date.getTime());
                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(recvRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats").child(senderRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database.getReference().child("chats").child(recvRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                                HashMap<String, Object> lastMsgObj=new HashMap<>();
                                                lastMsgObj.put("lastMsg",message.getMessage());
                                                lastMsgObj.put("lastMsgTime",date.getTime());
                                                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                                database.getReference().child("chats").child(recvRoom).updateChildren(lastMsgObj);

                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }


    public void returnHome(View view) {
        Intent intent=new Intent(this,MessageActivity.class);
        startActivity(intent);
        finish();
    }
}