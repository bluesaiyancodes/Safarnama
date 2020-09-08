package com.strongties.safarnama.user_classes;

public class RV_DistanceSearch {

    private String id;
    private String Placename;
    private String Location;
    private String District;
    private String type;
    private String photoUrl;
    private String DistanceText;
    private Double Distance;


    public RV_DistanceSearch() {
    }

    public RV_DistanceSearch(String id, String placename, String location, String district, String type, String photoUrl, String distanceText, Double distance) {
        this.id = id;
        Placename = placename;
        Location = location;
        District = district;
        this.type = type;
        this.photoUrl = photoUrl;
        DistanceText = distanceText;
        Distance = distance;
    }

    //Getter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlacename() {
        return Placename;
    }

    public void setPlacename(String placename) {
        Placename = placename;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    //Setter

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photo) {
        photoUrl = photo;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double distance) {
        Distance = distance;
    }

    public String getDistanceText() {
        return DistanceText;
    }

    public void setDistanceText(String distanceText) {
        DistanceText = distanceText;
    }

}
