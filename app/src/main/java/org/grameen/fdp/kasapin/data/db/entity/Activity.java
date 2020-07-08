package org.grameen.fdp.kasapin.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activities")
public class Activity {
    @PrimaryKey
    @NonNull
    String Id = "";
    String Name;

    public Activity() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    @NonNull
    public String getId() {
        return Id;
    }

    public void setId(@NonNull String id) {
        Id = id;
    }


}
