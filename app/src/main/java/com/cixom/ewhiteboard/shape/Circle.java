//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.shape;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.cixom.ewhiteboard.HandwriterView;

public class Circle extends AbsShape
{
    private Rect mInvalidRect;

    private int cx;
    private int cy;
    private int radius;

    public Circle(HandwriterView view, int model)
    {
        super(view, model);
        mInvalidRect = new Rect();
    }

    public void touchMove(int startX, int startY, int x, int y)
    {
        cx = (int) ((x + startX) / 2);
        cy = (int) ((y + startY) / 2);
        radius = (int) Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2)) / 2;

        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect.set(cx - radius - border, cy - radius - border, cx + radius + border, cy + radius + border);
        mView.invalidate(mInvalidRect);
    }

    public void drawShape(Canvas canvas)
    {
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

}
