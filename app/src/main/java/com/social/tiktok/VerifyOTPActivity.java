package com.social.tiktok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.social.tiktok.databinding.ActivityVerifyOTPBinding;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    ActivityVerifyOTPBinding binding;
    FirebaseAuth auth;
    String verificationId;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityVerifyOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog=new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        dialog.show();


        auth=FirebaseAuth.getInstance();

        String phoneNumber=getIntent().getStringExtra("phoneNumber");
        String phoneNumberLogin=getIntent().getStringExtra("phoneNumberLogin");
        if(phoneNumber!=null) {
            binding.textVerifyPhoneNumber.setText("Xác nhận qua số điện thoại " + phoneNumber);
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(VerifyOTPActivity.this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {

                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            dialog.dismiss();
                            verificationId = s;
                        }
                    }).build();
            PhoneAuthProvider.verifyPhoneNumber(options);

            binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
                @Override
                public void onOtpCompleted(String otp) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(VerifyOTPActivity.this, SetupProfileActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                Toast.makeText(VerifyOTPActivity.this, "Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }
        else
        {
            binding.textVerifyPhoneNumber.setText("Xác nhận qua số điện thoại " + phoneNumberLogin);
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumberLogin)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(VerifyOTPActivity.this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {

                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            dialog.dismiss();
                            verificationId = s;
                        }
                    }).build();
            PhoneAuthProvider.verifyPhoneNumber(options);

            binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
                @Override
                public void onOtpCompleted(String otp) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(VerifyOTPActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            } else {
                                Toast.makeText(VerifyOTPActivity.this, "Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }
    }
}