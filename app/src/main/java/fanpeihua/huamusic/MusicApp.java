package fanpeihua.huamusic;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import fanpeihua.huamusic.player.PlayerManager;

public class MusicApp extends Application {

    @SuppressLint("StaticFieldLeak")
    private static MusicApp sInstance;

    private PlayerManager.ServiceToken mToken;

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    public static synchronized MusicApp getsInstance() {
        return sInstance;
    }


    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mContext = this;
    }
}
