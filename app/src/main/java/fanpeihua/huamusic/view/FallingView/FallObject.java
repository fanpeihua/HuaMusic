package fanpeihua.huamusic.view.FallingView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class FallObject {
    private int initX;
    private int initY;
    private Random mRandom;
    private int parentWidth; // 父容器高度
    private int parentHeight; //父容器高度
    private float objectWidth; // 下落物体宽度
    private float objectHeight; // 下落物体高度


    public int initSpeed; // 初始下降速度
    public float presentX; //当前位置X坐标
    public float presentY; //当前位置Y坐标
    public float presentSpeed; //当前下降速度

    private Bitmap bitmap;
    public Builder builder;

    private static final int defaultSpeed = 10; //默认下降速度

    private boolean isSpeedRandom; // 物体初始下降比例是否随机
    private boolean isSizeRandom; // 物体初始大小比例是否随机

    public int initWindLevel; // 初始化风力等级
    private float angle; // 物体下落角度
    private boolean isWindRandom; // 物体初始风向和风力大小比例是否随机
    private boolean isWindChange; // 物体下落过程中风向和风力是否产生随机变化

    private static final int defaultWindLevel = 0; //默认风力等级
    private static final int defaultWindSpeed = 10; //默认单位风速
    private static final float HALF_PI = (float) Math.PI / 2; //

    public FallObject(Builder builder, int parentWidth, int parentHeight) {
        mRandom = new Random();
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        initX = mRandom.nextInt(parentWidth); //随机物体的X坐标
        initY = mRandom.nextInt(parentHeight) - parentHeight; //随机物体的Y坐标，并让物体一开始从屏幕顶部下落
        presentX = initX;
        presentY = initY;

        this.builder = builder;
        isSpeedRandom = builder.isSpeedRandom;
        isSizeRandom = builder.isSizeRandom;
        isWindRandom = builder.isWindRandom;
        isWindChange = builder.isWindChange;

        initSpeed = builder.initSpeed;

        presentSpeed = initSpeed;
        bitmap = builder.bitmap;
        objectWidth = bitmap.getWidth();
        objectHeight = bitmap.getHeight();


        randomSpeed();
        randomSize();
        randomWind();
    }

    private FallObject(Builder builder) {
        this.builder = builder;
        isSpeedRandom = builder.isSpeedRandom;
        isSizeRandom = builder.isSizeRandom;
        initSpeed = builder.initSpeed;
        bitmap = builder.bitmap;

        initWindLevel = builder.initWindLevel;
        isWindRandom = builder.isWindRandom;
        isWindChange = builder.isWindChange;
    }

    public static final class Builder {
        private int initSpeed;
        private Bitmap bitmap;
        private boolean isSpeedRandom;
        private boolean isSizeRandom;

        private boolean isWindRandom;
        private boolean isWindChange;
        private int initWindLevel;

        public Builder(Bitmap bitmap) {
            this.initSpeed = defaultSpeed;
            this.bitmap = bitmap;
            this.isSpeedRandom = false;
            this.isSizeRandom = false;
            this.isWindChange = false;
            this.isWindRandom = false;
        }


        public Builder(Drawable drawable) {
            this.initSpeed = defaultSpeed;
            this.bitmap = drawableToBitmap(drawable);
            this.isSpeedRandom = false;
            this.isSizeRandom = false;
            this.isWindChange = false;
            this.isWindRandom = false;
        }

        /**
         * 设置物体的下落速度
         *
         * @param speed
         * @return
         */
        public Builder speed(int speed) {
            this.initSpeed = speed;
            return this;
        }

        public Builder setSpeed(int speed, boolean isRandomSpeed) {
            this.initSpeed = speed;
            this.isSpeedRandom = isRandomSpeed;
            return this;
        }

        public Builder setWind(int level, boolean isWindRandom, boolean isWindChange) {
            this.initWindLevel = level;
            this.isWindRandom = isWindRandom;
            this.isWindChange = isWindChange;
            return this;
        }

        public Builder setSize(int w, int h) {
            this.bitmap = changeBitmapSize(this.bitmap, w, h);
            return this;
        }

        public Builder setSize(int w, int h, boolean isRandomSize) {
            this.bitmap = changeBitmapSize(this.bitmap, w, h);
            this.isSizeRandom = isRandomSize;
            return this;
        }

        public FallObject build() {
            return new FallObject(this);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ?
                        Bitmap.Config.ARGB_8888 :
                        Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 绘制物体对象
     *
     * @param canvas
     */
    public void drawObject(Canvas canvas) {
        moveObject();
        canvas.drawBitmap(bitmap, presentX, presentY, null);
    }

    /**
     * 移动物体对象
     */
    public void moveObject() {
        moveX();
        moveY();
        if (presentY > parentHeight || presentX < -bitmap.getWidth() ||
                presentX > parentWidth + bitmap.getWidth()) {
            reset();
        }
    }

    public static Bitmap changeBitmapSize(Bitmap bitmap, int newW, int newH) {
        int oldW = bitmap.getWidth();
        int oldH = bitmap.getHeight();
        // 计算缩放比例
        float scaledWidth = ((float) newW) / oldW;
        float scaledHeight = ((float) newH) / oldH;

        //取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaledWidth, scaledHeight);

        //得到新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, oldW, oldH, matrix, true);
        return bitmap;
    }

    private void moveX() {
        presentX += defaultWindSpeed * Math.sin(angle);
        if (isWindChange) {
            angle += (float) (mRandom.nextBoolean() ? -1 : 1) * Math.random() * 0.0025;
        }
    }

    private void moveY() {
        presentY += presentSpeed;
    }

    /**
     * 重置object位置
     */
    private void reset() {
        presentY = -objectHeight;
//        presentSpeed = initSpeed;
        randomSpeed();
        randomWind();
    }

    /**
     * 随机物体初始下落速度
     */
    private void randomSpeed() {
        if (isSpeedRandom) {
            presentSpeed = (float) ((mRandom.nextInt(3) + 1) * 0.1 + 1) * initSpeed;
        } else {
            presentSpeed = initSpeed;
        }
    }

    /**
     * 随机物体初始大小比例
     */
    private void randomSize() {
        if (isSizeRandom) {
            float r = (mRandom.nextInt(10) + 1) * 0.1f;
            float rW = r * builder.bitmap.getWidth();
            float rH = r * builder.bitmap.getHeight();
            bitmap = changeBitmapSize(builder.bitmap, (int) rW, (int) rH);
        } else {
            bitmap = builder.bitmap;
        }
        objectWidth = bitmap.getWidth();
        objectHeight = bitmap.getHeight();
    }

    private void randomWind() {
        if (isWindRandom) {
            angle = (float) ((mRandom.nextBoolean() ? -1 : 1) * Math.random() * initWindLevel / 50);
        } else {
            angle = (float) initWindLevel / 50;
        }

        if (angle > HALF_PI) {
            angle = HALF_PI;
        } else if (angle < -HALF_PI) {
            angle = -HALF_PI;
        }
    }
}
