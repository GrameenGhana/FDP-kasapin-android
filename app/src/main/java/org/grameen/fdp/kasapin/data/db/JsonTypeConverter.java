package org.grameen.fdp.kasapin.data.db;


import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.grameen.fdp.kasapin.ui.base.model.Loc;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by AangJnr on 20, September, 2018 @ 4:11 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class JsonTypeConverter {
    @TypeConverter
    public static JsonObject stringToJSON(String data) {
        try {
            return new JsonObject().getAsJsonObject(data);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonObject();
        } }

    @TypeConverter
    public static String fromJSONToString(JsonObject data) {
        return data.getAsString();
    }
}
