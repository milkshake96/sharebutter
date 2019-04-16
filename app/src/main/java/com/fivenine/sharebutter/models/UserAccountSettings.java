package com.fivenine.sharebutter.models;


public class UserAccountSettings {

    private String description;
    private String display_name;
    private long followers;
    private long likes;
    private long offers;
    private String profile_photo;
    private String username;
    private String state;

    public UserAccountSettings(String description, String display_name, long followers, long likes,
                               long offers, String profile_photo, String state, String username) {
        this.description = description;
        this.display_name = display_name;
        this.followers = followers;
        this.likes = likes;
        this.offers = offers;
        this.profile_photo = profile_photo;
        this.state = state;
        this.username = username;
    }

    public UserAccountSettings() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getOffers() {
        return offers;
    }

    public void setOffers(long offers) {
        this.offers = offers;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +
                ", followers=" + followers +
                ", following=" + likes +
                ", posts=" + offers +
                ", profile_photo='" + profile_photo + '\'' +
                ", state='" + state + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
