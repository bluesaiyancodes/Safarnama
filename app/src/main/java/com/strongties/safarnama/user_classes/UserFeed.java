package com.strongties.safarnama.user_classes;

import com.google.firebase.firestore.ServerTimestamp;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class UserFeed {
    private String user_id;
    private String datacontent;
    private @ServerTimestamp
    Date timestamp;

    public UserFeed(String user_id, String datacontent, Date timestamp) {
        this.user_id = user_id;
        this.datacontent = datacontent;
        this.timestamp = timestamp;
    }

    public UserFeed() {

    }


    // Getters and Setters


    public String getDatacontent() {
        return datacontent;
    }

    public void setDatacontent(String datacontent) {
        this.datacontent = datacontent;
    }

    public String  getUser_id() {
        return user_id;
    }

    public void setUser_id(String  user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @NotNull
    @Override
    public String toString() {
        return "UserLocation{" +
                "user_id=" + user_id +
                ", dataContent=" + datacontent +
                ", timestamp=" + timestamp +
                '}';
    }



}
