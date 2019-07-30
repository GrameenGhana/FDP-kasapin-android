package org.grameen.fdp.kasapin.data.db.entity;

public class PlotGpsPoint {

    double latitude_c;
    double longitude_c;
    double altitude_c;
    double precision_c;



    public PlotGpsPoint(){}


    public void setAltitude_c(double altitude_c) {
        this.altitude_c = altitude_c;
    }


    public void setPrecision_c(double precision_c) {
        this.precision_c = precision_c;
    }

    public double getAltitude_c() {
        return altitude_c;
    }


    public void setLatitude_c(double latitude_c) {
        this.latitude_c = latitude_c;
    }

    public void setLongitude_c(double longitude_c) {
        this.longitude_c = longitude_c;
    }

    public double getLatitude_c() {
        return latitude_c;
    }

    public double getLongitude_c() {
        return longitude_c;
    }

    public double getPrecision_c() {
        return precision_c;
    }
}
