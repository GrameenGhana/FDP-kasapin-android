package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Recommendation;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:47 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface RecommendationsDao extends BaseDao<Recommendation>{

    @Query("SELECT * FROM recommendations WHERE cropId = :id")
    Recommendation getByCrop(int id);


    @Query("DELETE FROM recommendations")
    void deleteAllRecommendations();



}
