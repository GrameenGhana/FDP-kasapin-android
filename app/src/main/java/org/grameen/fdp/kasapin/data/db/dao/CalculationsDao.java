package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Calculation;

import java.util.List;

/**
 * Created by AangJnr on 17, September, 2018 @ 8:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface CalculationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Calculation> objects);

    @Query("SELECT * FROM calculations")
    List<Calculation> getAllCalculations();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCalculation(Calculation calculation);


    @Query("SELECT * FROM calculations WHERE id = :id")
    Calculation getCalculationById(String id);


    @Update
    int updateCalculation(Calculation calculation);



    @Query("DELETE FROM calculations")
    void deleteAllCalculations();


    @Query("DELETE FROM calculations WHERE id = :id")
    int deleteCalculationById(String id);




}
