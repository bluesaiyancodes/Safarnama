package com.strongties.safarnama.user_classes;

public class RV_Journey_Tour {
    private String name;
    private String id;
    private String duration;
    private String img_url;
    private String description;

    public RV_Journey_Tour() {
    }

    public RV_Journey_Tour(String name, String id, String duration, String img_url, String description) {
        this.name = name;
        this.id = id;
        this.duration = duration;
        this.img_url = img_url;
        this.description = description;
    }

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RV_Journey_Tour{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", duration='" + duration + '\'' +
                ", img_url='" + img_url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
