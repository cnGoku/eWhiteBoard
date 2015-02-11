//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.shape;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.cixom.ewhiteboard.HandwriterView;

public class Oval extends AbsShape
{
    private Rect mInvalidRect;
    private RectF mDrawRect;

    public Oval(HandwriterView view, int model)
    {
        super(view, model);
        mInvalidRect = new Rect();
        mDrawRect = new RectF();
    }

    public void touchMove(int startX, int startY, int x, int y)
    {
        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect.set(startX - border, startY - border, startX + border, startY + border);
        mInvalidRect.union(x - border, y - border, x + border, y + border);
        mDrawRect.set(startX, startY, x, y);
        mView.invalidate(mInvalidRect);
    }

    public void drawShape(Canvas canvas)
    {
        canvas.drawOval(mDrawRect, mPaint);
    }

}
