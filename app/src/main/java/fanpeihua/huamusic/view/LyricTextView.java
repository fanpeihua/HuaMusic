package fanpeihua.huamusic.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import fanpeihua.huamusic.view.lyric.LyricInfo;

public class LyricTextView extends View {
    private static final String TAG = "LyricTextView";
    private int mLineCount = 0; // 行数
    private int mLineHeight;    // 行高

    private float mShaderWidth = 0; // 渐变过渡的距离
    private int mCurrentPlayLine = 0; // 当前播放位置对应的行数

    private int mDefaultMargin = 12;

    private int mDefaultSize = 35; //默认歌词大小
    private float fontSize = 16;// 设置字体大小
    private int fontColor = Color.RED; // 设置字体颜色

    private LyricInfo mLyricInfo;
    private String mDefaultHint = "音乐端";
    private Paint mTextPaint, mHighLigthPaint; //默认画笔、已读歌词画笔

    /**
     * 是否有歌词
     */
    private boolean hasLyric = false;

    /**
     * 当前歌词的第几个字
     */
    private int lyricsWordIndex = -1;

    /**
     * 已经播放的时间
     */
    private int lyricsWordHLEDTime = 0;

    /**
     * 当前歌词第几个字 已经播放的长度
     */
    private float lineLyricsHLWidth = 0;

    private Context context;

    private String content;
    private long mStartMillis, mCurrentMillis, mEndMillis, mDuration;


    public LyricTextView(Context context) {
        super(context);
        init(context);
    }

    public LyricTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LyricTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

    }
}
