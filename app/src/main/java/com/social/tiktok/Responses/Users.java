package com.social.tiktok.Responses;

import com.google.gson.annotations.SerializedName;
import com.social.tiktok.Models.MediaObject;

import java.util.List;

public class Users {
    @SerializedName("ALL_POSTS")
    private List<MediaObject> AllPosts;
    public List<MediaObject> getAllPosts()
    {
        return AllPosts;
    }

}
