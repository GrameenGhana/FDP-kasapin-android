package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;

@Entity(tableName = "urls")
public class ServerUrl extends BaseModel {
    String name;
    String url;
    public ServerUrl() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
