package com.strongties.safarnama.user_classes;

public class RV_Explore {

    private  int id;
    private String Placename;
    private String Location;
    private String State;
    private String type;
    private String Description;
    private String photoUrl;
    private String visit;


    public RV_Explore(String s, String puri, String odisha, int i) {
    }

    public RV_Explore(int id, String placename, String location, String state, String description, String _type, String url, String visit ) {
        Placename = placename;
        Location = location;
        State = state;
        Description =description;
        type = _type;
        photoUrl = url;
        this.id = id;
        this.visit = visit;
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

    public String getVisit() {
        return visit;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }




    //Setter


    public void setId(int id) {
        this.id = id;
    }

    public void setVisit(String visit) {
        this.visit = visit;
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
}
