package org.grameen.fdp.kasapin;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import org.grameen.fdp.kasapin.di.component.ApplicationComponent;
import org.grameen.fdp.kasapin.di.component.DaggerApplicationComponent;
import org.grameen.fdp.kasapin.di.module.ApplicationModule;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import java.io.File;
import java.io.FileOutputStream;

import timber.log.Timber;

public class FDPKasapin extends Application {


    private ApplicationComponent mApplicationComponent;


    public static void createNoMediaFile() {
        FileOutputStream out = null;

        try {

            File ROOT = new File(AppConstants.ROOT_DIR);
            if (!ROOT.exists()) ROOT.mkdirs();


            File thumbnailsDir = new File(ROOT + File.separator + ".thumbnails");
            if (!thumbnailsDir.exists())
                if (thumbnailsDir.mkdirs())
                    Timber.i("Thumbnails dir file created!  %s", thumbnailsDir);


            File crashReporterDir = new File(AppConstants.CRASH_REPORTS_DIR);
            if (!crashReporterDir.exists()) crashReporterDir.mkdirs();
            Timber.i("Crash Reporter dirs created! %s", crashReporterDir);


            File databaseBackupDir = new File(AppConstants.DATABASE_BACKUP_DIR);
            if (!databaseBackupDir.exists()) databaseBackupDir.mkdirs();
            Timber.i("DatabaseBackup dirs created! %s", databaseBackupDir);


            File file = new File(ROOT + File.separator, ".nomedia");
            if (!file.exists()) {
                out = new FileOutputStream(file);
                out.write(0);
                out.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static FDPKasapin getAppContext(Context context) {
        return (FDPKasapin) context.getApplicationContext();
    }


    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);


        //Initialize application logging mechanism
        AppLogger.init(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


    }

}
