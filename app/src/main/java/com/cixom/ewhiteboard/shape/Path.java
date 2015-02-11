//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.shape;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.cixom.ewhiteboard.HandwriterView;
import com.cixom.ewhiteboard.SerializablePath;

public class Path extends AbsShape
{
    private Rect mInvalidRect;
    protected SerializablePath mPath;

    private float mStartX = 0;
    private float mStartY = 0;

    public Path(HandwriterView view, int model)
    {
        super(view, model);
        mPath = new SerializablePath();
        mInvalidRect = new Rect();
    }

    public void touchMove(int startX, int startY, int x, int y)
    {
        // 判断是不是down事件
        if (mStartX == 0 && mStartY == 0)
        {
            mStartX = startX;
            mStartY = startY;
            mPath.moveTo(mStartX, mStartY);
        }

        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect
                .set((int) mStartX - border, (int) mStartY - border, (int) mStartX + border, (int) mStartY + border);

        float mCurveEndX = (x + mStartX) / 2;
        float mCurveEndY = (y + mStartY) / 2;

        // 使用贝赛尔曲线
        mPath.quadTo(mStartX, mStartY, mCurveEndX, mCurveEndY);

        mInvalidRect.union((int) mCurveEndX - border, (int) mCurveEndY - border, (int) mCurveEndX + border,
                (int) mCurveEndY + border);

        mStartX = x;
        mStartY = y;

        mView.invalidate(mInvalidRect);
    }

    public void drawShape(Canvas canvas)
    {
        canvas.drawPath(mPath, mPaint);
    }

}
