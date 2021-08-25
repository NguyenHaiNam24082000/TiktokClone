package com.social.tiktok.Models;

public class UserObject {
    private String user_id;
    private String user_name;
    private String phoneNumber;
    private String email;
    private String profileImage;

    public UserObject() {
    }

    public UserObject(String user_id, String user_name, String phoneNumber, String email, String profileImage) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.phoneNumber = phoneNumber;
        this.email=email;
        this.profileImage = profileImage;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
