package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface FormAnswersDao extends BaseDao<FormAnswerData> {
    @Query("DELETE FROM form_answers WHERE id = :id")
    int deleteAnswerData(int id);

    @Query("SELECT * FROM form_answers WHERE farmerCode = :farmerCode AND formId = :formId")
    Maybe<FormAnswerData> getFormAnswerDataOrNull(String farmerCode, int formId);

    @Query("SELECT * FROM form_answers WHERE farmerCode = :farmerCode AND formId = :formId")
    FormAnswerData getFormAnswerData(String farmerCode, int formId);

    @Query("SELECT * FROM form_answers WHERE farmerCode = :farmerCode")
    Single<List<FormAnswerData>> getAll(String farmerCode);

}
