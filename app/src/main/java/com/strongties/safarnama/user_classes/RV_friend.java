package com.strongties.safarnama.user_classes;

public class RV_friend {

    private String name;
    private String email;
    private String added_on;
    private String photoUrl;
    private UserRelation userRelation;

    public RV_friend(String name, String email, String added_on, String url, UserRelation userRelation) {
        this.name = name;
        this.email = email;
        photoUrl = url;
        this.added_on = added_on;
        this.userRelation = userRelation;
    }



    //Getters and Setters


    public UserRelation getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(UserRelation userRelation) {
        this.userRelation = userRelation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdded_on() {
        return added_on;
    }

    public void setAdded_on(String added_on) {
        this.added_on = added_on;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
