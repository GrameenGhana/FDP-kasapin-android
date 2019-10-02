package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by aangjnr on 30/01/2018.
 */

@Entity(tableName = "districts", indices = {@Index(value = "id", unique = true)})
public class District {

    @PrimaryKey
    @NonNull
    int id;

    @SerializedName("district")
    String district;


    @Ignore
    List<Community> communities;


    public District(){
    }



    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }


    public void setCommunities(List<Community> communities) {
        this.communities = communities;
    }

    public List<Community> getCommunities() {
        return communities;
    }
}
