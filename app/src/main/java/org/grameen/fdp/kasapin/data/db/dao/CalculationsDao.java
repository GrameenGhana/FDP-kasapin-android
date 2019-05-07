package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.Calculation;

import io.reactivex.Single;

/**
 * Created by AangJnr on 17, September, 2018 @ 8:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface CalculationsDao extends BaseDao<Calculation> {


    @Query("SELECT * FROM calculations WHERE recommendationId = :id AND year = :year")
    Single<Calculation> getByRecommendationYear(int id, int year);


    @Query("DELETE FROM calculations")
    void deleteAllCalculations();


}
