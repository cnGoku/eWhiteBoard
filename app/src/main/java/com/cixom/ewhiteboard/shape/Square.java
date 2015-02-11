//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.shape;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.cixom.ewhiteboard.HandwriterView;

public class Square extends AbsShape
{
    private int mStartX;
    private int mStartY;
    private int mX;
    private int mY;
    private Rect mInvalidRect;

    public Square(HandwriterView view, int model)
    {
        super(view, model);
        mInvalidRect = new Rect();
    }

    public void touchMove(int startX, int startY, int x, int y)
    {
        mStartX = startX;
        mStartY = startY;
        mX = x;
        mY = y;

        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect.set(mStartX - border, mStartY - border, mStartX + border, mStartY + border);
        mInvalidRect.union(x - border, y - border, x + border, y + border);
        mView.invalidate(mInvalidRect);
    }

    public void drawShape(Canvas canvas)
    {
        if ((mX > mStartX && mY > mStartY) || (mX < mStartX && mY < mStartY))
        {
            if (Math.abs(mX - mStartX) > Math.abs(mY - mStartY))
            {
                canvas.drawRect(mStartX, mStartY, mStartX + mY - mStartY, mY, mPaint);
            }
            else
            {
                canvas.drawRect(mStartX, mStartY, mX, mStartY + mX - mStartX, mPaint);
            }
        }
        else
        {
            if (Math.abs(mX - mStartX) > Math.abs(mY - mStartY))
            {
                canvas.drawRect(mStartX, mStartY, mStartX + mStartY - mY, mY, mPaint);
            }
            else
            {
                canvas.drawRect(mStartX, mStartY, mX, mStartY + mStartX - mX, mPaint);
            }
        }
    }

}
