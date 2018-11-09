package fanpeihua.huamusic.player;

import android.os.RemoteException;

import java.lang.ref.WeakReference;

import fanpeihua.huamusic.IMusicService;
import fanpeihua.huamusic.bean.Music;


public class IMusicServiceStub extends IMusicService.Stub {

    private final WeakReference<MusicPlayerService> mService;

    public IMusicServiceStub(final MusicPlayerService service) {
        mService = new WeakReference<>(service);
    }

    @Override
    public void nextPlay(Music music) {
        mService.get().next
    }


}
