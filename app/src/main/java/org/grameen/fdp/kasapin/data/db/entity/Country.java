package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aangjnr on 30/01/2018.
 */

@Entity(tableName = "countries" )
public class Country {


    @PrimaryKey
    @NonNull
    String id;
    String Name;

    @SerializedName("currencySign__c")
    String currency;

    @SerializedName("ISO_code__c")
    String isoCode;

    @SerializedName("Avg_Price_Kg__c")
    String averageGatePrice;


    public Country() {
    }


    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
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
