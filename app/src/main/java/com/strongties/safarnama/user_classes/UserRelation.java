package com.strongties.safarnama.user_classes;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserRelation {
    private String user_id;
    private String relation;
    private @ServerTimestamp
    Date timestamp;

    public UserRelation(String user_id, String relation, Date timestamp) {
        this.user_id = user_id;
        this.relation = relation;
        this.timestamp = timestamp;
    }

    public UserRelation() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String  getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "user_id=" + user_id +
                ", relation=" + relation +
                ", timestamp=" + timestamp +
                '}';
    }



}
