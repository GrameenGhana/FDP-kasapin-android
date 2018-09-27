package org.grameen.fdp.kasapin;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.di.component.ApplicationComponent;
import org.grameen.fdp.kasapin.di.component.DaggerApplicationComponent;
import org.grameen.fdp.kasapin.di.module.ApplicationModule;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;

public class FDPKasapin extends Application {


    @Inject
    AppDataManager mAppDataManager;

    private ApplicationComponent mApplicationComponent;


    @Override
    public void onCreate() {
        super.onCreate();


        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);




        //Initialize application logging mechanism
        AppLogger.init();

       /* AndroidNetworking.initialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        }*/


       createNoMediaFile();

    }



    public static FDPKasapin getAppContext(Context context) {
        return (FDPKasapin) context.getApplicationContext();
    }


    public ApplicationComponent getComponent(){
        return mApplicationComponent;
    }


    public static void createNoMediaFile() {
        FileOutputStream out = null;

        try {

            File ROOT = new File(AppConstants.ROOT_DIR);
            if (!ROOT.exists()) ROOT.mkdirs();


            File thumbnailsDir = new File(ROOT + File.separator + ".thumbnails");
            if (!thumbnailsDir.exists())
                if (thumbnailsDir.mkdirs())
                    Log.i("Application", "Thumbnails dir file created!  " + thumbnailsDir);



            File file = new File(ROOT + File.separator, ".nomedia");
            if (!file.exists()) {
                out = new FileOutputStream(file);
                out.write(0);
                out.close();}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
