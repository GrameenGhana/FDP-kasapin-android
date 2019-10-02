package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Logic;

import java.util.List;

/**
 * Created by AangJnr on 17, September, 2018 @ 9:03 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface LogicsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Logic> objects);


    @Query("SELECT * FROM logics")
    List<Logic> getAlLogics();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLogic(Logic logic);


    @Query("SELECT * FROM logics WHERE id = :id")
    Logic getLogicById(String id);


    @Update
    int updateLogic(Logic logic);


    @Query("DELETE FROM logics")
    void deleteAllLogics();


    @Query("DELETE FROM logics WHERE id = :id")
    int deleteLogicById(String id);


}
