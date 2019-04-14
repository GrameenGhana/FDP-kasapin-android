package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.RecommendationActivity;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:50 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface RecommendationActivitiesDao extends BaseDao<RecommendationActivity>{


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RecommendationActivity> objects);


    @Query("SELECT * FROM recommendation_activities WHERE recommendationId = :recommendationId " +
            "AND month = :month AND year = :year AND seasonal = :labour")
    Single<List<RecommendationActivity>> getAllByRecommendation(int recommendationId, String month, String year, String labour);


    @Query("SELECT * FROM recommendation_activities WHERE recommendationId = :recommendationId " +
            "AND month = :month AND year = :year")
    Single<List<RecommendationActivity>> getAllByRecommendation(int recommendationId, String month, String year);



    @Query("SELECT * FROM recommendation_activities WHERE activityId = :activityId")
    Single<List<RecommendationActivity>> getAllByActivity(int activityId);


    @Query("DELETE FROM recommendation_activities")
    void deleteAll();



}
