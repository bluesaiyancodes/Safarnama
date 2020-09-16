package com.strongties.safarnama.user_classes;

import android.os.Parcel;
import android.os.Parcelable;

public class RV_Distance implements Parcelable {
    private String id;
    private String name;
    private String img_url;
    private String category;
    private String state;
    public static final Creator<RV_Distance> CREATOR = new Creator<RV_Distance>() {
        @Override
        public RV_Distance createFromParcel(Parcel in) {
            return new RV_Distance(in);
        }

        @Override
        public RV_Distance[] newArray(int size) {
            return new RV_Distance[size];
        }
    };
    private String city;
    private Double distance;
    private String district;

    public RV_Distance() {
    }

    private String meta;

    public RV_Distance(String id, String name, String img_url, String category, String state, String district, String city, Double distance, String meta) {
        this.id = id;
        this.name = name;
        this.img_url = img_url;
        this.category = category;
        this.state = state;
        this.district = district;
        this.city = city;
        this.distance = distance;
        this.meta = meta;
    }

    protected RV_Distance(Parcel in) {
        id = in.readString();
        name = in.readString();
        img_url = in.readString();
        category = in.readString();
        state = in.readString();
        city = in.readString();
        district = in.readString();
        if (in.readByte() == 0) {
            distance = null;
        } else {
            distance = in.readDouble();
        }
        meta = in.readString();
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "RV_Distance{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", img_url='" + img_url + '\'' +
                ", category='" + category + '\'' +
                ", state='" + state + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", distance=" + distance +
                ", meta='" + meta + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(img_url);
        parcel.writeString(category);
        parcel.writeString(state);
        parcel.writeString(district);
        parcel.writeString(city);
        if (distance == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(distance);
        }
        parcel.writeString(meta);
    }
}
