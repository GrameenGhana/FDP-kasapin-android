package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Community;
import org.grameen.fdp.kasapin.data.db.entity.District;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 1:48 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface DistrictsDao extends BaseDao<District> {


    @Query("SELECT * FROM districts")
    Single<List<District>> getAllSingle();


    @Query("SELECT * FROM districts")
    Maybe<List<District>> getAll();




}
