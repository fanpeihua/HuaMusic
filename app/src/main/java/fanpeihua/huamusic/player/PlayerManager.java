package fanpeihua.huamusic.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.WeakHashMap;

//import aidl.IMusicService;

public class PlayerManager {
    //    public static IMusicService mService = null;
    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap;

    static {
        mConnectionMap = new WeakHashMap<Context, ServiceBinder>();
    }

    public static final ServiceToken bindToService(final Context context, final ServiceConnection callback) {
        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }
        final ContextWrapper contextWrapper = new ContextWrapper(realActivity);
//        contextWrapper.startService(new Intent(contextWrapper,MusicPlayer))
        return null;
    }

    public static final class ServiceBinder implements ServiceConnection {
        private final ServiceConnection mCallback;
        private final Context mContext;

        public ServiceBinder(final ServiceConnection callback, final Context context) {
            mCallback = callback;
            mContext = context;
        }

        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
//            mService = IMusicService.Stub.asInterface(service);
            if (mCallback != null) {
                mCallback.onServiceConnected(name, service);
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(name);
            }
//            mService = null;
        }
    }

    public static final class ServiceToken {
        public ContextWrapper mWrapperContext;

        public ServiceToken(final ContextWrapper context) {
            mWrapperContext = context;
        }
    }
}
