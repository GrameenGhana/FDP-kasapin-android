package org.grameen.fdp.kasapin.data.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

 import org.grameen.fdp.kasapin.data.db.entity.Country;

import java.util.List;

/**
 * Created by AangJnr on 17, September, 2018 @ 8:37 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Dao
public interface CountryDao extends BaseDao<Country>{

    @Query("SELECT * FROM countries")
    List<Country> getAllCountries();

    @Query("SELECT * FROM countries WHERE id = :id")
    Country getCountryById(String id);


    @Query("DELETE FROM countries")
    void deleteAllCountries();


    @Query("DELETE FROM countries WHERE id = :id")
    int deleteCountryById(String id);



}
