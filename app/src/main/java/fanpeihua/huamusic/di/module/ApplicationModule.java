package fanpeihua.huamusic.di.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import fanpeihua.huamusic.MusicApp;
import fanpeihua.huamusic.di.scope.ContextLife;
import fanpeihua.huamusic.di.scope.PerApp;

@Module
public class ApplicationModule {
    private MusicApp mApplication;

    public ApplicationModule(MusicApp application) {
        this.mApplication = application;
    }

    @Provides
    @PerApp
    @ContextLife("Application")
    public Context provideApplicationContext() {
        return mApplication.getApplicationContext();
    }
}
