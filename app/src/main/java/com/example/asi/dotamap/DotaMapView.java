package com.example.asi.dotamap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by asi on 2016/10/18.
 */

public class DotaMapView extends View {
    private  Context mContext;
    /**
     * 背景圖片
     */
    private Bitmap mBgImg;
    /**
     * 控件的宽
     */
    private int mWidth;
    /**
     * 控件的高
     */
    private int mHeight;

    /**
     * 绘制背景图片位置
     */
    private Rect  mDestRect;


    /**
     * 眼位半径
     */
    private int obRadius,ssRadius;
    /**
     * 眼位圆圈宽度
     */
    private   int PAINT_OUT_PX;
    /**
     * 眼位内圆半径
     */
    private   int PAINT_IN_PX;
    /**
     * 眼位圆圈颜色
     */
    private  Paint mPaint,mPaintOut,mPaintIn,mPaintCenter;
    /**
     * 服务端地图大小
     */
    private static final int MAP_SIZE= 127;
    private int mDOutColor,mSSColor,mROutColor,mOBColor;
    public DotaMapView(Context context) {
        super(context);
    }

    public DotaMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mDestRect = new Rect();
        //外环画笔
        mPaintOut = new Paint();
        //中间画笔
        mPaintCenter = new Paint();
        //实心圆画笔
        mPaintIn = new Paint();
        //背景画笔
        mPaint = new Paint();

        mBgImg = BitmapFactory.decodeResource(getResources(), R.mipmap.math_data_map);
        //真眼半径
        obRadius = dip2px(mContext,67);
        //假眼半径
        ssRadius =dip2px(mContext,87);
        //外环宽度
        PAINT_OUT_PX = dip2px(mContext,2);
        //内圆半径
        PAINT_IN_PX = dip2px(mContext,11);
        //夜魇外环颜色
        mROutColor = getResources().getColor(R.color.cff513a);
        //真眼实心颜色
        mOBColor = getResources().getColor(R.color.c60b21d);
        //天辉外环颜色
        mDOutColor = getResources().getColor(R.color.c0ebbf4);
        //假眼实心颜色
        mSSColor = getResources().getColor(R.color.cffba16);

        //设置外环样式
        mPaintOut.setStrokeWidth(PAINT_OUT_PX); // 设置圆环的宽度
        mPaintOut.setAntiAlias(true); // 消除锯齿
        mPaintOut.setStyle(Paint.Style.STROKE); // 设置空心

        //设置中间样式
        mPaintCenter.setStyle(Paint.Style.FILL); // 设置实心
        mPaintCenter.setAntiAlias(true); // 消除锯齿
        mPaintCenter.setColor(Color.BLACK);
        mPaintCenter.setAlpha(100);
     //设置实心样式
        mPaintIn.setStyle(Paint.Style.FILL); // 设置实心
        mPaintIn.setAntiAlias(true); // 消除锯齿

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mWidth = specSize;
        } else {
            // 由图片决定宽
            int desireByImg = getPaddingLeft() + getPaddingRight() + mBgImg.getWidth();
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mWidth = Math.min(desireByImg, specSize);
            }

        }
        /***
         * 方形高=宽
         */
        mHeight = mWidth;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGroud(canvas);
        drawrROs(canvas);
        drawRSs(canvas);
        drawDOs(canvas);
        drawDSs(canvas);
    }

    //绘制背景图片
    private void drawBackGroud(Canvas canvas) {
        mDestRect.left = 0;
        mDestRect.top = 0;
        mDestRect.right = mWidth;
        mDestRect.bottom = mHeight;
        canvas.drawBitmap(mBgImg, null, mDestRect, mPaint);
    }

    //绘制t天辉真眼
    private void drawDOs(Canvas canvas) {
        //服务端给的坐标是在dota地图里的坐标，我们要换算成控件的坐标，比如x: 14,y: 68，地图宽高是127
                int  x= 14* mWidth / MAP_SIZE;
                int  y= 68* mHeight / MAP_SIZE;
                mPaintOut.setColor(mDOutColor);
                mPaintIn.setColor(mOBColor);
        //外环的宽度是以外环中心为中点，像内外同时等量扩散的，比如一个半径为10的圆，外环半径为2，如果直接以半径为10画，那么整个圆的半径就是11。
        //所以要重新计算半径：整个圆半径-圆环宽度/2
                canvas.drawCircle( x,y,obRadius-PAINT_OUT_PX/2,mPaintOut);
        //绘制中间半透明
                canvas.drawCircle( x,y,obRadius-PAINT_OUT_PX,mPaintCenter);
        //绘制实心圆
                canvas.drawCircle( x,y,PAINT_IN_PX,mPaintIn);
        }
    //绘制t天辉假眼
    private void drawDSs(Canvas canvas) {
        //服务端给的坐标是在dota地图里的坐标，我们要换算成控件的坐标，x: 90,y: 52，地图宽高是127

                int  x=90 * mWidth / MAP_SIZE;
                int  y= 52 * mHeight / MAP_SIZE;
                mPaintOut.setColor(mDOutColor);
                mPaintIn.setColor(mSSColor);
                canvas.drawCircle( x,y,ssRadius-PAINT_OUT_PX/2,mPaintOut);
                canvas.drawCircle( x,y,ssRadius-PAINT_OUT_PX,mPaintCenter);
                canvas.drawCircle( x,y,PAINT_IN_PX,mPaintIn);
    }

    //绘制夜魇假眼
    private void drawRSs(Canvas canvas) {
        //服务端给的坐标是在dota地图里的坐标，我们要换算成控件的坐标，x: 22,y: 80，地图宽高是127
                int  x=22 * mWidth / MAP_SIZE;
                int  y= 80 * mHeight / MAP_SIZE;
                mPaintOut.setColor(mROutColor);
                mPaintIn.setColor(mSSColor);
                canvas.drawCircle( x,y,ssRadius-PAINT_OUT_PX/2,mPaintOut);
                canvas.drawCircle( x,y,ssRadius-PAINT_OUT_PX,mPaintCenter);
                canvas.drawCircle( x,y,PAINT_IN_PX,mPaintIn);
    }
    //绘制夜魇真眼
    private void drawrROs(Canvas canvas) {
        //服务端给的坐标是在dota地图里的坐标，我们要换算成控件的坐标，x: 100,y: 52，地图宽高是127


                int  x= 100 * mWidth / MAP_SIZE;
                int  y=52 * mHeight / MAP_SIZE;
                mPaintOut.setColor(mROutColor);
                mPaintIn.setColor(mOBColor);
                canvas.drawCircle( x,y,obRadius-PAINT_OUT_PX/2,mPaintOut);
                canvas.drawCircle( x,y,obRadius-PAINT_OUT_PX,mPaintCenter);
                canvas.drawCircle( x,y,PAINT_IN_PX,mPaintIn);
    }

    /**
     *
     * @param context 上下文
     * @param dpValue dp数值
     * @return dp to  px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);

    }
}
