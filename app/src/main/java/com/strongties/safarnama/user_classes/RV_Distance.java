package com.strongties.safarnama.user_classes;

public class RV_Distance {
    private String id;
    private String name;
    private String img_url;
    private String category;
    private String state;
    private String city;
    private Double distance;

    public RV_Distance() {
    }

    public RV_Distance(String id, String name, String img_url, String category, String state, String city, Double distance) {
        this.id = id;
        this.name = name;
        this.img_url = img_url;
        this.category = category;
        this.state = state;
        this.city = city;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "RV_Distance{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", img_url='" + img_url + '\'' +
                ", category='" + category + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", distance=" + distance +
                '}';
    }
}
