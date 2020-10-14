package org.grameen.fdp.kasapin.syncManager;

import org.grameen.fdp.kasapin.data.db.AppDatabase;
import org.grameen.fdp.kasapin.data.db.entity.Logs;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import java.util.Collections;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class LogRecorder {
    private final AppDatabase mAppDatabase;

     public LogRecorder(AppDatabase appDatabase) {
        mAppDatabase = appDatabase;
    }


    public void add(String farmerCode, String label) {
        //Save logs if this event triggers/
        //We want to be able to keep track of whether questions of type AppConstants.TYPE_PHOTO
        //Was changed or not in order to decide to add to sync up payload since image base64 files are relatively large
        add(farmerCode, Collections.singleton(label));
    }

    public void add(String farmerCode, Set<String> labels) {
        //Save logs if this event triggers/
        //We want to be able to keep track of whether questions of type AppConstants.TYPE_PHOTO
        //Was changed or not in order to decide to add to sync up payload since image base64 files are relatively large
        Logs farmerLog = mAppDatabase.logsDao().getAllLogsForFarmer(farmerCode).blockingGet(new Logs(farmerCode));
        for(String s : labels)
            farmerLog.add(s);

        Single.just(mAppDatabase.logsDao().insertOne(farmerLog))
                .subscribeOn(Schedulers.io())
                .subscribe();
        AppLogger.i("LogRecorder", "LOG ADDED ==> " + farmerLog.toString());
    }

}
