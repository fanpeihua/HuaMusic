package fanpeihua.huamusic.di.module;

import android.app.Activity;
import android.app.Fragment;

import dagger.Module;
import dagger.Provides;
import fanpeihua.huamusic.di.scope.ContextLife;
import fanpeihua.huamusic.di.scope.PerFragment;

@Module
public class FragmentModule {
    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

    @Provides
    @PerFragment
    @ContextLife("Activity")
    public Activity provideActivity() {
        return mFragment.getActivity();
    }

    @Provides
    @PerFragment
    public Fragment provideFragment() {
        return mFragment;
    }
}
