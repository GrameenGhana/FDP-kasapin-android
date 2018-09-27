package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Question;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:37 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface QuestionDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Question> objects);


    @Query("SELECT * FROM questions")
    List<Question> getAllQuestions();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(Question question);


    @Query("SELECT * FROM questions WHERE id = :id")
    Question getQuestionById(String id);

    @Query("SELECT * FROM questions WHERE formId = :formId")
    List<Question> getQuestionsByform(String formId);


    @Update
    int updateQuestion(Question question);



    @Query("DELETE FROM questions")
    void deleteAllQuestions();


    @Query("DELETE FROM questions WHERE id = :id")
    int deleteQuestionById(String id);


}
