package com.fivenine.sharebutter.models;

public class Item {
    public static final String CATEGORIES[] = {
            "Dried & canned food",
            "Fresh & Frozen",
            "Beverages"
    };

    private Long id;
    private String name;
    private String description;
    private String hashTag;
    private String category;
    private String city;
    private String state;
    private String expiredDate;
    private String img1URL;
    private String img2URL;
    private String img3URL;

    public Item() {

    }

    public Item(Long id, String name, String description, String hashTag,
                String category, String city, String state, String expiredDate){

        this.id = id;
        this.name = name;
        this.description = description;
        this.hashTag = hashTag;
        this.category = category;
        this.city = city;
        this.state = state;
        this.expiredDate = expiredDate;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHashTag() {
        return hashTag;
    }

    public String getCategory() {
        return category;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImg1URL() {
        return img1URL;
    }

    public void setImg1URL(String img1URL) {
        this.img1URL = img1URL;
    }

    public String getImg2URL() {
        return img2URL;
    }

    public void setImg2URL(String img2URL) {
        this.img2URL = img2URL;
    }

    public String getImg3URL() {
        return img3URL;
    }

    public void setImg3URL(String img3URL) {
        this.img3URL = img3URL;
    }
}
