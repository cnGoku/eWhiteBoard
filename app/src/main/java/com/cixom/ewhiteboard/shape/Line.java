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

public class Line extends AbsShape
{
    private Rect mInvalidRect;
    protected SerializablePath mPath;

    public Line(HandwriterView view, int model)
    {
        super(view, model);
        mPath = new SerializablePath();
        mInvalidRect = new Rect();
    }

    public void touchMove(int startX, int startY, int x, int y)
    {
        mPath.reset();
        mPath.moveTo(startX, startY);
        mPath.lineTo(x, y);

        int border = (int) mPaint.getStrokeWidth() * 2;
        mInvalidRect.set((int) startX - border, (int) startY - border, (int) startX + border, (int) startY + border);
        mInvalidRect.union((int) x - border, (int) y - border, (int) x + border, (int) y + border);
        mView.invalidate(mInvalidRect);
    }

    public void drawShape(Canvas canvas)
    {
        canvas.drawPath(mPath, mPaint);
    }

}
