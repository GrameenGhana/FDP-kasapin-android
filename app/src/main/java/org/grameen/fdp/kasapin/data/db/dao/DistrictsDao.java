package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.District;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface DistrictsDao extends BaseDao<District> {
    @Query("SELECT * FROM districts")
    Maybe<List<District>> getAll();
}
