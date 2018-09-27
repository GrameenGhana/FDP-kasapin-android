package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.RecommendationsPlusActivity;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:50 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface RecommendationPlusActivitiesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RecommendationsPlusActivity> objects);


    @Query("SELECT * FROM recommendation_plus_activities")
    List<RecommendationsPlusActivity> getAllRecommendationsPlusActivities();


    @Query("SELECT * FROM recommendation_plus_activities WHERE recommendationId = :recommendationId")
    List<RecommendationsPlusActivity> getRecommendationsPlusActivitiesByRecommendationId(String recommendationId);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecommendationsPlusActivity(RecommendationsPlusActivity recommendationsPlusActivity);


    @Query("SELECT * FROM recommendation_plus_activities WHERE id = :id")
    RecommendationsPlusActivity getRecommendationsPlusActivityById(String id);


    @Update
    int updateRecommendationsPlusActivity(RecommendationsPlusActivity recommendationsPlusActivity);



    @Query("DELETE FROM recommendation_plus_activities")
    void deleteAllRecommendationsPlusActivitys();


    @Query("DELETE FROM recommendation_plus_activities WHERE id = :id")
    int deleteRecommendationsPlusActivityById(String id);


}
