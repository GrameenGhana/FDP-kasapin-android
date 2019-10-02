package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Form;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 17, September, 2018 @ 8:43 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface FormsDao {


    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Form> forms);

    @Query("SELECT * FROM forms")
    Single<List<Form>> getAllForms();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertForm(Form form);


    @Query("SELECT * FROM forms WHERE id = :id")
    Single<Form> getFormById(int id);


    @Query("SELECT translationId FROM forms WHERE formNameC COLLATE NOCASE = :label")
    Maybe<Integer> getId(String label);


    @Query("SELECT * FROM forms ORDER BY id ASC LIMIT 1")
    Single<Form> getDefaultForm();


    @Update
    int updateForm(Form form);


    @Query("DELETE FROM forms")
    void deleteAllForms();


    @Query("DELETE FROM forms WHERE id = :id")
    int deleteFormById(int id);

}
