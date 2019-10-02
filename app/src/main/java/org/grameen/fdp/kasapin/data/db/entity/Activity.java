package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by aangjnr on 27/01/2018.
 */

@Entity(tableName = "activities")
public class Activity {


    @PrimaryKey
    @NonNull
    String Id;
    String Name;

    public Activity() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


}
