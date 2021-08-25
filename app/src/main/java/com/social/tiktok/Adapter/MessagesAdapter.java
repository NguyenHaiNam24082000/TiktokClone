package com.social.tiktok.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.tiktok.MessageActivity;
import com.social.tiktok.Models.Message;
import com.social.tiktok.Models.UserObject;
import com.social.tiktok.R;
import com.social.tiktok.databinding.ItemRecvBinding;
import com.social.tiktok.databinding.ItemSendBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT=1;
    final int ITEM_RECV=2;

    String senderRoom;
    String recvRoom;
    URL serverURL;
    public MessagesAdapter(Context context, ArrayList<Message> messages,String senderRoom,String recvRoom)
    {
        this.context=context;
        this.messages=messages;
        this.senderRoom=senderRoom;
        this.recvRoom=recvRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.item_send,parent,false);
            return new SentViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.item_recv,parent,false);
            return new RecvViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId()))
        {
            return ITEM_SENT;
        }
        else
        {
            return ITEM_RECV;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=messages.get(position);

        int reactions[] = new int[]
                {
                        R.drawable.ic_fb_like,
                        R.drawable.ic_fb_love,
                        R.drawable.ic_fb_laugh,
                        R.drawable.ic_fb_wow,
                        R.drawable.ic_fb_sad,
                        R.drawable.ic_fb_angry
                };

        int reactionsGIF[] = new int[]
                {
                        R.drawable.ic_like_fb,
                        R.drawable.ic_love_fb,
                        R.drawable.ic_haha_fb,
                        R.drawable.ic_wow_fb,
                        R.drawable.ic_sad_fb,
                        R.drawable.ic_angry_fb
                };
        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(options);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==SentViewHolder.class)
            {
                SentViewHolder viewHolder=(SentViewHolder)holder;
                if(pos>=0 && pos <reactions.length) {
                    Glide.with(context).load(reactionsGIF[pos]).into(viewHolder.binding.feeling);
                    //Glide.with(context).load(reactionsGIF[pos]).into(viewHolder.binding.feeling);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }


            }
            else
            {
                RecvViewHolder viewHolder=(RecvViewHolder) holder;
                if(pos>=0 && pos <reactions.length) {
                    viewHolder.binding.feeling.setImageResource(reactionsGIF[pos]);
                    //Glide.with(context).load(reactionsGIF[pos]).into(viewHolder.binding.feeling);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }


            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference().child("chats").child(recvRoom).child("messages")
                    .child(message.getMessageId()).setValue(message);


            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder.getClass() == SentViewHolder.class)
        {
            SentViewHolder viewHolder=(SentViewHolder)holder;
            viewHolder.binding.textSend.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });
            viewHolder.binding.imageChat.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
            if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
                reference.child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserObject user= snapshot.getValue(UserObject.class);
                        Glide.with(context).load(user.getProfileImage()).into(viewHolder.binding.circleImageViewSendUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            if(message.getMessage().toLowerCase().equals("photo"))
            {
                viewHolder.binding.imageChat.setVisibility(View.VISIBLE);
                viewHolder.binding.textSend.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.placeholder_image).into(viewHolder.binding.imageChat);
            }

            if(message.getMessage().toLowerCase().contains("video call start in "))
            {
                String room=message.getMessage().toLowerCase().split("video call start in ")[1];
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(room)
                        .setWelcomePageEnabled(false)
                        .build();
                JitsiMeetActivity.launch(context, options);
                HashMap<String, Object> lastMsgObj=new HashMap<>();
                lastMsgObj.put("lastMsg","video call");

                Date date=new Date();
                lastMsgObj.put("lastMsgTime",date.getTime());
                reference.child("chats").child(senderRoom).child("messages")
                        .child(message.getMessageId()).removeValue();
                reference.child("chats").child(recvRoom).child("messages")
                        .child(message.getMessageId()).removeValue();
                Message ms=new Message("video call",message.getSenderId(),new Date().getTime());
                ms.setFeeling(message.getFeeling());
                reference.child("chats").child(senderRoom).child("messages")
                        .child(message.getMessageId()).setValue(ms);
                reference.child("chats").child(recvRoom).child("messages")
                        .child(message.getMessageId()).setValue(ms);
                reference.child("chats").child(senderRoom).updateChildren(lastMsgObj);
                reference.child("chats").child(recvRoom).updateChildren(lastMsgObj);
            }

            viewHolder.binding.textSend.setText(message.getMessage());

            if(message.getFeeling()>=0) {
                viewHolder.binding.feeling.setImageResource(reactionsGIF[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

        }
        else
        {
            RecvViewHolder viewHolder=(RecvViewHolder) holder;
            viewHolder.binding.textRecv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });

            viewHolder.binding.imageChat.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

            if(!FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {

                reference.child("users").child(message.getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserObject user = snapshot.getValue(UserObject.class);
                        Glide.with(context).load(user.getProfileImage()).into(viewHolder.binding.circleImageViewRecvUser);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            if(message.getMessage().toLowerCase().equals("photo"))
            {
                viewHolder.binding.imageChat.setVisibility(View.VISIBLE);
                viewHolder.binding.textRecv.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.placeholder_image).into(viewHolder.binding.imageChat);
            }
            if(message.getMessage().toLowerCase().contains("video call start in "))
            {
                String room=message.getMessage().toLowerCase().split("video call start in ")[1];

                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(room)
                        .setWelcomePageEnabled(false)
                        .build();
                JitsiMeetActivity.launch(context, options);

                HashMap<String, Object> lastMsgObj=new HashMap<>();
                lastMsgObj.put("lastMsg","video call");

                Date date=new Date();
                lastMsgObj.put("lastMsgTime",date.getTime());
                reference.child("chats").child(senderRoom).child("messages")
                        .child(message.getMessageId()).removeValue();
                reference.child("chats").child(recvRoom).child("messages")
                        .child(message.getMessageId()).removeValue();
                Message ms=new Message("video call",message.getSenderId(),new Date().getTime());
                ms.setFeeling(message.getFeeling());
                reference.child("chats").child(senderRoom).child("messages")
                        .child(message.getMessageId()).setValue(ms);
                reference.child("chats").child(recvRoom).child("messages")
                        .child(message.getMessageId()).setValue(ms);
                reference.child("chats").child(senderRoom).updateChildren(lastMsgObj);
                reference.child("chats").child(recvRoom).updateChildren(lastMsgObj);
            }

            viewHolder.binding.textRecv.setText(message.getMessage());
            if(message.getFeeling()>=0) {
                //message.setFeeling(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactionsGIF[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }


        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder
    {
        ItemSendBinding binding;
        public SentViewHolder(@NonNull View itemView)
        {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    public class RecvViewHolder extends RecyclerView.ViewHolder
    {

        ItemRecvBinding binding;
        public RecvViewHolder(@NonNull View itemView)
        {
            super(itemView);
            binding = ItemRecvBinding.bind(itemView);
        }
    }
}
