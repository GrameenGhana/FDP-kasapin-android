package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.Recommendation;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:47 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface RecommendationsDao extends BaseDao<Recommendation> {

    @Query("SELECT * FROM recommendations WHERE cropId = :id")
    Recommendation getByCrop(int id);


    @Query("SELECT * FROM recommendations WHERE cropId = :id ORDER BY hierarchy ASC")
    Maybe<List<Recommendation>> getRecommendationsByCrop(int id);


    @Query("SELECT label FROM recommendations WHERE id = :id")
    Maybe<String> getByRecommendationName(int id);

    @Query("SELECT label FROM recommendations WHERE id = :id")
    Maybe<String> getByRecommendationId(int id);

    @Query("SELECT * FROM recommendations WHERE recommendationName  LIKE '%' || :label || '%'")
    Maybe<Recommendation> getByRecommendationName(String label);

    @Query("SELECT * FROM recommendations WHERE id  = :id")
    Maybe<Recommendation> get(int id);


    @Query("DELETE FROM recommendations")
    void deleteAllRecommendations();


}
