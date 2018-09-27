package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import org.grameen.fdp.kasapin.data.db.entity.Input;

import java.util.List;

/**
 * Created by AangJnr on 17, September, 2018 @ 8:59 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface InputsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Input> objects);


    @Query("SELECT * FROM inputs")
    List<Input> getAllInputs();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInput(Input input);


    @Query("SELECT * FROM inputs WHERE id = :id")
    Input getInputById(String id);


    @Update
    int updateInput(Input input);



    @Query("DELETE FROM inputs")
    void deleteAllInputs();


    @Query("DELETE FROM inputs WHERE id = :id")
    int deleteInputById(String id);

}




