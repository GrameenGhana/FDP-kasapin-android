package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Query;

import org.grameen.fdp.kasapin.data.db.entity.Country;

import java.util.List;

@Dao
public interface CountryDao extends BaseDao<Country> {
    @Query("SELECT * FROM countries")
    List<Country> getAllCountries();

    @Query("SELECT * FROM countries WHERE id = :id")
    Country getCountryById(String id);

    @Query("DELETE FROM countries")
    void deleteAllCountries();

    @Query("DELETE FROM countries WHERE id = :id")
    int deleteCountryById(String id);
}
