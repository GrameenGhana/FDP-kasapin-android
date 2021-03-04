package org.grameen.fdp.kasapin.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.ShadowData;

import java.util.List;

@Dao
public interface ShadowDataDao extends BaseDao<ShadowData> {

    @Query("SELECT * FROM shadow_data WHERE farmer_id=:farmerId ORDER BY id DESC")
    ShadowData getShadowDataForFarmer(String farmerId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addData(ShadowData sdata);

    @Query("DELETE FROM shadow_data WHERE farmer_id LIKE :farmerId")
    int removeFarmerData(String farmerId);
}
