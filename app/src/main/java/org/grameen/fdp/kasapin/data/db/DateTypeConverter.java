package org.grameen.fdp.kasapin.data.db;


import androidx.room.TypeConverter;

import java.util.Date;


/**
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class DateTypeConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestampInMillis(Date date) {
        return date == null ? null : date.getTime();
    }
}