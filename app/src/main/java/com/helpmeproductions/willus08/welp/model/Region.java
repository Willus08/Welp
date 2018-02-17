
package com.helpmeproductions.willus08.welp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.helpmeproductions.willus08.welp.model.Center;

public class Region {

    @SerializedName("center")
    @Expose
    private Center center;

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

}
