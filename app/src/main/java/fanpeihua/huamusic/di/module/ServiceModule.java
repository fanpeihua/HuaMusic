package fanpeihua.huamusic.di.module;

import android.app.Service;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import fanpeihua.huamusic.di.scope.ContextLife;
import fanpeihua.huamusic.di.scope.PerService;

@Module
public class ServiceModule {
    private Service mService;

    public ServiceModule(Service service) {
        mService = service;
    }

    @Provides
    @PerService
    @ContextLife("Service")
    public Context ProvideServiceContext() {
        return mService;
    }
}
