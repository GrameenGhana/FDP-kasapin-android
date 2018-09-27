package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Village;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 1:48 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface VillagesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Village> objects);

    @Query("SELECT * FROM villages")
    LiveData<List<Village>> getAll();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVillage(Village village);


    @Query("SELECT * FROM villages WHERE id = :id")
    Village getVillageById(String id);


    @Update
    int updateVillage(Village village);



    @Query("DELETE FROM villages")
    void deleteAllVillages();


    @Query("DELETE FROM villages WHERE id = :id")
    int deleteVillageById(String id);

}
