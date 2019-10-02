package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Question;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:37 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface QuestionDao extends BaseDao<Question> {

    @Query("SELECT * FROM questions")
    Single<List<Question>> getAllQuestions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(Question question);


    @Query("SELECT * FROM questions WHERE id = :id")
    Question getQuestionById(int id);

    @Query("SELECT * FROM questions WHERE formTranslationId = :formTranslationId ORDER BY displayOrderC ASC ")
    Single<List<Question>> getQuestionsByForm(int formTranslationId);


    @Query("SELECT * FROM questions WHERE labelC  LIKE :label || '%'")
    Single<Question> getSingle(String label);

    @Query("SELECT * FROM questions WHERE labelC  LIKE :label || '%'")
    Question get(String label);


    @Query("SELECT labelC FROM questions WHERE labelC  LIKE :label || '%'")
    Maybe<String> getLabel(String label);

    @Query("SELECT captionC FROM questions WHERE labelC  LIKE :label || '%'")
    String getCaption(String label);

    @Query("SELECT labelC FROM questions WHERE id = :id")
    Maybe<String> getLabel(int id);

    @Query("SELECT * FROM questions WHERE id = :id")
    Maybe<Question> get(int id);

    @Update
    int updateQuestion(Question question);


    @Query("DELETE FROM questions")
    void deleteAllQuestions();


    @Query("DELETE FROM questions WHERE id = :id")
    int deleteQuestionById(int id);


}
