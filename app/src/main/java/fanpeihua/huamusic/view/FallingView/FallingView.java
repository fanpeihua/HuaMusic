package fanpeihua.huamusic.view.FallingView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

public class FallingView extends View {

    private Context mContext;
    private AttributeSet mAttrs;

    private int viewWidth;
    private int viewHeight;

    private static final int defaultWidth = 600; // 默认宽度
    private static final int defaultHeight = 1000; //默认高度
    private static final int intervalTime = 5; //重绘间隔时间

    private Paint testPaint;
    private int snowY;

    private List<FallObject> fallObjects;

    public FallingView(Context context) {
        super(context);
        mContext = context;
        init();
    }


    public FallingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        mAttrs = attrs;
        init();
    }

    private void init() {
        // 初始化画笔
//        testPaint = new Paint();
//        testPaint.setColor(Color.WHITE);
//        testPaint.setStyle(Paint.Style.FILL);
//        snowY = 0;
        fallObjects = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureSize(defaultHeight, heightMeasureSpec);
        int width = measureSize(defaultWidth, widthMeasureSpec);
        setMeasuredDimension(width, height);

        viewWidth = width;
        viewHeight = height;
    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawCircle(100, snowY, 25, testPaint);

        if (fallObjects.size() > 0) {
            for (int i = 0; i < fallObjects.size(); i++) {
                fallObjects.get(i).drawObject(canvas);
            }
            getHandler().postDelayed(mRunnable, intervalTime);
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public void addFallObject(final FallObject fallObject, final int num) {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < num; i++) {
                    FallObject newFallObject = new FallObject(fallObject.builder,
                            viewWidth, viewHeight);
                    fallObjects.add(newFallObject);
                }
                invalidate();
                return true;
            }
        });
    }

}
