package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.Community;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface CommunitiesDao extends BaseDao<Community> {
    @Query("SELECT * FROM communities")
    Maybe<List<Community>> getAll();

    @Query("SELECT * FROM communities WHERE id = :id")
    Community getVillageById(int id);
}
