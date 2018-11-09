package fanpeihua.huamusic.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;

import fanpeihua.huamusic.R;
import fanpeihua.huamusic.view.CircleProgressBar.CircleBarView;
import fanpeihua.huamusic.view.FallingView.FallObject;
import fanpeihua.huamusic.view.FallingView.FallingView;
import fanpeihua.huamusic.view.MusicButton.MusicButton;

public class ViewActivity extends AppCompatActivity {

//    Paint snowPaint;
//    Bitmap bitmap;
//    Canvas bitmapCanvas;
//    FallingView fallingView;

//    CircleBarView circleBarView;
//    TextView mTextView;

    MusicButton mMusicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        initView();
    }

    // 雪花
//    private void initView() {
//        snowPaint = new Paint();
//        snowPaint.setColor(Color.WHITE);
//        snowPaint.setStyle(Paint.Style.FILL);
//
//        bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
//        bitmapCanvas = new Canvas(bitmap);
//        bitmapCanvas.drawCircle(15, 15, 15, snowPaint);
//
//        FallObject.Builder builder = new FallObject.Builder(getResources().getDrawable(R.mipmap.img_snow));
//        FallObject fallObject = builder
//                .setSpeed(5, true)
//                .setSize(60, 60, true)
//                .setWind(5, true, true)
//                .build();
//
//        fallingView = findViewById(R.id.fallingView);
//        fallingView.addFallObject(fallObject, 40);
//    }

//    private void initView() {
////        circleBarView = findViewById(R.id.circle_view);
////        mTextView = findViewById(R.id.text_progress);
//////        circleBarView.setProgressNum(100, 3000);
////
////        circleBarView.setOnAnimationListener(
////                new CircleBarView.OnAnimationListener() {
////                    @Override
////                    public String howToChangeText(float interpolatedTime, float progressNum, float maxNum) {
////                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
////                        String s = decimalFormat.format(interpolatedTime * progressNum / maxNum * 100) + "%";
////                        return s;
////                    }
////
////                    @Override
////                    public void howToChangeProgressColor(Paint paint, float interpolatedTime, float grogressNum, float maxNum) {
////
////                    }
////                }
////
////        );
////        circleBarView.setTextView(mTextView);
////        circleBarView.setProgressNum(80, 3000);
////    }


    private void initView() {
        mMusicButton = findViewById(R.id.music_btn);
        mMusicButton.playMusic();
    }
}
