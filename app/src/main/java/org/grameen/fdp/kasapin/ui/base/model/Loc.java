package org.grameen.fdp.kasapin.ui.base.model;

/**
 * Created by aangjnr on 19/09/2017.
 */


import androidx.room.Ignore;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Loc {
    Integer id;

    String plotId;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("lonngitude")
    private String longitude;


    public Loc() {
    }

    @Ignore
    public Loc(String plotId, String lat, String lon) {
        this.latitude = lat;
        this.longitude = lon;
        this.plotId = plotId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPlotId() {
        return plotId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Ignore
    LatLng getLatLng() {
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

    }
}
