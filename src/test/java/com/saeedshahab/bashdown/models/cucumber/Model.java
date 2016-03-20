package com.saeedshahab.bashdown.models.cucumber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saeedshahab.bashdown.annotations.CommonName;

@JsonIgnoreProperties(ignoreUnknown = true)
@CommonName("model")
public class Model {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id='" + id + '\'' +
                '}';
    }
}
