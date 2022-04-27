package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.RecommendationActivity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface RecommendationActivitiesDao extends BaseDao<RecommendationActivity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RecommendationActivity> objects);

    @Query("SELECT * FROM recommendation_activities WHERE recommendationId = :recommendationId " +
            "AND month = :month AND year = :year AND seasonal = :labour")
    Single<List<RecommendationActivity>> getAllByRecommendation(int recommendationId, String month, String year, String labour);

    @Query("SELECT * FROM recommendation_activities WHERE recommendationId = :recommendationId " +
            "AND month = :month AND year = :year")
    Single<List<RecommendationActivity>> getAllByRecommendation(int recommendationId, String month, String year);

    @Query("SELECT * FROM recommendation_activities WHERE recommendationId = :recommendationId  AND year = :year")
    Single<List<RecommendationActivity>> getAllByRecommendation(int recommendationId, String year);


    @Query("SELECT * FROM recommendation_activities WHERE activityId = :activityId")
    Single<List<RecommendationActivity>> getAllByActivity(int activityId);

    @Query("DELETE FROM recommendation_activities")
    void deleteAll();

}
