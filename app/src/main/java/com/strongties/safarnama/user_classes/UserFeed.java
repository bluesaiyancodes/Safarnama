package com.strongties.safarnama.user_classes;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserFeed {
    private String user_id;
    private String datacontent;
    private String type;
    private Boolean imgIncluded;
    private String imgUrl;
    private Boolean landmarkIncluded;
    private String landmarkId;
    private @ServerTimestamp
    Date timestamp;

    public UserFeed(String user_id, String datacontent, Date timestamp, String type, Boolean imgIncluded, String imgUrl, Boolean landmarkIncluded, String landmarkId) {
        this.user_id = user_id;
        this.datacontent = datacontent;
        this.timestamp = timestamp;
        this.type = type;
        this.imgIncluded = imgIncluded;
        this.imgUrl = imgUrl;
        this.landmarkIncluded = landmarkIncluded;
        this.landmarkId = landmarkId;
    }

    public UserFeed() {

    }


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getLandmarkIncluded() {
        return landmarkIncluded;
    }

    public void setLandmarkIncluded(Boolean landmarkIncluded) {
        this.landmarkIncluded = landmarkIncluded;
    }

    public String getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(String landmarkId) {
        this.landmarkId = landmarkId;
    }

    public Boolean getImgIncluded() {
        return imgIncluded;
    }

    public void setImgIncluded(Boolean imgIncluded) {
        this.imgIncluded = imgIncluded;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "UserFeed{" +
                "user_id='" + user_id + '\'' +
                ", datacontent='" + datacontent + '\'' +
                ", type='" + type + '\'' +
                ", imgIncluded=" + imgIncluded +
                ", imgUrl='" + imgUrl + '\'' +
                ", landmarkIncluded=" + landmarkIncluded +
                ", landmarkId='" + landmarkId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }


}
