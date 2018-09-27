package org.grameen.fdp.kasapin.di.component;



import android.support.v7.app.AlertDialog;

import org.grameen.fdp.kasapin.di.Scope.PerActivity;
import org.grameen.fdp.kasapin.di.module.ViewModule;
import org.grameen.fdp.kasapin.ui.farmerProfile.AddFarmerActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.ui.main.FarmerListFragment;
import org.grameen.fdp.kasapin.ui.main.MainActivity;
import org.grameen.fdp.kasapin.ui.setup.LoginActivity;
import org.grameen.fdp.kasapin.ui.splash.SplashActivity;

import dagger.Component;

/**
 * Created by AangJnr on 20, September, 2018 @ 2:12 AM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@PerActivity
@Component(modules = {ViewModule.class}, dependencies = {ApplicationComponent.class})
public interface ActivityComponent {


    //void inject(BaseActivity view);
    void inject(SplashActivity view);
    void inject(LoginActivity view);
    void inject(LandingActivity view);
    void inject(MainActivity view);
    void inject(AddFarmerActivity view);




}