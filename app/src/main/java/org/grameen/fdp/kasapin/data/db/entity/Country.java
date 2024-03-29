package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "countries")
public class Country extends BaseModel {
    String name;
    @SerializedName("currency")
    String currency;
    @SerializedName("iso_code")
    String isoCode;
    @SerializedName("avg_gate_price")
    String averageGatePrice;

    public Country() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAverageGatePrice() {
        return averageGatePrice;
    }

    public void setAverageGatePrice(String averageGatePrice) {
        this.averageGatePrice = averageGatePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }
}
