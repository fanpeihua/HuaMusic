package fanpeihua.huamusic.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import net.steamcrafted.materialiconlib.MaterialIconView;

import fanpeihua.huamusic.R;

public class FloatLyricView extends LinearLayout implements View.OnClickListener {
    /**
     * 记录窗口的最小宽度
     */
    public int viewWidth;

    /**
     * 记录窗口的最小高度
     */
    public int viewHeight;

    /**
     * 记录状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;


    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;
    private float mFontSize;
    private int mFontColor;
    private boolean mMovement;
    private boolean isHiddenSettings;

    public LyricTextView mLyricText;
    public TextView mTitle;
    public SeekBar mSizeSeekBar;
    public ColorSeekBar mColorSeekBar;
    private MaterialIconView mLockButton, mPreButton, mNextButton, mPlayButton, mSettingsButton;
    private ImageButton mCloseButton;
    private ImageButton mMusicButton;
    private LinearLayout mSettingLinearLayout;
    private LinearLayout mRelLyricView;
    private LinearLayout mLinLyricView;
    private FrameLayout mFrameBackground;
    private View mRootView;

    public FloatLyricView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mRootView = LayoutInflater.from(context).inflate(R.layout.float_lyric_view,this);
        FrameLayout view = findViewById(R.id.small_window_layout);
        init();
    }

    public FloatLyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasureSize(widthMeasureSpec);
    }

    private int getMeasureSize(int measureSpec) {
        if (measureSpec != 0) {

        } else if (measureSpec == MeasureSpec.AT_MOST) {

        } else if (measureSpec == MeasureSpec.EXACTLY) {
            return 1;
        }
        return 0;
    }

    @Override
    public void onClick(View v) {

    }
}
