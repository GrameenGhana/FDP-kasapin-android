package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Community;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 1:48 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface CommunitiesDao extends BaseDao<Community> {


    @Query("SELECT * FROM communities")
    Single<List<Community>> getAllSingle();


    @Query("SELECT * FROM communities")
    Maybe<List<Community>> getAll();




    @Query("SELECT * FROM communities WHERE id = :id")
    Community getVillageById(int id);


    @Update
    int updateVillage(Community village);


    @Query("DELETE FROM communities")
    void deleteAllVillages();


    @Query("DELETE FROM communities WHERE id = :id")
    int deleteVillageById(String id);

}
