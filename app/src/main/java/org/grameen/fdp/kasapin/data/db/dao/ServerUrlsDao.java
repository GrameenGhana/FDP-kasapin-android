package org.grameen.fdp.kasapin.data.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.ServerUrl;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ServerUrlsDao extends BaseDao<ServerUrl> {


    @Query("SELECT * FROM urls")
    Single<List<ServerUrl>> getAllUrls();



}
