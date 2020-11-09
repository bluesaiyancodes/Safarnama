package com.strongties.safarnama.user_classes;

public class UserShort {

    private String email;
    private String user_id;

    public UserShort() {
    }

    public UserShort(String email, String user_id) {
        this.email = email;
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UserShort{" +
                "email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}

