package com.strongties.safarnama.user_classes;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class LandmarkList {
    private LandmarkMeta landmarkMeta;
    private @ServerTimestamp
    Date timestamp;

    public LandmarkList() {
    }

    public LandmarkList(LandmarkMeta landmarkMeta, Date timestamp) {
        this.landmarkMeta = landmarkMeta;
        this.timestamp = timestamp;
    }

    public LandmarkMeta getLandmarkMeta() {
        return landmarkMeta;
    }

    public void setLandmarkMeta(LandmarkMeta landmarkMeta) {
        this.landmarkMeta = landmarkMeta;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LandmarkList{" +
                "landmarkMeta=" + landmarkMeta +
                ", timestamp=" + timestamp +
                '}';
    }
}
