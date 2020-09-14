package com.strongties.safarnama.user_classes;

import com.google.firebase.firestore.GeoPoint;

public class LandmarkMeta {

    private String id;
    private String state;
    private String district;
    private GeoPoint geoPoint;
    private Landmark landmark;

    public LandmarkMeta() {
    }

    public LandmarkMeta(String id, String state, String district, GeoPoint geoPoint, Landmark landmark) {
        this.id = id;
        this.state = state;
        this.district = district;
        this.geoPoint = geoPoint;
        this.landmark = landmark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getdistrict() {
        return district;
    }

    public void setdistrict(String district) {
        this.district = district;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    @Override
    public String toString() {
        return "LandmarkMeta{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", district='" + district + '\'' +
                ", geoPoint=" + geoPoint + '\'' +
                ", landmark=" + landmark +
                '}';
    }
}
