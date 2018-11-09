package fanpeihua.huamusic.player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class MusicPlayerEngine implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {

    private String TAG = "MusicPlayerEngine";

    private final WeakReference<MusicPlayerService> mService;

    private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();

    private Handler mHandler;

    // 是否已经初始化
    private boolean mIsInitialized = false;
    // 是否准备好
    private boolean mIsPrepared = false;

    MusicPlayerEngine(final MusicPlayerService service) {
        mService = new WeakReference<>(service);
        mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    public void setDataSource(final String path) {
        mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
    }

    private boolean setDataSourceImpl(final MediaPlayer player, final String path) {
        if (path == null) return false;
        try {
            if (player.isPlaying()) player.stop();
            mIsPrepared = false;
            player.reset();
            if (path.startsWith("content://")) {
                player.setDataSource(mService.get(), Uri.parse(path));
            } else {
                player.setDataSource(path);
            }
            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnBufferingUpdateListener(this);
            player.setOnErrorListener(this);
            player.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setHandler(final Handler handler) {
        mHandler = handler;
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public void start() {
        mCurrentMediaPlayer.start();
    }

    public void stop() {
        try {
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
            mIsPrepared = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        mCurrentMediaPlayer.release();
    }

    public void pause() {
        mCurrentMediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mCurrentMediaPlayer.isPlaying();
    }

    /**
     * getDuration 只能在prepared之后才能调用，否则会报-38错误
     *
     * @return
     */
    public long duration() {
        if (mIsPrepared) {
            return mCurrentMediaPlayer.getDuration();
        } else return 0;
    }

    public long position() {
        try {
            return mCurrentMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void seek(final long whereto) {
        mCurrentMediaPlayer.seekTo((int) whereto);
    }

    public void setVolume(final float vol) {
        mCurrentMediaPlayer.setVolume(vol, vol);
    }

    public int getAudioSessionId() {
        return mCurrentMediaPlayer.getAudioSessionId();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        LogUtils.e(TAG, "onBufferingUpdate" + percent);
        Message message = mHandler.obtainMessage(MusicPlayerService.PREPARE_ASYNC_UPDATE, percent);
        mHandler.sendMessage(message);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtils.e(TAG, "onCompletion");
        if (mp == mCurrentMediaPlayer) {
            mHandler.sendEmptyMessage(MusicPlayerService.TRACK_WENT_TO_NEXT);
        } else {
            mService.get().mWakeLock.acquire(30000);
            mHandler.sendEmptyMessage(MusicPlayerService.TRACK_PLAY_ENDED);
            mHandler.sendEmptyMessage(MusicPlayerService.RELEASE_WAKELOCK);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtils.e(TAG, "Music Server Error what: " + what + " extra: " + extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                final MusicPlayerService service = mService.get();
//                final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId(),
//                        service.getTitle());
                mIsInitialized = false;
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = new MediaPlayer();
                mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
//                Message msg = mHandler.obtainMessage(MusicPlayerService.TRACK_PLAY_ERROR, errorInfo);
//                mHandler.sendMessageDelayed(msg, 2000);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mIsPrepared = true;
        Message message = mHandler.obtainMessage(MusicPlayerService.PLAYER_PREPARED);
        mHandler.sendMessage(message);
    }

    private class TrackErrorInfo {
        private String audioId;
        private String trackName;

        public TrackErrorInfo(String audioId, String tackName) {
            this.audioId = audioId;
            this.trackName = trackName;
        }

        public String getAudioId() {
            return audioId;
        }

        public void setAudioId(String audioId) {
            this.audioId = audioId;
        }

        public String getTrackName() {
            return trackName;
        }

        public void setTrackName(String trackName) {
            this.trackName = trackName;
        }
    }
}
