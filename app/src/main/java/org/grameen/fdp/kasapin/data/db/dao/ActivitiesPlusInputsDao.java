package org.grameen.fdp.kasapin.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.ActivitiesPlusInput;

import java.util.List;

/**
 * Data Access Object for the Activities_plus_input_table.
 */

@Dao
public interface ActivitiesPlusInputsDao extends BaseDao<ActivitiesPlusInput> {
    @Query("DELETE FROM activities_plus_inputs where id = :id")
    int deleteActivityPlusInput(String id);

    @Query("SELECT * FROM activities_plus_inputs where id = :id")
    ActivitiesPlusInput getActivityPlusInput(String id);

    @Query("DELETE FROM activities_plus_inputs")
    void deleteAllActivitiesPlusInputs();

    @Query("SELECT * FROM activities_plus_inputs")
    List<ActivitiesPlusInput> getAllActivitiesPlusInputs();

}
