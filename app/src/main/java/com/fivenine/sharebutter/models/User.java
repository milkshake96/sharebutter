package com.fivenine.sharebutter.models;

public class User {

    private String user_id;
    private String username;
    private String profilePhoto;

    public User(String user_id, String username, String profilePhoto) {
        this.user_id = user_id;
        this.username = username;
        this.profilePhoto = profilePhoto;
    }

    public User() {

    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
