package com.strongties.safarnama.user_classes;

public class RV_Bucketlist {

    private  int id;
    private String Placename;
    private String Location;
    private String State;
    private String type;
    private String Description;
    private String photoUrl;
    private String datecreated;


    public RV_Bucketlist(String s, String puri, String odisha, int i) {
    }

    public RV_Bucketlist(int id, String placename, String location, String state, String description, String _type, String url, String datecreated ) {
        Placename = placename;
        Location = location;
        State = state;
        Description =description;
        type = _type;
        photoUrl = url;
        this.id = id;
        this.datecreated = datecreated;
    }



    //Getter
    public int getId() {
        return id;
    }

    public String getPlacename() {
        return Placename;
    }

    public String getLocation() {
        return Location;
    }

    public String getState() {
        return State;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return Description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getDatecreated() {
        return datecreated;
    }



//Setter


    public void setId(int id) {
        this.id = id;
    }

    public void setPlacename(String placename) {
        Placename = placename;
    }

    public void setPhotoUrl(String photo) {
        photoUrl = photo;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setState(String state) {
        State = state;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }
}
