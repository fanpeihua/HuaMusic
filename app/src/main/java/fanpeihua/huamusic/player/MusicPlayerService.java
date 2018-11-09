package fanpeihua.huamusic.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

import org.litepal.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fanpeihua.huamusic.bean.Music;
import fanpeihua.huamusic.common.Constants;
import fanpeihua.huamusic.player.playback.PlayProgressListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MusicPlayerService extends Service {
    private static final String TAG = "MusicPlayerService";
    public static final String ACTION_SERVICE = "fanpeihua.huamusic.service"; // 广播标志
    // 通知栏
    public static final String ACTION_NEXT = "fanpeihua.huamusic.notify.next"; // 下一首广播标志
    public static final String ACTION_PERV = "fanpeihua.huamusic.notify.prev"; // 上一首广播
    public static final String ACTION_PLAY_PAUSE = "fanpeihua.huamusic.notify.play_state"; // 播放暂停广播
    public static final String ACITON_CLOSE = "fanpeihua.huamusic.notify.close"; // 播放暂停广播

    public static final String ACTION_LYRIC = "fanpeihua.huamusic.notify.lyric"; // 播放暂停广播
    public static final String PLAY_STATE_CHANGED = "fanpeihua.huamusic.play_state"; // 切换播放状态

    public static final String DURATION_CHANGED = "fanpeihua.huamusic.duration";

    public static final String TRAK_ERROR = "fanpeihua.huamusic.error";
    public static final String SHUTDOWN = "fanpeihua.huamusic.shutdown";
    public static final String REFRESH = "fanpeihua.huamusic.refresh";

    public static final String PLAY_QUEUE_CLEAR = "fanpeihua.huamusic.play_queue_clear";// 清空播放队列
    public static final String PLAY_QUEUE_CHANGE = "fanpeihua.huamusic.play_queue_change";// 播放队列改变

    public static final String META_CHANGED = "fanpeihua.huamusic.metachanged";// 歌曲切换
    public static final String SCHEDULE_CHANGED = "fanpeihua.huamusic.schedule"; // 定时广播

    public static final String CMD_TOGGLE_PAUSE = "toggle_pause";//按键播放暂停
    public static final String CMD_NEXT = "next";//按键下一首
    public static final String CMD_PREVIOUS = "previous";//按键上一首
    public static final String CMD_PAUSE = "pause";//按键暂停
    public static final String CMD_PLAY = "play";//按键播放
    public static final String CMD_STOP = "stop";//按键停止
    public static final String CMD_FORWARD = "forward";//按键停止
    public static final String CMD_REWIND = "reward";//按键停止
    public static final String SERVICE_CMD = "cmd_service";//状态改变
    public static final String FROM_MEDIA_BUTTON = "media";//状态改变
    public static final String CMD_NAME = "name";//状态改变


    public static final int TRACK_WENT_TO_NEXT = 2; //下一首
    public static final int RELEASE_WAKELOCK = 3; //播放完成
    public static final int TRACK_PLAY_ENDED = 4; //播放完成
    public static final int TRACK_PLAY_ERROR = 5; //播放出错

    public static final int PREPARE_ASYNC_UPDATE = 7; //PrepareAsync装载进程
    public static final int PLAYER_PREPARED = 8; //mediaplayer准备完成

    public static final int AUDIO_FOCUS_CHANGE = 12; //音频焦点改变
    public static final int VOLUME_FADE_DOWN = 13; //音频焦点改变
    public static final int VOLUME_FADE_UP = 14; //音频焦点改变

    private final int NOTIFICATION_ID = 0x123;
    private long mNotificationPostTime = 0;
    private int mServiceStartId = -1;

    /**
     * 错误次数，超过最大错误次数，自动停止播放
     */
    private int playErrorTimes = 0;
    private int MAX_ERROR_TIMES = 5;

    private static final boolean DEBUG = true;

    private MusicPlayerEngine mPlayer = null;
    public PowerManager.WakeLock mWakeLock;
    private PowerManager powerManager;

    public Music mPlayingMusic = null;
    private List<Music> mPlayQueue = new ArrayList<>();
    private List<Integer> mHistoryPos = new ArrayList<>();
    private int mPlayingPos = -1;
    private int mNextPlayPos = -1;
    private String mPlaylistId = Constants.PLAYLIST_QUEUE_ID;

    ServiceReceiver mServiceReceiver;
    HeadsetReceiver mHeadsetReceiver;
    HeadsetPlugInReceiver mHeadsetPlugInReceiver;

    private FloatLyricViewManager mFloatLyricViewManager;
    private MediaSessionManager mediaSessionManager;
    private AudioAndFocusManager audioAndFocusManager;

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private Notification mNotification;
    private IMusicServiceStub mBindStub = new IMusicServiceStub(this);
    private boolean mPausedByTransientLossOfFocus = false;

    public static int totalTime = 0;
    boolean mServiceInUse = false;

    private MusicPlayerHandler mHandler;
    private HandlerThread mWorkThread;
    private Handler mMainHandler;

    private boolean showLyric;

    private static MusicPlayerService instance;

    public static MusicPlayerService getInstance() {
        return instance;
    }

    private static List<PlayProgressListener> listenerList = new ArrayList<>();

    public static void addProgressListener(PlayProgressListener listener) {
        listenerList.add(listener);
    }

    public static void removeProgressListener(PlayProgressListener listener) {
        listenerList.remove(listener);
    }

    private Disposable disposable = Observable
            .interval(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(v -> {
                for (int i = 0; i < listenerList.size(); i++) {
                    listenerList.get(i).onProgressUpdate(getCurrentPosition(), getDuration());
                }
            });

    private class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d(TAG, intent.getAction());
            handleCommandIntent(intent);
        }
    }

    private class HeadsetReceiver extends BroadcastReceiver {
        final IntentFilter filter;
        final BluetoothAdapter bluetoothAdapter;

        public HeadsetReceiver() {
            filter = new IntentFilter();
            filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY); //有线耳机拔出变化
            filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); //蓝牙耳机连接变化

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (isRunningForeground) {
//
//            }
        }
    }

    /**
     * 获取正在播放进度
     *
     * @return 进度
     */
    public long getCurrentPosition() {
        if (mPlayer != null && mPlayer.isInitialized()) {
            return mPlayer.position();
        } else {
            return 0;
        }
    }

    /**
     * 获取总时长
     *
     * @return 时长
     */
    public long getDuration() {
        if (mPlayer != null && mPlayer.isInitialized() && mPlayer.isPrepared()) {
            return mPlayer.duration();
        }
        return 0;
    }

    /**
     * 是否准备播放
     *
     * @return
     */
    public boolean isPrepared() {
        if (mPlayer != null) {
            return mPlayer.isPrepared();
        }
        return false;
    }

    private class MusicPlayerHandler extends Handler {
        private final WeakReference<MusicPlayerService> mService;
        private float mCurrentVolume = 1.0f;

        public MusicPlayerHandler(final MusicPlayerService service, final Looper looper) {
            super(looper);
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MusicPlayerService service = mService.get();
            synchronized (mService) {
                switch (msg.what) {
                    case VOLUME_FADE_DOWN:
                        mCurrentVolume -= 0.05f;
                        if (mCurrentVolume > 0.2f) {
                            sendEmptyMessageDelayed(VOLUME_FADE_DOWN, 10);
                        } else {
                            mCurrentVolume = 0.2f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case VOLUME_FADE_UP:
                        mCurrentVolume += 0.01f;
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(VOLUME_FADE_UP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                    case TRACK_WENT_TO_NEXT:
                        mMain.post(() -> service.next(true));
                        break;
                }
            }
        }
    }

    public class HeadsetPlugInReceiver extends BroadcastReceiver {
        final IntentFilter filter;

        public HeadsetPlugInReceiver() {
            filter = new IntentFilter();
            if (Build.VERSION.SDK_INT >= 21) {
                filter.addAction(AudioManager.ACTION_HEADSET_PLUG);
            } else {
                filter.addAction(Intent.ACTION_HEADSET_PLUG);
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("state")) {
                final boolean isPlugin = intent.getExtras().getInt("state") == 1;
                LogUtils.e(TAG, "耳机插入状态：" + isPlugin);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = SERVICE_CMD.equals(action) ? intent.getStringExtra(CMD_NAME) : null;
        if (DEBUG) {
            LogUtils.d(TAG, "handleCommandIntent: action = " + action + ", command = " + command);
        }

        if (CMD_NEXT.equals(command) || ACTION_NEXT.equals(action)) {

        }
    }

    public void notifyChange(final String what) {
        if (DEBUG) LogUtils.d(TAG, "notifyChange: what  = " + what);
        switch (what) {
            case META_CHANGED:
                mFloatLyricViewManager.
                break;
        }
    }

    /**
     * 【在线音乐】加入播放队列并播放音乐
     *
     * @param music
     */
    public void play(Music music) {
        if (music == null) return;
        if (mPlayingPos == -1 || mPlayQueue.size() == 0) {
            mPlayQueue.add(music);
            mPlayingPos = 0;
        } else if (mPlayingPos < mPlayQueue.size()) {
            mPlayQueue.add(mPlayingPos, music);
        } else {
            mPlayQueue.add(mPlayQueue.size(), music);
        }
        LogUtils.e(TAG, music.toString());
        mPlayingMusic = music;
        playCurrentAndNext();
    }

    private void playCurrentAndNext() {
        synchronized (this) {
            if (mPlayingPos >= mPlayQueue.size() || mPlayingPos < 0) {
                return;
            }
            mPlayingMusic = mPlayQueue.get(mPlayingPos);
            notifyChange(META_CHANGED);
            LogUtils.e(TAG, "playingSongInfo:" + mPlayingMusic.toString());
            if (mPlayingMusic.getUri() == null || mPlayingMusic.getUri().equals("") ||
                    mPlayingMusic.getUri().equals("null")) {

            }
        }
    }


    /**
     * 下一首播放
     *
     * @param music 设置的歌曲
     */
    public void nextPlay(Music music) {
        if (mPlayQueue.size() == 0) {
            play(music);
        } else if (mPlayingPos < mPlayQueue.size()) {
            mPlayQueue.add(mPlayingPos + 1, music);
        }
    }


}
