package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

public interface BaseDao<T> {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<T> objects);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOne(T object);

    @Update
    int updateOne(T object);

    @Delete
    void deleteOne(T object);
}
