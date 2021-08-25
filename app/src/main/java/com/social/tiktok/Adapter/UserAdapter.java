package com.social.tiktok.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.tiktok.ChatActivity;
import com.social.tiktok.HomeActivity;
import com.social.tiktok.Models.UserObject;
import com.social.tiktok.R;
import com.social.tiktok.databinding.LayoutRowConversationBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    Context context;
    ArrayList<UserObject> userObjects;
    String lastMsg;
    long time;
    public UserAdapter(Context context,ArrayList<UserObject> userObjects)
    {
        this.context=context;
        this.userObjects=userObjects;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_row_conversation,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserObject userObject= userObjects.get(position);

        String senderId= FirebaseAuth.getInstance().getUid();
        String senderRoom=senderId+userObject.getUser_id();

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            time = snapshot.child("lastMsgTime").getValue(Long.class);
                            if((int)((new Date().getTime()-time))/60000 < 59) {
                                holder.binding.textContent.setText(lastMsg + " - " + (int) ((new Date().getTime() - time)) / 60000 + " phút trước");
                            }
                            if((int)((new Date().getTime()-time))/60000 > 60)
                            {
                                holder.binding.textContent.setText(lastMsg + " - " + ((int) ((new Date().getTime() - time)) / 60000)/60 + " giờ trước");
                            }
                        }
                        else
                        {
                            holder.binding.textContent.setText("Nhấp để trò chuyện");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.textNameChat.setText(userObject.getUser_name());
        Glide.with(context).load(userObject.getProfileImage()).into(holder.binding.imageAvatarChat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("name",userObject.getUser_name());
                intent.putExtra("uid",userObject.getUser_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userObjects.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        LayoutRowConversationBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=LayoutRowConversationBinding.bind(itemView);
        }
    }
}
