package org.grameen.fdp.kasapin.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity(tableName = "logs", indices = {@Index(value = "farmerCode", unique = true),})
public class Logs extends BaseModel {
    @NotNull
    String farmerCode;
    List<String> data;

    public Logs() {
    }

    public Logs(@NonNull String f_code) {
        this.farmerCode = f_code;
        data = new ArrayList<>();
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setFarmerCode(@NonNull String farmerCode) {
        this.farmerCode = farmerCode;
    }

    @NonNull
    public String getFarmerCode() {
        return farmerCode;
    }


    @Ignore
    public void addAll(Set<String> labels){
        data.addAll(labels);
    }

    @Ignore
    public void remove(String label){
      data.remove(label);
    }
}
