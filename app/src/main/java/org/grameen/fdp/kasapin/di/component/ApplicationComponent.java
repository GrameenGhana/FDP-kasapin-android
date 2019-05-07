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

/**
 * Created by AangJnr on 20, September, 2018 @ 2:12 AM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(FDPKasapin app);

    void inject(SyncService service);
    //void inject(RetrofitInterceptor interceptor);


    @ApplicationContext
    Context getContext();

    Application getApplication();

    AppDataManager getAppDataManager();

}