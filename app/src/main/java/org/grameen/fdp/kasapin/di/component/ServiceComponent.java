package org.grameen.fdp.kasapin.di.component;

import org.grameen.fdp.kasapin.di.Scope.PerService;
import org.grameen.fdp.kasapin.di.module.ServiceModule;
import org.grameen.fdp.kasapin.services.SyncService;

import dagger.Component;

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {
    void inject(SyncService service);
}

