package com.kenny.loadingdemo.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kenny.loadingdemo.R;

/**
 * Created by Kenny on 2016/11/4 9:38.
 * Desc：
 */

public class BottleLoading extends View {
    private static final String TAG = BottleLoading.class.getSimpleName();
    private final int bottleMarginTop = 40;
    private final int bottleWidth = 40;
    private final int bottleHeight = 120;
    private final int perItemWidth = 60;
    private final int bottlePadding = 10;

    private int defaultSize = 300;
    private int mWidth;
    private int mHeight;

    private Paint textPaint;//文字画笔
    private Paint bottlePaint;//瓶身画笔
    private Paint waterPaint;//水量画笔
    private Path path;

    private int offset = 0;

    private Handler mHandler;

    //自定义属性
    private int bottleColor;
    private int waterColor;
    private int capsuleColor;
    private int textColor;
    private String textContent;
    private float textSize;
    private int speed;

    public int getBottleColor() {
        return bottleColor;
    }

    public void setBottleColor(int bottleColor) {
        this.bottleColor = bottleColor;
    }

    public int getCapsuleColor() {
        return capsuleColor;
    }

    public void setCapsuleColor(int capsuleColor) {
        this.capsuleColor = capsuleColor;
    }

    public int getWaterColor() {
        return waterColor;
    }

    public void setWaterColor(int waterColor) {
        this.waterColor = waterColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }


    public BottleLoading(Context context) {
        this(context, null);
    }

    public BottleLoading(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottleLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottleLoading);
        bottleColor = typedArray.getColor(R.styleable.BottleLoading_bottleColor, Color.parseColor("#ffffbb33"));
        waterColor = typedArray.getColor(R.styleable.BottleLoading_waterColor, Color.parseColor("#ffffbb33"));
        capsuleColor = typedArray.getColor(R.styleable.BottleLoading_capsuleColor, Color.parseColor("#ffffbb33"));
        textColor = typedArray.getColor(R.styleable.BottleLoading_textColor, Color.parseColor("#ffffbb33"));
        textContent = typedArray.getString(R.styleable.BottleLoading_textContent);
        textSize = typedArray.getDimension(R.styleable.BottleLoading_textSize, 50);
        speed = typedArray.getInt(R.styleable.BottleLoading_speed, 7);
        typedArray.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getMyDefaultSize(defaultSize, widthMeasureSpec);
        mHeight = getMyDefaultSize(defaultSize, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

    }

    private void init() {
        path = new Path();

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);

        bottlePaint = new Paint();
        bottlePaint.setColor(bottleColor);
        bottlePaint.setAntiAlias(true);
        bottlePaint.setStyle(Paint.Style.STROKE);
        bottlePaint.setStrokeWidth(1);

