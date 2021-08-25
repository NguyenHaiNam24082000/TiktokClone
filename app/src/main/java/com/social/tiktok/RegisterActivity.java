package com.social.tiktok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.tiktok.Models.UserObject;
import com.social.tiktok.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        binding.textPhoneNumber.requestFocus();
        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = database.getReference();
                ref.child("users").child("phoneNumber").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            // use "username" already exists
//                            // Let the user know he needs to pick another username.
//                            binding.textPhoneNumber.setError("Tài khoản đã tồn tại");
//                            return;
//                        } else {
//                            // User does not exist. NOW call createUserWithEmailAndPassword
//                            Intent intent = new Intent(RegisterActivity.this, VerifyOTPActivity.class);
//                            intent.putExtra("phoneNumber", binding.textPhoneNumber.getText().toString());
//                            startActivity(intent);
//                            finish();
//                            // Your previous code here.
//                        }
                        Intent intent = new Intent(RegisterActivity.this, VerifyOTPActivity.class);
                        intent.putExtra("phoneNumber", binding.textPhoneNumber.getText().toString());
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void returnHome(View view) {
        Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}