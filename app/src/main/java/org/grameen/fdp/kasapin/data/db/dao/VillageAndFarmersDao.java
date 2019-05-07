package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.grameen.fdp.kasapin.data.db.entity.VillageAndFarmers;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by AangJnr on 20, September, 2018 @ 5:57 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface VillageAndFarmersDao {

    @Transaction
    @Query("SELECT id, name FROM villages")
    Single<List<VillageAndFarmers>> getVillagesAndFarmers();

}
