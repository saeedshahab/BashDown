package com.saeedshahab.bashdown.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saeedshahab.bashdown.annotations.CommonName;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@CommonName("bash")
public class Bash {

    private String id;

    @NotNull
    @Size(min = 5, max = 32)
    private String title;

    @Size(max = 256)
    private String description;

    @NotNull
    private Date dateTime;

    private String image;
    private String imageTitle;
    private String imageDescription;

    private Boolean active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Bash{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateTime=" + dateTime +
                ", image='" + image + '\'' +
                ", imageTitle='" + imageTitle + '\'' +
                ", imageDescription='" + imageDescription + '\'' +
                ", active=" + active +
                '}';
    }
}
