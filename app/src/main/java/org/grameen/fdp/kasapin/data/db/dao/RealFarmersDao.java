package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:44 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface RealFarmersDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RealFarmer> objects);

    @Query("SELECT * FROM farmers")
    LiveData<List<RealFarmer>> getAll();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFarmer(RealFarmer farmer);


    @Query("SELECT * FROM farmers WHERE id = :id")
    RealFarmer getFarmerById(String id);


    @Update
    int updateFarmer(RealFarmer farmer);



    @Query("DELETE FROM farmers")
    void deleteAllFarmers();


    @Query("DELETE FROM farmers WHERE id = :id")
    int deleteFarmerById(String id);



}
