package fanpeihua.huamusic.di.component;

import android.content.Context;

import dagger.Component;
import fanpeihua.huamusic.di.module.ApplicationModule;
import fanpeihua.huamusic.di.scope.ContextLife;
import fanpeihua.huamusic.di.scope.PerApp;

@PerApp
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ContextLife("Application")
    Context getApplication();
}
