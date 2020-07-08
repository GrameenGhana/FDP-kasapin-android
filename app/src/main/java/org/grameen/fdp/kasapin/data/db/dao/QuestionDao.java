package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.Question;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface QuestionDao extends BaseDao<Question> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(Question question);

    @Query("SELECT * FROM questions WHERE id = :id")
    Question getQuestionById(int id);

    @Query("SELECT * FROM questions WHERE formTranslationId = :formTranslationId ORDER BY displayOrderC ASC ")
    Single<List<Question>> getQuestionsByForm(int formTranslationId);

    @Query("SELECT * FROM questions WHERE formTranslationId = :formTranslationId AND labelC  LIKE :label || '%'")
    Question get(int formTranslationId, String label);

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
}
