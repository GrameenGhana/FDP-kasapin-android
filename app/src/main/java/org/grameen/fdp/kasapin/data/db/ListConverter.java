package org.grameen.fdp.kasapin.data.db;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


/**
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class ListConverter {
    @TypeConverter
    public static String from(List<String> data) {
        return new Gson().toJson(data);
    }

    @TypeConverter
    public static List<String> to(String data) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(data, listType);
    }
}