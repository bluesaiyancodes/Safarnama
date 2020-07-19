package com.strongties.safarnama.user_classes;

import com.google.firebase.firestore.GeoPoint;

public class Landmark {


    private String name;
    private String id;
    private String state;
    private String district;
    private String city;
    private GeoPoint geo_point;
    private String category;
    private String fee;
    private String hours;
    private String short_desc;
    private String long_desc;
    private String history;
    private String img_url;
    private String img_all_url;


    public Landmark() {
    }

    public Landmark(String name, String id, String state, String district, String city, GeoPoint geo_point, String type, String fee, String hours, String short_desc, String long_desc, String history, String img_url, String img_all_url) {
        this.name = name;
        this.id = id;
        this.state = state;
        this.district = district;
        this.city = city;
        this.geo_point = geo_point;
        this.category = type;
        this.fee = fee;
        this.hours = hours;
        this.short_desc = short_desc;
        this.long_desc = long_desc;
        this.history = history;
        this.img_url = img_url;
        this.img_all_url = img_all_url;
    }



    //Setters and Getters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getLong_desc() {
        return long_desc;
    }

    public void setLong_desc(String long_desc) {
        this.long_desc = long_desc;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getImg_all_url() {
        return img_all_url;
    }

    public void setImg_all_url(String img_all_url) {
        this.img_all_url = img_all_url;
    }


    @Override
    public String toString() {
        return "Landmark{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", geo_point=" + geo_point +
                ", category='" + category + '\'' +
                ", fee='" + fee + '\'' +
                ", hours='" + hours + '\'' +
                ", short_desc='" + short_desc + '\'' +
                ", long_desc='" + long_desc + '\'' +
                ", history='" + history + '\'' +
                ", img_url='" + img_url + '\'' +
                ", img_all_url='" + img_all_url + '\'' +
                '}';
    }
}
