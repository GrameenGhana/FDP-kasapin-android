package org.grameen.fdp.kasapin.data.db;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.grameen.fdp.kasapin.ui.base.model.Loc;

import java.util.List;

/**
 * Created by AangJnr on 20, September, 2018 @ 4:11 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class ListTypeConverter {
    @TypeConverter
    public static List<Loc> stringToList(String data) {
        return new Gson().fromJson(data, new TypeToken<List<Loc>>() {
        }.getType());
    }

    @TypeConverter
    public static String fromListToString(List<Loc> data) {
        return new Gson().toJson(data);
    }
}
