package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Activity;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 17, September, 2018 @ 7:07 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface FormAnswersDao extends BaseDao<FormAnswerData>{


   /* @Insert
    long insertOne(FormAnswerData answerData);

    @Update
    int updateOne(FormAnswerData answerData);*/

    @Query("DELETE FROM form_answers WHERE id = :id AND farmerCode = :farmerCode")
    int deleteAnswerData(int id, String farmerCode);

    @Query("SELECT * FROM form_answers WHERE farmerCode = :farmerCode AND formId = :formId")
    Single<FormAnswerData> getFormAnswerData(String farmerCode, int formId);

    @Query("SELECT * FROM form_answers WHERE farmerCode = :farmerCode")
    Single<List<FormAnswerData>> getFormAnswersData(String farmerCode);







}
