package com.strongties.safarnama.user_classes;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class RV_Inspiration {
    private String content_id;
    private String content_type;
    private String content_url;
    private String content_description;
    private String content_location;
    private @ServerTimestamp
    Date timestamp;
    private int like_count;

    public RV_Inspiration() {
    }

    public RV_Inspiration(String content_id, String content_type, String content_url, String content_description, String content_location, Date timestamp, int like_count) {
        this.content_id = content_id;
        this.content_type = content_type;
        this.content_url = content_url;
        this.content_description = content_description;
        this.content_location = content_location;
        this.timestamp = timestamp;
        this.like_count = like_count;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public String getContent_description() {
        return content_description;
    }

    public void setContent_description(String content_description) {
        this.content_description = content_description;
    }

    public String getContent_location() {
        return content_location;
    }

    public void setContent_location(String content_location) {
        this.content_location = content_location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    @Override
    public String toString() {
        return "RV_Inspiration{" +
                "content_id='" + content_id + '\'' +
                ", content_type='" + content_type + '\'' +
                ", content_url='" + content_url + '\'' +
                ", content_description='" + content_description + '\'' +
                ", content_location='" + content_location + '\'' +
                ", timestamp=" + timestamp +
                ", like_count=" + like_count +
                '}';
    }
}
