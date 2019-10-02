package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.grameen.fdp.kasapin.data.db.entity.CommunitiesAndFarmers;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by AangJnr on 20, September, 2018 @ 5:57 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface CommunityAndFarmersDao {

    @Transaction
    @Query("SELECT * FROM communities")
    Single<List<CommunitiesAndFarmers>> getVillagesAndFarmers();

}
