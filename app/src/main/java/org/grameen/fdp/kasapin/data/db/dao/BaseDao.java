package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/**
 * Created by AangJnr on 20, September, 2018 @ 5:14 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public interface BaseDao<T> {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<T> objects);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOne(T object);

    @Update
    int updateOne(T object);

    @Delete
    int deleteOne(T object);


}
