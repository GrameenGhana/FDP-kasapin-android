package org.grameen.fdp.kasapin.data.db.entity;

import com.google.gson.annotations.SerializedName;

public class PlotGpsPoint {
    @SerializedName("latitude_c")
    private double latitude;
    @SerializedName("longitude_c")
    private double longitude;
    @SerializedName("altitude_c")
    private double altitude;
    @SerializedName("precision_c")
    private double precision;

    public PlotGpsPoint() {
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }
}
