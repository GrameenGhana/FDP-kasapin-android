package org.grameen.fdp.kasapin.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface FormAndQuestionsDao {


    /*@Transaction
    @Query("SELECT * FROM forms")
    Single<List<FormAndQuestions>> getAllFormAndQuestions();
*/


    @Transaction
    @Query("SELECT * FROM forms WHERE formNameC LIKE '%' || :name|| '%'")
    Single<FormAndQuestions> getFormAndQuestionsByName(String name);

    @Transaction
    @Query("SELECT * FROM forms WHERE formNameC LIKE '%' || :name|| '%'")
    Maybe<FormAndQuestions> maybeGetFormAndQuestionsByName(String name);

    @Transaction
    @Query("SELECT * FROM forms WHERE typeC LIKE '%' || :formType|| '%' AND displayTypeC LIKE '%' || :displayType|| '%' ORDER BY displayOrderC ASC")
    Single<List<FormAndQuestions>> getFormAndQuestionsByType(String formType, String displayType);


    @Transaction
    @Query("SELECT * FROM forms WHERE displayTypeC LIKE '%' || :displayType|| '%' ORDER BY displayOrderC ASC")
    Single<List<FormAndQuestions>> getFormAndQuestionsByDispayTypeOnly(String displayType);



    @Transaction
    @Query("SELECT * FROM forms WHERE displayTypeC LIKE '%' || :displayType|| '%' ORDER BY displayOrderC ASC")
    Maybe<List<FormAndQuestions>> getFormAndQuestionsByDisplayType(String displayType);


}
