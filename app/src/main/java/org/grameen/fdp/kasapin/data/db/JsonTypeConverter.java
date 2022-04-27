package org.grameen.fdp.kasapin.data.db;


import androidx.room.TypeConverter;

import com.google.gson.JsonObject;

/**
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
        }
    }

    @TypeConverter
    public static String fromJSONToString(JsonObject data) {
        return data.getAsString();
    }
}
