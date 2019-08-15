package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

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
