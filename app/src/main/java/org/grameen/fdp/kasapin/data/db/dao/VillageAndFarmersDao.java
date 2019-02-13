package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.grameen.fdp.kasapin.data.db.entity.PlotAndAssessments;
import org.grameen.fdp.kasapin.data.db.entity.VillageAndFarmers;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by AangJnr on 20, September, 2018 @ 5:57 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface VillageAndFarmersDao {

    @Transaction
    @Query("SELECT id, name FROM villages")
    Flowable<List<VillageAndFarmers>> getVillagesAndFarmers();

}
