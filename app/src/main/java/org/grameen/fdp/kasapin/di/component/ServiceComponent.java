package org.grameen.fdp.kasapin.di.component;


import org.grameen.fdp.kasapin.di.Scope.PerService;
import org.grameen.fdp.kasapin.di.module.ServiceModule;
import org.grameen.fdp.kasapin.services.SyncService;

import dagger.Component;

/**
 * Created by AangJnr on 20, September, 2018 @ 2:20 AM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {

    void inject(SyncService service);

}

