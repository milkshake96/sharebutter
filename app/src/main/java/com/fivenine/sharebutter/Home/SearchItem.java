package com.fivenine.sharebutter.Home;

public class SearchItem {

    public String image, name, hashtag;

    public SearchItem(){

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public SearchItem(String image, String name, String hashtag) {
        this.image = image;
        this.name = name;
        this.hashtag = hashtag;
    }
}
