package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.ComplexCalculation;

import java.util.List;

/**
 * Created by AangJnr on 17, September, 2018 @ 8:30 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface ComplexCalculationDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ComplexCalculation> objects);

    @Query("SELECT * FROM complex_calculations")
    List<ComplexCalculation> getAllComplexCalculations();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComplexCalculation(ComplexCalculation complexCalculation);


    @Query("SELECT * FROM complex_calculations WHERE id = :id")
    ComplexCalculation getComplexCalculationById(String id);


    @Update
    int updateComplexComplexCalculation(ComplexCalculation complexCalculation);


    @Query("DELETE FROM complex_calculations")
    void deleteAllComplexCalculations();


    @Query("DELETE FROM complex_calculations WHERE id = :id")
    int deleteComplexCalculationById(String id);

}
