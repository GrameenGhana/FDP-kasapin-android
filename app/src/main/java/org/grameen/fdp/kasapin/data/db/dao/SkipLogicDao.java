package org.grameen.fdp.kasapin.data.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface SkipLogicDao extends BaseDao<SkipLogic> {
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
    void deleteAlSkipLogic();

    @Query("DELETE FROM skip_logics WHERE id = :id")
    int deleteSkipLogicById(int id);
}
