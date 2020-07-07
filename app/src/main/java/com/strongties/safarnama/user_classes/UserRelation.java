package com.strongties.safarnama.user_classes;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserRelation {
    private User user;
    private String relation;
    private @ServerTimestamp
    Date timestamp;

    public UserRelation(User user, String relation, Date timestamp) {
        this.user = user;
        this.relation = relation;
        this.timestamp = timestamp;
    }

    public UserRelation() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                "user=" + user +
                ", relation=" + relation +
                ", timestamp=" + timestamp +
                '}';
    }



}
