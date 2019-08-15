package org.grameen.fdp.kasapin.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by aangjnr on 08/02/2018.
 */

@Entity(tableName = "monitorings", indices = {@Index("plotExternalId"), @Index("year")}, foreignKeys = @ForeignKey(entity = Plot.class, parentColumns = "externalId", childColumns = "plotExternalId", onDelete = CASCADE))
public class Monitoring {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    String externalId;
    String year;
    String name;
    String json;
    String plotExternalId;


    public Monitoring() {
    }


    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getPlotExternalId() {
        return plotExternalId;
    }

    public void setPlotExternalId(String plotExternalId) {
        this.plotExternalId = plotExternalId;
    }


    @Ignore
    public JSONObject getMonitoringAOJsonData() throws JSONException {
        return new JSONObject(json);
    }



}
