package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Activity;

import java.util.List;

/**
 * Created by AangJnr on 17, September, 2018 @ 7:07 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface ActivitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Activity> objects);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivity(Activity activity);


    @Update
    int updateActivity(Activity activity);


    @Query("DELETE FROM activities WHERE id = :id")
    int deleteActivityById(String id);


    @Query("DELETE FROM activities")
    void deleteAllActivities();


    @Query("SELECT * FROM activities")
    List<Activity> getAllActivities();


}