        waterPaint = new Paint();
        waterPaint.setColor(waterColor);
        waterPaint.setAntiAlias(true);
        waterPaint.setStyle(Paint.Style.FILL);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (offset > mWidth)
                    offset = 0;
                offset += speed;
                invalidate();
            }
        };
    }

    private int getMyDefaultSize(int size, int measureSpec) {
        int result = size;
        //获得测量模式
        int specMode = View.MeasureSpec.getMode(measureSpec);
        //获得测量大小
        int specSize = View.MeasureSpec.getSize(measureSpec);
        //判断模式是否是 EXACTLY
        if (specMode == View.MeasureSpec.EXACTLY) {
            //如果模式是 EXACTLY 则直接使用specSize的测量大小
            result = specSize;
        } else {
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBottle(canvas, offset);
        drawLine(canvas, offset);
        drawText(canvas);
        mHandler.sendEmptyMessageDelayed(1, 50);
    }

    private void drawText(Canvas canvas) {
        //测量字体长度
        if (textContent == null || textContent.isEmpty())
            return;
        float textWidth = textPaint.measureText(textContent);
        canvas.drawText(textContent, (mWidth - textWidth) / 2, mHeight - ((mHeight - bottleMarginTop - bottleHeight) / 2), textPaint);
    }

    private void drawLine(Canvas canvas, int offset) {
        bottlePaint.setStrokeWidth(5);
        for (int i = 0; i < mWidth / perItemWidth * 2; i++) {
            int start = (i * perItemWidth) + -mWidth + bottlePadding + offset;
            int end = start + bottleWidth;
            if (start > mWidth / 2 - bottleWidth / 2 && start <= mWidth / 2 + bottleWidth) {
                canvas.drawLine(start + bottleWidth / 2, 5, start + bottleWidth / 2, 30, bottlePaint);
                canvas.drawLine(start + 15, 30, end - 15, 30, bottlePaint);
            } else if (start > mWidth / 2 + bottleWidth) {
                canvas.drawLine(start + 15 - 2, bottleMarginTop, end - 15 + 2, bottleMarginTop, bottlePaint);
            } else {
                canvas.drawLine(start + 15, 5, end - 15, 5, bottlePaint);
            }
        }
        bottlePaint.setStrokeWidth(1);
    }

    private void drawBottle(Canvas canvas, int offset) {
        for (int i = 0; i < mWidth / perItemWidth * 2; i++) {

            int start = i * perItemWidth - mWidth + bottlePadding + offset;

            path.reset();
            path.moveTo(start + 15, bottleMarginTop);
            path.lineTo(start + 15, bottleMarginTop + 5);
            path.lineTo(start + 5, bottleMarginTop + 50);
            path.lineTo(start, bottleMarginTop + 55);
            path.lineTo(start, bottleMarginTop + bottleHeight - 5);
            path.lineTo(start + 5, bottleMarginTop + bottleHeight);
            path.lineTo(start + 35, bottleMarginTop + bottleHeight);
            path.lineTo(start + 40, bottleMarginTop + bottleHeight - 5);
            path.lineTo(start + 40, bottleMarginTop + 55);
            path.lineTo(start + 35, bottleMarginTop + 50);
            path.lineTo(start + 25, bottleMarginTop + 5);
            path.lineTo(start + 25, bottleMarginTop);
            path.close();
            canvas.drawPath(path, bottlePaint);
        }

        int sc = canvas.saveLayer(0, 0, mWidth, mHeight, null);

        Paint paint = new Paint();
        canvas.drawBitmap(makeBottle(offset), 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(makeWater(offset), 0, 0, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    Bitmap makeBottle(int offset) {
        Bitmap bitmap = Bitmap.createBitmap(mWidth * 2, mHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        for (int i = 0; i < mWidth / perItemWidth * 2; i++) {
            int start = i * perItemWidth - mWidth + bottlePadding + offset;
            path.reset();
            path.moveTo(start + 15, bottleMarginTop);
            path.lineTo(start + 15, bottleMarginTop + 5);
            path.lineTo(start + 5, bottleMarginTop + 50);
            path.lineTo(start, bottleMarginTop + 55);
            path.lineTo(start, bottleMarginTop + bottleHeight - 5);
            path.lineTo(start + 5, bottleMarginTop + bottleHeight);
            path.lineTo(start + 35, bottleMarginTop + bottleHeight);
            path.lineTo(start + 40, bottleMarginTop + bottleHeight - 5);
            path.lineTo(start + 40, bottleMarginTop + 55);
            path.lineTo(start + 35, bottleMarginTop + 50);
            path.lineTo(start + 25, bottleMarginTop + 5);
            path.lineTo(start + 25, bottleMarginTop);
            path.close();
            canvas.drawPath(path, waterPaint);
        }
        return bitmap;
    }

    Bitmap makeWater(int offset) {
        Bitmap bitmap = Bitmap.createBitmap(mWidth * 2, mHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        for (int i = 0; i < mWidth / perItemWidth * 2; i++) {
            int start = i * perItemWidth - mWidth + bottlePadding + offset;
            canvas.drawRect(start, perItemWidth, start + bottleWidth, bottleMarginTop + bottleHeight, waterPaint);
        }

        return bitmap;
    }

}
