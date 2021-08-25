package com.social.tiktok.Models;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class ImageGallery {
    String imagePath;
    String imageName;
    public ImageGallery(String imagePath,String imageName)
    {
        this.imageName=imageName;
        this.imagePath=imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public ImageGallery() {
    }
}
