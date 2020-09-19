package com.strongties.safarnama.user_classes;

public class FirebaseToken {
    String token;

    public FirebaseToken(String token) {
        this.token = token;
    }

    public FirebaseToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "FirebaseToken{" +
                "token='" + token + '\'' +
                '}';
    }
}
