package com.strongties.safarnama;

import android.app.Application;

import com.strongties.safarnama.user_classes.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
