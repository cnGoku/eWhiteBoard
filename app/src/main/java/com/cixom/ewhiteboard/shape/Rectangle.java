//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.shape;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.cixom.ewhiteboard.HandwriterView;

public class Rectangle extends AbsShape
{
    private int mX;
    private int mY;
    private int mStartX;
    private int mStartY;
    private Rect mInvalidRect;

    public Rectangle(HandwriterView view, int model)
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
        canvas.drawRect(mStartX, mStartY, mX, mY, mPaint);
    }

}
