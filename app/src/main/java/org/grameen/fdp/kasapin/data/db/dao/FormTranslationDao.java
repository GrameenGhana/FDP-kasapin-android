package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.FormTranslation;

import java.util.List;

/**
 * Created by AangJnr on 17, September, 2018 @ 8:43 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface FormTranslationDao extends BaseDao<FormTranslation> {


    @Query("SELECT * FROM form_translation")
    List<FormTranslation> getAllFormTranslations();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFormTranslation(FormTranslation formTranslation);


    @Query("SELECT * FROM form_translation WHERE id = :id")
    FormTranslation getFormTranslationById(int id);


    @Update
    int updateFormTranslation(FormTranslation formTranslation);


    @Query("DELETE FROM form_translation")
    void deleteAllFormTranslations();


    @Query("DELETE FROM form_translation WHERE id = :id")
    int deleteFormTranslationById(int id);

}
