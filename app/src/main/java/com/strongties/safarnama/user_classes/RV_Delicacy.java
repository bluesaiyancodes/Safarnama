package com.strongties.safarnama.user_classes;

public class RV_Delicacy {
    private String id;
    private String name;
    private String pronunciation;
    private String famous_in;
    private String price;
    private String category;
    private String foodtype;
    private String description;
    private String preparation;
    private String img_header;
    private String img_carousel;
    private String img_desciption;
    private String video_link;

    public RV_Delicacy() {
    }

    public RV_Delicacy(String id, String name, String pronunciation, String famous_in, String price, String category, String foodtype, String description, String preparation, String img_header, String img_carousel, String img_desciption, String video_link) {
        this.id = id;
        this.name = name;
        this.pronunciation = pronunciation;
        this.famous_in = famous_in;
        this.price = price;
        this.category = category;
        this.foodtype = foodtype;
        this.description = description;
        this.preparation = preparation;
        this.img_header = img_header;
        this.img_carousel = img_carousel;
        this.img_desciption = img_desciption;
        this.video_link = video_link;
    }

//Getters and Setters

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

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getFamous_in() {
        return famous_in;
    }

    public void setFamous_in(String famous_in) {
        this.famous_in = famous_in;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public String getImg_header() {
        return img_header;
    }

    public void setImg_header(String img_header) {
        this.img_header = img_header;
    }

    public String getImg_carousel() {
        return img_carousel;
    }

    public void setImg_carousel(String img_carousel) {
        this.img_carousel = img_carousel;
    }

    public String getImg_desciption() {
        return img_desciption;
    }

    public void setImg_desciption(String img_desciption) {
        this.img_desciption = img_desciption;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public String getFoodtype() {
        return foodtype;
    }

    public void setFoodtype(String foodtype) {
        this.foodtype = foodtype;
    }

    //String
    @Override
    public String toString() {
        return "RV_Delicacy{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pronunciation='" + pronunciation + '\'' +
                ", famous_in='" + famous_in + '\'' +
                ", price='" + price + '\'' +
                ", category='" + category + '\'' +
                ", foodtype='" + foodtype + '\'' +
                ", description='" + description + '\'' +
                ", preparation='" + preparation + '\'' +
                ", img_header='" + img_header + '\'' +
                ", img_carousel='" + img_carousel + '\'' +
                ", img_desciption='" + img_desciption + '\'' +
                ", video_link='" + video_link + '\'' +
                '}';
    }
}
