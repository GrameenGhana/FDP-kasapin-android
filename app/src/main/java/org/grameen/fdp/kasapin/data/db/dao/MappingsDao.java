package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.grameen.fdp.kasapin.data.db.entity.Mapping;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 18, September, 2018 @ 12:25 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface MappingsDao extends BaseDao<Mapping> {


    @Transaction
    @Query("SELECT * FROM mappings WHERE questionId = :questionId")
    Maybe<Mapping> getByQuestionId(int questionId);


    @Query("DELETE FROM mappings")
    void deleteAll();


    @Query("SELECT * FROM mappings")
    Single<List<Mapping>> getAll();


}
