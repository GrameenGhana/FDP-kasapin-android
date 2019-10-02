package org.grameen.fdp.kasapin.data.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.grameen.fdp.kasapin.data.db.entity.Monitoring;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by AangJnr on 17, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */


@Dao
public interface MonitoringsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Monitoring> objects);

    @Query("SELECT * FROM  monitorings")
    List<Monitoring> getAllMonitorings();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMonitoring(Monitoring monitoring);


    @Query("SELECT * FROM monitorings WHERE id = :id")
    Monitoring getMonitoringById(String id);

    @Query("SELECT * FROM monitorings WHERE externalId = :monitoringExternalId")
    Maybe<Monitoring> getMonitoringForSelectedYear(String monitoringExternalId);

    @Query("SELECT * FROM monitorings WHERE plotExternalId = :plotExternalId")
    Maybe<List<Monitoring>> getAllMonitoringForPlot(String plotExternalId);

    @Query("SELECT * FROM monitorings WHERE plotExternalId = :plotExternalId AND year =:year")
    Single<List<Monitoring>> getAllMonitoringsForSelectedYear(String plotExternalId, int year);


    @Query("SELECT * FROM monitorings WHERE plotExternalId = :plotExternalId AND year =:year ORDER BY id ASC LIMIT 1")
    Monitoring getFirstMonitoringForSelectedYear(String plotExternalId, int year);

    @Query("SELECT * FROM monitorings WHERE plotExternalId = :plotExternalId AND year =:year ORDER BY id DESC LIMIT 1")
    Monitoring getLastMonitoringForSelectedYear(String plotExternalId, int year);

    @Query("SELECT COUNT(id) FROM monitorings WHERE plotExternalId = :plotExternalId AND year =:year")
    Maybe<Integer> countMonitoringsForSelectedYear(String plotExternalId, int year);

    @Update
    int updateMonitoring(Monitoring monitoring);


    @Query("DELETE FROM monitorings")
    void deleteAllMonitoring();


    @Query("DELETE FROM monitorings WHERE id = :id")
    int deleteMonitoringById(String id);


}
