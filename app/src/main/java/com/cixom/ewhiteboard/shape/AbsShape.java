//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.shape;

import android.graphics.Paint;

import com.cixom.ewhiteboard.Constants;
import com.cixom.ewhiteboard.HandwriterView;

public abstract class AbsShape implements IShape
{
    protected HandwriterView mView;
    protected Paint mPaint;

    public AbsShape(HandwriterView view, int model)
    {
        mView = view;
        // 去锯齿
        mPaint = new Paint();
        // 去锯齿
        mPaint.setAntiAlias(true);
        // 设置paint的颜色
        mPaint.setColor(model == HandwriterView.MODEL_ERASE ? Constants.BACK_COLOR : view.getPenColor());
        // 设置paint的 style 为STROKE：空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置paint的外框宽度
        mPaint.setStrokeWidth(model == HandwriterView.MODEL_ERASE ? view.getEraserWidth() : view.getPenWidth());
        // 获取跟清晰的图像采样
        mPaint.setDither(true);
        // 接合处为圆弧
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 画笔样式圆形
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }
}
