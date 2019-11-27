package org.grameen.fdp.kasapin.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "urls")
public class ServerUrl extends BaseModel{

    String name;
    String url;


    public ServerUrl(){}


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
