package com.social.tiktok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import com.social.tiktok.Adapter.GalleryAdapter;
import com.social.tiktok.Models.ImageGallery;

import java.util.ArrayList;
import java.util.List;

public class StoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    ArrayList<ImageGallery> images;

    private static final int MY_READ_PERMISSION_CODE= 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        recyclerView = findViewById(R.id.recyclerGallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setHasFixedSize(true);
        if(ContextCompat.checkSelfPermission(StoryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(StoryActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_READ_PERMISSION_CODE);
        }
        images=new ArrayList<>();
        if(images.isEmpty())
        {
            images=getAllImages();
            recyclerView.setAdapter(new GalleryAdapter(this,images));
        }
    }

    private ArrayList<ImageGallery> getAllImages() {
        Uri allImageUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.Images.ImageColumns.DATA,MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor= this.getContentResolver().query(allImageUri,projection,null,null,null);
        cursor.moveToFirst();
        try {
            do {
                ImageGallery image = new ImageGallery();
                image.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                image.setImageName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                images.add(image);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return images;
    }


//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode==MY_READ_PERMISSION_CODE)
//        {
//            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
//            {
//                Toast.makeText(this,"Read external storage permission granted",Toast.LENGTH_LONG).show();
//                loadImages();
//            }
//            else
//            {
//                Toast.makeText(this,"Read external storage permission",Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}