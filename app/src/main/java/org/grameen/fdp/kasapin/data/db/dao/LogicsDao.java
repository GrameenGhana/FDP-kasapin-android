package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


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
