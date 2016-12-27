package com.kenny.loadingdemo.customView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kenny.loadingdemo.R;

/**
 * Created by Kenny on 2016/12/21 10:58.
 * Desc：
 */

public class GhostLoadingView extends View {
    private static final String TAG = GhostLoadingView.class.getSimpleName();
    private int duration;//动画持续时间
    private int feet = 3;
    private int eyesSize;
    private int mWidth;//View宽
    private int mHeight;//View高
    private int drawWidth;//作图宽
    private int drawHeight;//作图高
    private int defaultHeight = 300;//默认高度
    private int defaultWidth = 150;//默认宽度
    private int perDistance;//每一份的高度
    private int paddingLeft;//左边界
    private int paddingTop;//上边界
    private float offset = 0;//偏移量

    private Paint ghostPaint;//幽灵画笔
    private Paint shadowPaint;//阴影画笔

    private ValueAnimator anim;//动画效果

    private int ghostColor;//ghost颜色
    private int eyesColor;
    private int shadowColor;

    public GhostLoadingView(Context context) {
        this(context, null);
    }

    public GhostLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GhostLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GhostLoading);
        ghostColor = typedArray.getColor(R.styleable.GhostLoading_ghostColor, ContextCompat.getColor(getContext(), android.R.color.white));
        eyesColor = typedArray.getColor(R.styleable.GhostLoading_eyesColor, ContextCompat.getColor(getContext(), android.R.color.black));
        shadowColor = typedArray.getColor(R.styleable.GhostLoading_shadowColor, ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        duration = typedArray.getInteger(R.styleable.GhostLoading_breedTime, 2 * 1000);
        eyesSize = typedArray.getInteger(R.styleable.GhostLoading_eyesSize, 10);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getMyDefaultSize(defaultWidth, widthMeasureSpec);
        mHeight = getMyDefaultSize(defaultHeight, heightMeasureSpec);
        if (mWidth > mHeight) {
            drawHeight = mHeight;
            drawWidth = drawHeight / 2;
        } else if (mWidth * 2 >= mHeight) {
            drawHeight = mHeight;
            drawWidth = drawHeight / 2;
        } else if (mWidth <= mHeight / 2) {
            drawHeight = mWidth * 2;
            drawWidth = mWidth;
        }
        perDistance = drawHeight / 10;
        Log.i(TAG, "onMeasure: " + mWidth + " " + mHeight + " " + perDistance + " " + drawWidth + " " + drawHeight);
        setMeasuredDimension(mWidth, mHeight);
        init();
    }

    private void init() {
        ghostPaint = new Paint();
        ghostPaint.setAntiAlias(true);
        ghostPaint.setStyle(Paint.Style.FILL);
        ghostPaint.setColor(ghostColor);

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(shadowColor);

        paddingLeft = (mWidth - drawWidth) / 2;
        paddingTop = (mHeight - drawHeight) / 2;

        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(makeGhost(drawWidth, perDistance * 6), paddingLeft, perDistance * 2 + paddingTop - offset, null);
        float scale = 1 - offset / perDistance * 0.2f;
        Bitmap shadow = makeShadow(drawWidth, perDistance, scale);
        canvas.drawBitmap(shadow, paddingLeft + (drawWidth - shadow.getWidth()) * 0.5f, perDistance * 9 + paddingTop + (perDistance - shadow.getHeight()) * 0.5f, null);

    }

    private Bitmap makeGhost(int w, int h) {
        Bitmap bpGhost = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bpGhost);
        int perRange = w / feet;
        canvas.drawOval(0, 0, w, perDistance * 4, ghostPaint);
        canvas.drawRect(0, perDistance * 2, w, perDistance * 5, ghostPaint);
        for (int i = 0; i < feet; i++) {
            canvas.drawOval(perRange * i, perDistance * 4, perRange * (i + 1), perDistance * 6, ghostPaint);
        }

        //draw eyes
        ghostPaint.setColor(eyesColor);
        for (int i = 0; i < 2; i++) {
            canvas.drawCircle(perRange * (i + 1), perDistance * 3, eyesSize, ghostPaint);
        }
        ghostPaint.setColor(ghostColor);

        return bpGhost;
    }

    private Bitmap makeShadow(int w, int h, float scale) {
        Bitmap bpShadow = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bpShadow);
        canvas.drawOval(0, 0, w, h, shadowPaint);

        //缩放bitmap
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(bpShadow, 0, 0, w, h, matrix, true);
    }

    private int getMyDefaultSize(int size, int measureSpec) {
        int result = size;
        //获得测量模式
        int specMode = MeasureSpec.getMode(measureSpec);
        //获得测量大小
        int specSize = MeasureSpec.getSize(measureSpec);
        //判断模式是否是 EXACTLY
        if (specMode == MeasureSpec.EXACTLY) {
            //如果模式是 EXACTLY 则直接使用specSize的测量大小
            result = specSize;
        } else {
            //如果是其他两个模式，先设置一个默认大小值
            //如果是 AT_MOST 也就是 wrap_content 的话，就取默认值 default 和 specSize 中小的一个为准。
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void startAnimation() {
        anim = ValueAnimator.ofFloat(0, perDistance, 0);
        anim.setDuration(duration)
                .setRepeatCount(-1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float) anim.getAnimatedValue();
                postInvalidate();
            }
        });
        anim.start();
    }

}
