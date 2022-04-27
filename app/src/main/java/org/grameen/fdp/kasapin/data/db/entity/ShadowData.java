package org.grameen.fdp.kasapin.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "shadow_data")
public class ShadowData extends BaseModel{

//    @PrimaryKey(autoGenerate = true)
//    @NonNull
//    @SerializedName("id")
//    private int id;

    @SerializedName("farmer_id")
    public String farmer_id;

    @SerializedName("farmer_member_data")
    public String farmer_member_data;

    public void setFarmerId(String farmerId){
        this.farmer_id = farmerId;
    }

    public String getFarmerId(){
        return this.farmer_id;
    }

    public void setFarmerData(String jsonData){
        this.farmer_member_data = jsonData;
    }

    public String getFarmerMemberData(){
        return this.farmer_member_data;
    }

    public int getDataId(){
        return this.id;
    }
}
