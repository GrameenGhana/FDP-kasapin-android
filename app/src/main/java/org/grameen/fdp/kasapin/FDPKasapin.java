package org.grameen.fdp.kasapin;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;


import org.grameen.fdp.kasapin.di.component.ApplicationComponent;
import org.grameen.fdp.kasapin.di.component.DaggerApplicationComponent;
import org.grameen.fdp.kasapin.di.module.ApplicationModule;
 import org.grameen.fdp.kasapin.utilities.AppLogger;
 ;

public class FDPKasapin extends Application {
    private ApplicationComponent mApplicationComponent;

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
