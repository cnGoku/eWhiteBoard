//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

import com.cixom.ewhiteboard.R;

public class GalleryFlow extends Gallery
{
    private Camera mCamera = new Camera();
    private int mMaxRotationAngle = 80;
    private int mMaxZoom = -120;
    private int mCoveflowCenter;

    private boolean mNeedTrans;
    private boolean mNeedZoom;

    public GalleryFlow(Context context)
    {
        super(context);
        this.setStaticTransformationsEnabled(true);
    }

    public GalleryFlow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public GalleryFlow(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        // 读取参数
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GalleryStyle);
        mNeedTrans = a.getBoolean(R.styleable.GalleryStyle_needTrans, false);
        mNeedZoom = a.getBoolean(R.styleable.GalleryStyle_needZoom, false);
        a.recycle();
        this.setStaticTransformationsEnabled(true);
    }

    public int getMaxRotationAngle()
    {
        return mMaxRotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle)
    {
        mMaxRotationAngle = maxRotationAngle;
    }

    public int getMaxZoom()
    {
        return mMaxZoom;
    }

    public void setMaxZoom(int maxZoom)
    {
        mMaxZoom = maxZoom;
    }

    private int getCenterOfCoverflow()
    {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    private static int getCenterOfView(View view)
    {
        return view.getLeft() + view.getWidth() / 2;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t)
    {
        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();
        int rotationAngle = 0;

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);

        if (childCenter == mCoveflowCenter)
        {
            if (mNeedZoom)
            {
                transformImageBitmap(child, t, 0);
            }

            if (mNeedTrans)
            {
                child.setAlpha(255);
            }
        }
        else
        {
            float f = ((float) (mCoveflowCenter - childCenter) / childWidth);

            if (mNeedZoom)
            {
                rotationAngle = (int) (f * mMaxRotationAngle);

                if (Math.abs(rotationAngle) > mMaxRotationAngle)
                {
                    rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
                }

                transformImageBitmap(child, t, rotationAngle);
            }

            if (mNeedTrans)
            {
                int x = (int) (f * 255);

                // [-100,100]
                if (Math.abs(x) > 150)
                {
                    x = (x < 0) ? -150 : 150;
                }

                child.setAlpha(255 - Math.abs(x));
            }
        }

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        mCoveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void transformImageBitmap(View child, Transformation t, int rotationAngle)
    {
        mCamera.save();
        final Matrix imageMatrix = t.getMatrix();
        final int imageHeight = child.getLayoutParams().height;
        final int imageWidth = child.getLayoutParams().width;
        final int rotation = Math.abs(rotationAngle);

        // 在Z轴上正向移动camera的视角，实际效果为放大图片。
        mCamera.translate(0.0f, 0.0f, 100.0f);

        if (rotation < mMaxRotationAngle)
        {
            float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
            mCamera.translate(0.0f, 0.0f, zoomAmount);
        }

        mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));

        mCamera.restore();
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2)
    {
        return e2.getX() > e1.getX();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        int kEvent;
        if (isScrollingLeft(e1, e2))
        {
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        }
        else
        {
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;
    }

}
