package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Input;

import java.util.List;

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




