package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:53 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface SkipLogicsDao extends BaseDao<SkipLogic> {


    @Query("SELECT * FROM skip_logics")
    LiveData<List<SkipLogic>> getAll();

    @Query("SELECT * FROM skip_logics WHERE questionId = :questionId")
    Single<List<SkipLogic>> getAllByQuestionId(int questionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSkipLogic(SkipLogic skipLogic);


    @Query("SELECT * FROM skip_logics WHERE id = :id")
    SkipLogic getSkipLogicById(int id);

    @Query("SELECT * FROM skip_logics WHERE questionId = :id")
    SkipLogic getSkipLogicByQuestionId(int id);

    @Query("SELECT * FROM skip_logics WHERE questionId = :id")
    Maybe<List<SkipLogic>> getMaybeSkipLogicByQuestionId(int id);

    @Update
    int updateSkipLogic(SkipLogic skipLogic);


    @Query("DELETE FROM skip_logics")
    void deleteAlSkipLogics();


    @Query("DELETE FROM skip_logics WHERE id = :id")
    int deleteSkipLogicById(int id);


}
