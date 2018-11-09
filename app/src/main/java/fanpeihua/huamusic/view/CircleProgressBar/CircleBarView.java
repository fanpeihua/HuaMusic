package fanpeihua.huamusic.view.CircleProgressBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import fanpeihua.huamusic.R;
import fanpeihua.huamusic.utils.DpOrPxUtils;

public class CircleBarView extends View {
    private Paint rPaint;
    private Paint mProgressPaint;
    private Paint bgPaint;//绘制背景圆弧的画笔

    private float progressNum; // 可以更新的进度条数值
    private float maxNum; // 进度条最大值
    CircleBarAnim anim;

    private float progressSweepAngle; // 进度条圆弧扫过的角度
    private float startAngle; // 背景圆弧的起始角度
    private float sweepAngle; //圆弧经过的角度

    private RectF mRectF; // 绘制圆弧的矩形区域
    private float barWidth; //圆弧进度条宽度
    private int defaultSize; //自定义view默认的高度

    private int progressColor; //进度条圆弧颜色
    private int bgColor; //背景圆弧颜色

    private TextView textView;
    private OnAnimationListener onAnimationListener;

    public CircleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rPaint = new Paint();
        rPaint.setStyle(Paint.Style.STROKE);
        rPaint.setColor(Color.RED);


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleBarView);
        progressColor = typedArray.getColor(R.styleable.CircleBarView_progress_color, Color.GREEN); //默认为绿色
        bgColor = typedArray.getColor(R.styleable.CircleBarView_bg_color, Color.GRAY);
        startAngle = typedArray.getFloat(R.styleable.CircleBarView_start_angle, 0);
        sweepAngle = typedArray.getFloat(R.styleable.CircleBarView_sweep_angle, 360);
        barWidth = typedArray.getDimension(R.styleable.CircleBarView_bar_width, DpOrPxUtils.dip2px(context, 10));
        typedArray.recycle(); // typedArray用完之后需要回收防止内存泄漏


        mProgressPaint = new Paint();
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeWidth(barWidth); // 随便设置一个画笔宽度
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔为圆角

        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.STROKE); //只描边，不填充
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true); // 设置抗锯齿
        bgPaint.setStrokeWidth(barWidth);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);

        anim = new CircleBarAnim();

        progressNum = 0;
        maxNum = 100;   // 随便设的

        defaultSize = DpOrPxUtils.dip2px(context, 100);
        mRectF = new RectF();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = 50;
        float y = 50;
        RectF rectF = new RectF(x, y, x + 300, y + 300);

//        canvas.drawArc(rectF, 135, 270, false, mProgressPaint);
//        canvas.drawRect(rectF, rPaint);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, bgPaint);
        canvas.drawArc(mRectF, startAngle, progressSweepAngle, false, mProgressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = measureSize(defaultSize, heightMeasureSpec);
        int width = measureSize(defaultSize, widthMeasureSpec);
        int min = Math.min(width, height); // 获取View最短边的长度
        setMeasuredDimension(min, min); // 强制改View为以最短边为长度的正方形

        if (min >= barWidth * 2) { // 简单限制了圆弧的最大宽度
            mRectF.set(barWidth / 2, barWidth / 2, min - barWidth / 2, min - barWidth / 2);
        }
    }

    /**
     * 设置显示文字的TextView
     *
     * @param textView
     */
    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.onAnimationListener = onAnimationListener;
    }

    public interface OnAnimationListener {
        /**
         * 如何处理要显示的问题内容
         *
         * @param interpolatedTime 从0渐变成1，到1时结束动画
         * @param progressNum      进度条数值
         * @param maxNum           进度条最大值
         * @return
         */
        String howToChangeText(float interpolatedTime, float progressNum, float maxNum);

        void howToChangeProgressColor(Paint paint, float interpolatedTime, float grogressNum, float maxNum);
    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    private class CircleBarAnim extends Animation {
        public CircleBarAnim() {
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            progressSweepAngle = interpolatedTime * sweepAngle * progressNum / maxNum;//计算进度条的比例
            if (textView != null && onAnimationListener != null) {
                textView.setText(onAnimationListener.howToChangeText(interpolatedTime, progressNum, maxNum));
            }
            postInvalidate();
        }
    }

    public void setProgressNum(float progressNum, int time) {
        anim.setDuration(time);
        this.startAnimation(anim);
        this.progressNum = progressNum;
    }
}
