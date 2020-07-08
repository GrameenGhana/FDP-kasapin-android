package org.grameen.fdp.kasapin.di.component;


import android.app.Application;
import android.content.Context;

import org.grameen.fdp.kasapin.FDPKasapin;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.di.Scope.ApplicationContext;
import org.grameen.fdp.kasapin.di.module.ApplicationModule;
import org.grameen.fdp.kasapin.services.SyncService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(FDPKasapin app);
    void inject(SyncService service);

    @ApplicationContext
    Context getContext();
    Application getApplication();
    AppDataManager getAppDataManager();
}