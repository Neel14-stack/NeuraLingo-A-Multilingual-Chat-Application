package com.invincible.neuralingo.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileModel implements Serializable {
    @SerializedName("name")
    String name;
    @SerializedName("image")
    String image;
    @SerializedName("about")
    String about;

    public ProfileModel()
    {
        name = "";
        image = "";
        about = "";
    }

    public ProfileModel(String name, String about, String image)
    {
        this.about = about;
        this.name = name;
        this.image = image;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setImage(String imageStr) {
        this.image = imageStr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
