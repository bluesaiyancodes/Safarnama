package com.strongties.safarnama.user_classes;

public class RV_Journey_Location {
    private String name;
    private String id;
    private String place_pos_type;
    private String place_pos;
    private String distance_origin;
    private String distance_previous;
    private String visiting_hours;
    private String entry_fee;
    private String duration;
    private String famous_for;
    private String image;


    public RV_Journey_Location() {
    }

    public RV_Journey_Location(String name, String id, String place_pos_type, String place_pos, String distance_origin, String distance_previous, String visiting_hours, String entry_fee, String duration, String famous_for, String image) {
        this.name = name;
        this.id = id;
        this.place_pos_type = place_pos_type;
        this.place_pos = place_pos;
        this.distance_origin = distance_origin;
        this.distance_previous = distance_previous;
        this.visiting_hours = visiting_hours;
        this.entry_fee = entry_fee;
        this.duration = duration;
        this.famous_for = famous_for;
        this.image = image;
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

    public String getPlace_pos_type() {
        return place_pos_type;
    }

    public void setPlace_pos_type(String place_pos_type) {
        this.place_pos_type = place_pos_type;
    }

    public String getPlace_pos() {
        return place_pos;
    }

    public void setPlace_pos(String place_pos) {
        this.place_pos = place_pos;
    }

    public String getDistance_origin() {
        return distance_origin;
    }

    public void setDistance_origin(String distance_origin) {
        this.distance_origin = distance_origin;
    }

    public String getDistance_previous() {
        return distance_previous;
    }

    public void setDistance_previous(String distance_previous) {
        this.distance_previous = distance_previous;
    }

    public String getVisiting_hours() {
        return visiting_hours;
    }

    public void setVisiting_hours(String visiting_hours) {
        this.visiting_hours = visiting_hours;
    }

    public String getEntry_fee() {
        return entry_fee;
    }

    public void setEntry_fee(String entry_fee) {
        this.entry_fee = entry_fee;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFamous_for() {
        return famous_for;
    }

    public void setFamous_for(String famous_for) {
        this.famous_for = famous_for;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "RV_Journey_Location{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", place_pos_type='" + place_pos_type + '\'' +
                ", place_pos='" + place_pos + '\'' +
                ", distance_origin='" + distance_origin + '\'' +
                ", distance_previous='" + distance_previous + '\'' +
                ", visiting_hours='" + visiting_hours + '\'' +
                ", entry_fee='" + entry_fee + '\'' +
                ", duration='" + duration + '\'' +
                ", famous_for='" + famous_for + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
