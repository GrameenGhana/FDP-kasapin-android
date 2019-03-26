package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Recommendation;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:47 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface RecommendationsDao extends BaseDao<Recommendation>{

    @Query("SELECT * FROM recommendations WHERE cropId = :id")
    Recommendation getByCrop(int id);


    @Query("SELECT * FROM recommendations WHERE cropId = :id ORDER BY hierarchy ASC")
    Maybe<List<Recommendation>> getRecommendationsByCrop(int id);


    @Query("SELECT label FROM recommendations WHERE id = :id")
    Maybe<String> getLabel(int id);

    @Query("SELECT * FROM recommendations WHERE label  LIKE '%' || :label || '%'")
    Maybe<Recommendation> getLabel(String label);


    @Query("DELETE FROM recommendations")
    void deleteAllRecommendations();









}
