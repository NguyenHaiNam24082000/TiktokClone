package com.social.tiktok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.social.tiktok.Models.UserObject;
import com.social.tiktok.databinding.ActivityHomeBinding;
import com.social.tiktok.databinding.ActivityRegisterBinding;
import com.social.tiktok.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.recyclerVideo.setLayoutManager(new GridLayoutManager(this,3));
        binding.recyclerVideo.setHasFixedSize(true);
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        StorageReference reference=storage.getReference().child("Profiles").child(auth.getUid());
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(UserProfileActivity.this).load(uri.toString()).into(binding.circleImageView);
                Glide.with(UserProfileActivity.this).load(uri.toString()).into(binding.imageUser);
            }
        });
        database= FirebaseDatabase.getInstance();
        DatabaseReference ref= database.getReference();
        ref.child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.textName.setText(" @"+snapshot.getValue(UserObject.class).getUser_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserProfileActivity.this,SetupProfileActivity.class);
                startActivity(intent);

            }
        });
        binding.imageDashBoard.setColorFilter(Color.RED);
        binding.imageDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imageDashBoard.setColorFilter(Color.RED);
                binding.imageViewLove.setColorFilter(Color.BLACK);
                binding.imageViewPrivate.setColorFilter(Color.BLACK);
            }
        });
        binding.imageViewLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imageDashBoard.setColorFilter(Color.BLACK);
                binding.imageViewLove.setColorFilter(Color.RED);
                binding.imageViewPrivate.setColorFilter(Color.BLACK);
            }
        });
        binding.imageViewPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imageDashBoard.setColorFilter(Color.BLACK);
                binding.imageViewLove.setColorFilter(Color.BLACK);
                binding.imageViewPrivate.setColorFilter(Color.RED);
            }
        });
    }

    public void searchBtn(View view) {
    }

    public void addBtn(View view) {
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();

    }

    public void gobackhome(View view) {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}