package org.grameen.fdp.kasapin.data.db.model;

public class FarmerNameAndCode {
    String code;
    String farmerName;


    public FarmerNameAndCode(){}

    public void setCode(String code) {
        this.code = code;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getCode() {
        return code;
    }

    public String getFarmerName() {
        return farmerName;
    }
}
