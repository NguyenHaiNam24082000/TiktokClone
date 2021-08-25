package com.social.tiktok;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageFullActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);

        String imagePath=getIntent().getStringExtra("path");
        String imageName=getIntent().getStringExtra("name");

        Glide.with(this).load(imagePath).into((ImageView) findViewById(R.id.imageViewFull));
    }
}